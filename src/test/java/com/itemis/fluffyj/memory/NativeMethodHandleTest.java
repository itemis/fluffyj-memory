package com.itemis.fluffyj.memory;

import static com.itemis.fluffyj.tests.FluffyTestHelper.assertFinal;
import static com.itemis.fluffyj.tests.FluffyTestHelper.assertNullArgNotAccepted;
import static java.util.Optional.empty;
import static jdk.incubator.foreign.CLinker.C_INT;
import static jdk.incubator.foreign.CLinker.C_POINTER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.itemis.fluffyj.memory.NativeMethodHandle.ArgsStage;
import com.itemis.fluffyj.memory.NativeMethodHandle.FuncStage;
import com.itemis.fluffyj.memory.NativeMethodHandle.LinkerStage;
import com.itemis.fluffyj.memory.NativeMethodHandle.ReturnTypeStage;
import com.itemis.fluffyj.memory.api.FluffyMemoryLinker;
import com.itemis.fluffyj.memory.api.FluffyMemoryTypeConverter;
import com.itemis.fluffyj.memory.error.FluffyMemoryException;
import com.itemis.fluffyj.memory.internal.impl.CDataTypeConverter;
import com.itemis.fluffyj.memory.tests.MemoryScopedTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.invoke.MethodHandle;
import java.util.Optional;

import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemoryLayout;
import jdk.incubator.foreign.SymbolLookup;

public class NativeMethodHandleTest extends MemoryScopedTest {

    private MemoryAddress symbolMock;
    private SymbolLookup libMock;
    private FluffyMemoryLinker linkerMock;
    private FluffyMemoryTypeConverter convSpy;
    private MethodHandle methodHandleMock;

    @BeforeEach
    void setUp() {
        symbolMock = MemoryAddress.NULL;
        linkerMock = mock(FluffyMemoryLinker.class);
        libMock = mock(SymbolLookup.class);
        convSpy = spy(new CDataTypeConverter());
        methodHandleMock = mock(MethodHandle.class);
        when(linkerMock.link(any(), any(), any())).thenReturn(methodHandleMock);
    }

    @Test
    void is_final() {
        assertFinal(NativeMethodHandle.class);
    }

    @Test
    void test_correct_stage_order() {
        var knownSymbol = "strlen";
        addKnownSymbol(knownSymbol);

        var firstStage = NativeMethodHandle.fromLib(libMock);
        assertThat(firstStage).isInstanceOf(ReturnTypeStage.class);

        var secondStage = firstStage.withLinker(linkerMock);
        assertThat(secondStage).isInstanceOf(LinkerStage.class);

        var thirdStage = secondStage.withTypeConverter(convSpy);
        assertThat(thirdStage).isInstanceOf(FuncStage.class);

        var fourthStage = thirdStage.returnType(Integer.class);
        assertThat(fourthStage).isInstanceOf(FuncStage.class);

        var otherFourthStage = thirdStage.noReturnType();
        assertThat(otherFourthStage).isInstanceOf(FuncStage.class);

        var fifthStage = fourthStage.func(knownSymbol);
        assertThat(fifthStage).isInstanceOf(ArgsStage.class);

        var sixthStage = fifthStage.args(C_POINTER);
        assertThat(sixthStage).isInstanceOf(NativeMethodHandle.class);

        var otherSixthStage = fifthStage.noArgs();
        assertThat(otherSixthStage).isInstanceOf(NativeMethodHandle.class);
    }

    @Test
    void does_not_accept_null_lib() {
        assertNullArgNotAccepted(() -> NativeMethodHandle.fromLib(null), "lib");
    }

    @Test
    void does_not_accept_null_linker() {
        assertNullArgNotAccepted(() -> NativeMethodHandle.fromLib(libMock).withLinker(null), "linker");
    }

    @Test
    void does_not_accept_null_converter() {
        assertNullArgNotAccepted(() -> NativeMethodHandle.fromLib(libMock).withLinker(linkerMock).withTypeConverter(null), "conv");
    }

    @Test
    void does_not_accept_null_return_type() {
        assertNullArgNotAccepted(() -> NativeMethodHandle.fromLib(libMock).withLinker(linkerMock).withTypeConverter(convSpy).returnType(null), "returnType");
    }

    @Test
    void does_not_accept_null_func() {
        assertNullArgNotAccepted(() -> NativeMethodHandle.fromLib(libMock).withLinker(linkerMock).withTypeConverter(convSpy).returnType(Long.class).func(null),
            "funcName");
    }

    @Test
    void func_loads_symbol_from_lib() {
        var expectedSymbolName = "expectedSymbolName";
        addKnownSymbol(expectedSymbolName);
        NativeMethodHandle.fromLib(libMock).withLinker(linkerMock).withTypeConverter(convSpy).returnType(Long.class).func(expectedSymbolName);

        verify(libMock, times(1)).lookup(expectedSymbolName);
    }

    @Test
    void unknown_func_name_yields_exception() {
        var unknownSymbol = "unknownSymbol";
        removeSymbol(unknownSymbol);

        assertThatThrownBy(
            () -> NativeMethodHandle.fromLib(libMock).withLinker(linkerMock).withTypeConverter(convSpy).returnType(long.class).func(unknownSymbol))
                .isInstanceOf(FluffyMemoryException.class)
                .hasMessage("Could not find symbol '" + unknownSymbol + "' in library '" + libMock.toString() + "'.");
    }

    @Test
    void returnType_calls_typeConverter() {
        var returnType = long.class;
        NativeMethodHandle.fromLib(libMock).withLinker(linkerMock).withTypeConverter(convSpy).returnType(returnType);

        verify(convSpy, times(1)).getNativeType(returnType);
    }

    @Test
    void args_calls_typeConverter() {
        var args = new MemoryLayout[] {C_POINTER, C_INT};
        var knownSymbol = "knownSymbol";
        addKnownSymbol(knownSymbol);
        NativeMethodHandle.fromLib(libMock).withLinker(linkerMock).withTypeConverter(convSpy).returnType(long.class).func(knownSymbol).args(args);

        verify(convSpy, times(1)).getJavaTypes(args);
    }

    @Test
    void cStdLib_shortcut_returns_correct_stage() {
        var resultStage = NativeMethodHandle.fromCStdLib();
        assertThat(resultStage).isInstanceOf(ReturnTypeStage.class);
    }

    @Test
    void cLib_shortcut_returns_correct_stage() {
        var resultStage = NativeMethodHandle.fromCLib(libMock);
        assertThat(resultStage).isInstanceOf(ReturnTypeStage.class);
    }

    @Test
    void cStdLib_shortcut_does_not_accept_null() {
        assertNullArgNotAccepted(() -> NativeMethodHandle.fromCLib(null), "lib");
    }

    @Test
    void no_return_type_should_not_invoke_conv() {
        NativeMethodHandle.fromLib(libMock).withLinker(linkerMock).withTypeConverter(convSpy).noReturnType();

        verify(convSpy, never()).getNativeType(any());
    }

    @Test
    void test_no_return_type() {
        var knownSymbol = "knownSymbol";
        addKnownSymbol(knownSymbol);

        var handle = NativeMethodHandle.fromLib(libMock).withLinker(linkerMock).withTypeConverter(convSpy).noReturnType().func(knownSymbol).noArgs();

        assertThat(handle).isNotNull();
    }

    @Test
    void test_Void_return_type_no_args() {
        var knownSymbol = "knownSymbol";
        addKnownSymbol(knownSymbol);

        var handle = NativeMethodHandle.fromLib(libMock).withLinker(linkerMock).withTypeConverter(convSpy).returnType(Void.class).func(knownSymbol).args();

        assertThat(handle).isNotNull();
    }

    @Test
    void test_void_return_type_no_args() {
        var knownSymbol = "knownSymbol";
        addKnownSymbol(knownSymbol);

        var handle = NativeMethodHandle.fromLib(libMock).withLinker(linkerMock).withTypeConverter(convSpy).returnType(void.class).func(knownSymbol).args();

        assertThat(handle).isNotNull();
    }

    private void addKnownSymbol(String symbolName) {
        when(libMock.lookup(symbolName)).thenReturn(Optional.of(symbolMock));
    }

    private void removeSymbol(String symbolName) {
        when(libMock.lookup(symbolName)).thenReturn(empty());
    }
}
