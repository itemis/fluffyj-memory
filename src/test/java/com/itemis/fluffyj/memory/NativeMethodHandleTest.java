package com.itemis.fluffyj.memory;

import static com.itemis.fluffyj.tests.FluffyTestHelper.assertFinal;
import static com.itemis.fluffyj.tests.FluffyTestHelper.assertNullArgNotAccepted;
import static java.util.Optional.empty;
import static jdk.incubator.foreign.CLinker.C_POINTER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.itemis.fluffyj.memory.NativeMethodHandle.ArgsStage;
import com.itemis.fluffyj.memory.NativeMethodHandle.CreateStage;
import com.itemis.fluffyj.memory.NativeMethodHandle.FuncStage;
import com.itemis.fluffyj.memory.NativeMethodHandle.ReturnTypeStage;
import com.itemis.fluffyj.memory.error.FluffyMemoryException;
import com.itemis.fluffyj.memory.tests.MemoryScopedTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.SymbolLookup;

public class NativeMethodHandleTest extends MemoryScopedTest {

    private MemoryAddress symbolMock;
    private SymbolLookup libMock;

    @BeforeEach
    void setUp() {
        symbolMock = MemoryAddress.NULL;
        libMock = mock(SymbolLookup.class);
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

        var secondStage = firstStage.returnType(String.class);
        assertThat(secondStage).isInstanceOf(FuncStage.class);

        var otherSecondStage = firstStage.noReturnType();
        assertThat(otherSecondStage).isInstanceOf(FuncStage.class);

        var thirdStage = secondStage.func(knownSymbol);
        assertThat(thirdStage).isInstanceOf(ArgsStage.class);

        var fourthStage = thirdStage.args(C_POINTER);
        assertThat(fourthStage).isInstanceOf(CreateStage.class);

        var otherFourthStage = thirdStage.noArgs();
        assertThat(otherFourthStage).isInstanceOf(CreateStage.class);
    }

    @Test
    void does_not_accept_null_lib() {
        assertNullArgNotAccepted(() -> NativeMethodHandle.ofLib(null), "lib");
    }

    @Test
    void does_not_accept_null_return_type() {
        assertNullArgNotAccepted(() -> NativeMethodHandle.ofLib(libMock).returnType(null), "returnType");
    }

    @Test
    void does_not_accept_null_func() {
        assertNullArgNotAccepted(() -> NativeMethodHandle.ofLib(libMock).returnType(Long.class).func(null), "funcName");
    }

    @Test
    void func_loads_symbol_from_lib() {
        var expectedSymbolName = "expectedSymbolName";
        addKnownSymbol(expectedSymbolName);
        NativeMethodHandle.ofLib(libMock).returnType(Long.class).func(expectedSymbolName);

        verify(libMock, times(1)).lookup(expectedSymbolName);
    }

    @Test
    void unknown_func_name_yields_exception() {
        var unknownSymbol = "unknownSymbol";
        removeSymbol(unknownSymbol);

        assertThatThrownBy(() -> NativeMethodHandle.ofLib(libMock).returnType(long.class).func(unknownSymbol))
            .isInstanceOf(FluffyMemoryException.class)
            .hasMessage("Could not find symbol '" + unknownSymbol + "' in library '" + libMock.toString() + "'.");
    }

    private void addKnownSymbol(String symbolName) {
        when(libMock.lookup(symbolName)).thenReturn(Optional.of(symbolMock));
    }

    private void removeSymbol(String symbolName) {
        when(libMock.lookup(symbolName)).thenReturn(empty());
    }
}
