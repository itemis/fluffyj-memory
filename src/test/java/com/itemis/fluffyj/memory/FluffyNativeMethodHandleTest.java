package com.itemis.fluffyj.memory;

import static com.itemis.fluffyj.tests.FluffyTestHelper.assertFinal;
import static com.itemis.fluffyj.tests.FluffyTestHelper.assertNullArgNotAccepted;
import static java.lang.foreign.MemorySegment.allocateNative;
import static java.lang.foreign.ValueLayout.ADDRESS;
import static java.lang.invoke.MethodType.methodType;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.itemis.fluffyj.memory.FluffyNativeMethodHandle.ArgsStage;
import com.itemis.fluffyj.memory.FluffyNativeMethodHandle.FuncStage;
import com.itemis.fluffyj.memory.FluffyNativeMethodHandle.LinkerStage;
import com.itemis.fluffyj.memory.FluffyNativeMethodHandle.ReturnTypeStage;
import com.itemis.fluffyj.memory.api.FluffyMemoryLinker;
import com.itemis.fluffyj.memory.api.FluffyMemoryTypeConverter;
import com.itemis.fluffyj.memory.error.FluffyMemoryException;
import com.itemis.fluffyj.memory.internal.impl.CDataTypeConverter;
import com.itemis.fluffyj.memory.tests.MemoryScopeEnabledTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.foreign.SymbolLookup;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.Optional;
import java.util.function.Supplier;

public class FluffyNativeMethodHandleTest extends MemoryScopeEnabledTest {

    private SymbolLookup libMock;
    private FluffyMemoryLinker linkerMock;
    private FluffyMemoryTypeConverter convSpy;
    private MethodHandle realMethodHandle;

    @BeforeEach
    void setUp() throws Exception {
        linkerMock = mock(FluffyMemoryLinker.class);
        libMock = mock(SymbolLookup.class);
        convSpy = spy(new CDataTypeConverter());

        var publicLookup = MethodHandles.publicLookup();
        var methodType = methodType(int.class);
        realMethodHandle = publicLookup.findVirtual(String.class, "length", methodType);

        when(linkerMock.link(any(), any())).thenReturn(realMethodHandle);
    }

    @Test
    void is_final() {
        assertFinal(FluffyNativeMethodHandle.class);
    }

    @Test
    void test_correct_stage_order() {
        var knownSymbol = "strlen";
        addKnownSymbol(knownSymbol);

        var firstStage = FluffyNativeMethodHandle.fromLib(libMock);
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

        var sixthStage = fifthStage.args(ADDRESS);
        assertThat(sixthStage).isInstanceOf(FluffyNativeMethodHandle.class);

        var otherSixthStage = fifthStage.noArgs();
        assertThat(otherSixthStage).isInstanceOf(FluffyNativeMethodHandle.class);
    }

    @Test
    void does_not_accept_null_lib() {
        assertNullArgNotAccepted(() -> FluffyNativeMethodHandle.fromLib(null), "lib");
    }

    @Test
    void does_not_accept_null_linker() {
        assertNullArgNotAccepted(() -> FluffyNativeMethodHandle.fromLib(libMock).withLinker(null), "linker");
    }

    @Test
    void does_not_accept_null_converter() {
        assertNullArgNotAccepted(
            () -> FluffyNativeMethodHandle.fromLib(libMock).withLinker(linkerMock).withTypeConverter(null), "conv");
    }

    @Test
    void does_not_accept_null_return_type() {
        assertNullArgNotAccepted(() -> FluffyNativeMethodHandle.fromLib(libMock).withLinker(linkerMock)
            .withTypeConverter(convSpy).returnType(null), "returnType");
    }

    @Test
    void does_not_accept_null_func() {
        assertNullArgNotAccepted(
            () -> FluffyNativeMethodHandle.fromLib(libMock).withLinker(linkerMock).withTypeConverter(convSpy)
                .returnType(Long.class).func(null),
            "funcName");
    }

    @Test
    void func_loads_symbol_from_lib() {
        var expectedSymbolName = "expectedSymbolName";
        addKnownSymbol(expectedSymbolName);
        FluffyNativeMethodHandle.fromLib(libMock).withLinker(linkerMock).withTypeConverter(convSpy)
            .returnType(Long.class)
            .func(expectedSymbolName);

        verify(libMock, times(1)).find(expectedSymbolName);
    }

    @Test
    void unknown_func_name_yields_exception() {
        var unknownSymbol = "unknownSymbol";
        removeSymbol(unknownSymbol);

        assertThatThrownBy(
            () -> FluffyNativeMethodHandle.fromLib(libMock).withLinker(linkerMock).withTypeConverter(convSpy)
                .returnType(long.class).func(unknownSymbol))
                    .isInstanceOf(FluffyMemoryException.class)
                    .hasMessage(
                        "Could not find symbol '" + unknownSymbol + "' in library '" + libMock.toString() + "'.");
    }

    @Test
    void returnType_calls_typeConverter() {
        var returnType = long.class;
        FluffyNativeMethodHandle.fromLib(libMock).withLinker(linkerMock).withTypeConverter(convSpy)
            .returnType(returnType);

        verify(convSpy, times(1)).getNativeType(returnType);
    }

    @Test
    void cStdLib_shortcut_returns_correct_stage() {
        var resultStage = FluffyNativeMethodHandle.fromCStdLib();
        assertThat(resultStage).isInstanceOf(ReturnTypeStage.class);
    }

    @Test
    void cLib_shortcut_returns_correct_stage() {
        var resultStage = FluffyNativeMethodHandle.fromCLib(libMock);
        assertThat(resultStage).isInstanceOf(ReturnTypeStage.class);
    }

    @Test
    void cStdLib_shortcut_does_not_accept_null() {
        assertNullArgNotAccepted(() -> FluffyNativeMethodHandle.fromCLib(null), "lib");
    }

    @Test
    void no_return_type_should_not_invoke_conv() {
        FluffyNativeMethodHandle.fromLib(libMock).withLinker(linkerMock).withTypeConverter(convSpy).noReturnType();

        verify(convSpy, never()).getNativeType(any());
    }

    @Test
    void test_no_return_type() {
        var knownSymbol = "knownSymbol";
        addKnownSymbol(knownSymbol);

        var handle = FluffyNativeMethodHandle.fromLib(libMock).withLinker(linkerMock).withTypeConverter(convSpy)
            .noReturnType().func(knownSymbol).noArgs();

        assertThat(handle).isNotNull();
    }

    @Test
    void test_Void_return_type_no_args() {
        var knownSymbol = "knownSymbol";
        addKnownSymbol(knownSymbol);

        var handle = FluffyNativeMethodHandle.fromLib(libMock).withLinker(linkerMock).withTypeConverter(convSpy)
            .returnType(Void.class).func(knownSymbol).args();

        assertThat(handle).isNotNull();
    }

    @Test
    void test_void_return_type_no_args() {
        var knownSymbol = "knownSymbol";
        addKnownSymbol(knownSymbol);

        var handle = FluffyNativeMethodHandle.fromLib(libMock).withLinker(linkerMock).withTypeConverter(convSpy)
            .returnType(void.class).func(knownSymbol).args();

        assertThat(handle).isNotNull();
    }

    private void addKnownSymbol(String symbolName) {
        var seg = allocateNative(1, scope);
        when(libMock.find(symbolName)).thenReturn(Optional.of(seg));
    }

    private void removeSymbol(String symbolName) {
        // The mock statement is required in order for eclipse to accept the next statement.
        // There seems to be a Java19 related bug wrt Mockito.when
        Mockito.mock(Supplier.class);
        when(libMock.find(symbolName)).thenReturn(Optional.empty());
    }
}
