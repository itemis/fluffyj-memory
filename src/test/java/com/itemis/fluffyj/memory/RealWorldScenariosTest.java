package com.itemis.fluffyj.memory;

import static com.itemis.fluffyj.memory.FluffyMemory.pointer;
import static com.itemis.fluffyj.memory.FluffyMemory.segment;
import static com.itemis.fluffyj.memory.FluffyMemory.wrap;
import static jdk.incubator.foreign.CLinker.C_POINTER;
import static jdk.incubator.foreign.CLinker.systemLookup;
import static jdk.incubator.foreign.CLinker.toCString;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.itemis.fluffyj.exceptions.ThrowablePrettyfier;
import com.itemis.fluffyj.memory.error.FluffyMemoryException;
import com.itemis.fluffyj.memory.internal.PointerOfString;
import com.itemis.fluffyj.memory.internal.StringSegment;
import com.itemis.fluffyj.memory.tests.MemoryScopedTest;

import org.junit.jupiter.api.Test;

import java.lang.invoke.WrongMethodTypeException;
import java.util.Random;

import jdk.incubator.foreign.CLinker;
import jdk.incubator.foreign.MemoryAddress;

public class RealWorldScenariosTest extends MemoryScopedTest {

    private static final int MAX_RND_STR_LENGTH = 100;

    @Test
    void can_call_strlen_with_address_of_string_seg() {
        var expectedStringLength = new Random().nextInt(MAX_RND_STR_LENGTH);
        var rndStr = randomAlphanumeric(expectedStringLength);

        var cStr = new StringSegment(rndStr, scope);
        var ptrCStr = new PointerOfString(cStr.address(), scope);

        assertThat(strlen(cStr.address())).isEqualTo(expectedStringLength);
        assertThat(strlen(ptrCStr.getValue())).isEqualTo(expectedStringLength);
    }

    @Test
    void can_call_strlen_with_results_of_fluffy_memory_builder() {
        var expectedStringLength = new Random().nextInt(MAX_RND_STR_LENGTH);
        var rndStr = randomAlphanumeric(expectedStringLength);

        var cStr = segment().of(rndStr).allocate(scope);
        var ptrCStr = pointer().to(cStr).allocate(scope);
        var wrappedStrSeg = wrap(toCString(rndStr, scope)).as(String.class);

        assertThat(strlen(cStr.address())).isEqualTo(expectedStringLength);
        assertThat(strlen(ptrCStr.getValue())).isEqualTo(expectedStringLength);
        assertThat(strlen(wrappedStrSeg.address())).isEqualTo(expectedStringLength);
    }

    @Test
    void call_throws_exception_when_error_is_encountered() {
        var underTest =
            NativeMethodHandle
                .ofLib(systemLookup())
                .returnType(long.class)
                .func("strlen")
                .args(C_POINTER)
                .create(CLinker.getInstance());

        var expectedCause = new WrongMethodTypeException("cannot convert MethodHandle(MemoryAddress)long to ()Object");
        assertThatThrownBy(() -> underTest.call())
            .isInstanceOf(FluffyMemoryException.class)
            .hasMessage("Calling native code failed: " + ThrowablePrettyfier.pretty(expectedCause))
            .hasCause(expectedCause);
    }

    private long strlen(MemoryAddress pointerToString) {
        var underTest =
            NativeMethodHandle
                .ofLib(systemLookup())
                .returnType(long.class)
                .func("strlen")
                .args(C_POINTER)
                .create(CLinker.getInstance());

        return underTest.call(pointerToString);
    }
}
