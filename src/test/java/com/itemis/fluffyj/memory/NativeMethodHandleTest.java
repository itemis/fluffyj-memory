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
import com.itemis.fluffyj.memory.NativeMethodHandle.CreateStage;
import com.itemis.fluffyj.memory.NativeMethodHandle.FuncStage;
import com.itemis.fluffyj.memory.NativeMethodHandle.ReturnTypeStage;
import com.itemis.fluffyj.memory.api.FluffyMemoryTypeConverter;
import com.itemis.fluffyj.memory.error.FluffyMemoryException;
import com.itemis.fluffyj.memory.tests.MemoryScopedTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemoryLayout;
import jdk.incubator.foreign.SymbolLookup;

public class NativeMethodHandleTest extends MemoryScopedTest {

    private MemoryAddress symbolMock;
    private SymbolLookup libMock;
    private FluffyMemoryTypeConverter convMock;

    @BeforeEach
    void setUp() {
        symbolMock = MemoryAddress.NULL;
        libMock = mock(SymbolLookup.class);
        convMock = mock(FluffyMemoryTypeConverter.class);
        when(convMock.getCType(any())).thenReturn(C_LONG);
        when(convMock.getJavaTypes(Mockito.any())).thenReturn(new Class[] {long.class});
    }

    @Test
    void is_final() {
        assertFinal(NativeMethodHandle.class);
    }

    @Test
    void test_correct_stage_order() {
        String knownSymbol = "strlen";
        addKnownSymbol(knownSymbol);

        var firstStage = NativeMethodHandle.ofLib(libMock);
        assertThat(firstStage).isInstanceOf(ReturnTypeStage.class);

        var secondStage = firstStage.withTypeConverter(convMock);
        assertThat(secondStage).isInstanceOf(FuncStage.class);

        var thirdStage = secondStage.returnType(String.class);
        assertThat(secondStage).isInstanceOf(FuncStage.class);

        var otherThirdStage = secondStage.noReturnType();
        assertThat(otherThirdStage).isInstanceOf(FuncStage.class);

        var fourthStage = thirdStage.func(knownSymbol);
        assertThat(fourthStage).isInstanceOf(ArgsStage.class);

        var fifthStage = fourthStage.args(C_POINTER);
        assertThat(fifthStage).isInstanceOf(CreateStage.class);

        var otherFifthStage = fourthStage.noArgs();
        assertThat(otherFifthStage).isInstanceOf(CreateStage.class);
    }

    @Test
    void does_not_accept_null_lib() {
        assertNullArgNotAccepted(() -> NativeMethodHandle.ofLib(null), "lib");
    }

    @Test
    void does_not_accept_null_converter() {
        assertNullArgNotAccepted(() -> NativeMethodHandle.ofLib(libMock).withTypeConverter(null), "conv");
    }

    @Test
    void does_not_accept_null_return_type() {
        assertNullArgNotAccepted(() -> NativeMethodHandle.ofLib(libMock).withTypeConverter(convMock).returnType(null), "returnType");
    }

    @Test
    void does_not_accept_null_func() {
        assertNullArgNotAccepted(() -> NativeMethodHandle.ofLib(libMock).withTypeConverter(convMock).returnType(Long.class).func(null), "funcName");
    }

    @Test
    void func_loads_symbol_from_lib() {
        var expectedSymbolName = "expectedSymbolName";
        addKnownSymbol(expectedSymbolName);
        NativeMethodHandle.ofLib(libMock).withTypeConverter(convMock).returnType(Long.class).func(expectedSymbolName);

        verify(libMock, times(1)).lookup(expectedSymbolName);
    }

    @Test
    void unknown_func_name_yields_exception() {
        var unknownSymbol = "unknownSymbol";
        removeSymbol(unknownSymbol);

        assertThatThrownBy(() -> NativeMethodHandle.ofLib(libMock).withTypeConverter(convMock).returnType(long.class).func(unknownSymbol))
            .isInstanceOf(FluffyMemoryException.class)
            .hasMessage("Could not find symbol '" + unknownSymbol + "' in library '" + libMock.toString() + "'.");
    }

    @Test
    void returnType_calls_typeConverter() {
        Class<Long> returnType = long.class;
        NativeMethodHandle.ofLib(libMock).withTypeConverter(convMock).returnType(returnType);

        verify(convMock, times(1)).getCType(returnType);
    }

    @Test
    void args_calls_typeConverter() {
        var args = new MemoryLayout[] {C_POINTER, C_INT};
        var knownSymbol = "knownSymbol";
        addKnownSymbol(knownSymbol);
        NativeMethodHandle.ofLib(libMock).withTypeConverter(convMock).returnType(long.class).func(knownSymbol).args(args);

        verify(convMock, times(1)).getJavaTypes(args);
    }


    private void addKnownSymbol(String symbolName) {
        when(libMock.lookup(symbolName)).thenReturn(Optional.of(symbolMock));
    }

    private void removeSymbol(String symbolName) {
        when(libMock.lookup(symbolName)).thenReturn(empty());
    }
}
