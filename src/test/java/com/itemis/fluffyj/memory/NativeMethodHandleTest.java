package com.itemis.fluffyj.memory;

import static com.itemis.fluffyj.tests.FluffyTestHelper.assertFinal;
import static com.itemis.fluffyj.tests.FluffyTestHelper.assertNullArgNotAccepted;
import static java.util.Optional.empty;
import static jdk.incubator.foreign.CLinker.C_INT;
import static jdk.incubator.foreign.CLinker.C_LONG;
import static jdk.incubator.foreign.CLinker.C_POINTER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
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
    private FluffyMemoryTypeConverter convMock;
    private MethodHandle methodHandleMock;

    @BeforeEach
    void setUp() {
        symbolMock = MemoryAddress.NULL;
        linkerMock = mock(FluffyMemoryLinker.class);
        libMock = mock(SymbolLookup.class);
        convMock = mock(FluffyMemoryTypeConverter.class);
        methodHandleMock = mock(MethodHandle.class);
        when(linkerMock.link(any(), any(), any())).thenReturn(methodHandleMock);
        when(convMock.getCType(any())).thenReturn(C_LONG);
        when(convMock.getJavaTypes(any())).thenReturn(new Class[] {long.class});
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

        var thirdStage = secondStage.withTypeConverter(convMock);
        assertThat(thirdStage).isInstanceOf(FuncStage.class);

        var fourthStage = thirdStage.returnType(String.class);
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
        assertNullArgNotAccepted(() -> NativeMethodHandle.fromLib(libMock).withLinker(linkerMock).withTypeConverter(convMock).returnType(null), "returnType");
    }

    @Test
    void does_not_accept_null_func() {
        assertNullArgNotAccepted(() -> NativeMethodHandle.fromLib(libMock).withLinker(linkerMock).withTypeConverter(convMock).returnType(Long.class).func(null),
            "funcName");
    }

    @Test
    void func_loads_symbol_from_lib() {
        var expectedSymbolName = "expectedSymbolName";
        addKnownSymbol(expectedSymbolName);
        NativeMethodHandle.fromLib(libMock).withLinker(linkerMock).withTypeConverter(convMock).returnType(Long.class).func(expectedSymbolName);

        verify(libMock, times(1)).lookup(expectedSymbolName);
    }

    @Test
    void unknown_func_name_yields_exception() {
        var unknownSymbol = "unknownSymbol";
        removeSymbol(unknownSymbol);

        assertThatThrownBy(
            () -> NativeMethodHandle.fromLib(libMock).withLinker(linkerMock).withTypeConverter(convMock).returnType(long.class).func(unknownSymbol))
                .isInstanceOf(FluffyMemoryException.class)
                .hasMessage("Could not find symbol '" + unknownSymbol + "' in library '" + libMock.toString() + "'.");
    }

    @Test
    void returnType_calls_typeConverter() {
        var returnType = long.class;
        NativeMethodHandle.fromLib(libMock).withLinker(linkerMock).withTypeConverter(convMock).returnType(returnType);

        verify(convMock, times(1)).getCType(returnType);
    }

    @Test
    void args_calls_typeConverter() {
        var args = new MemoryLayout[] {C_POINTER, C_INT};
        var knownSymbol = "knownSymbol";
        addKnownSymbol(knownSymbol);
        NativeMethodHandle.fromLib(libMock).withLinker(linkerMock).withTypeConverter(convMock).returnType(long.class).func(knownSymbol).args(args);

        verify(convMock, times(1)).getJavaTypes(args);
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

    private void addKnownSymbol(String symbolName) {
        when(libMock.lookup(symbolName)).thenReturn(Optional.of(symbolMock));
    }

    private void removeSymbol(String symbolName) {
        when(libMock.lookup(symbolName)).thenReturn(empty());
    }
}
