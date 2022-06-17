package com.itemis.fluffyj.memory;

import static com.itemis.fluffyj.memory.FluffyMemory.pointer;
import static com.itemis.fluffyj.memory.FluffyMemory.segment;
import static com.itemis.fluffyj.memory.FluffyMemory.wrap;
import static com.itemis.fluffyj.memory.NativeMethodHandle.fromCStdLib;
import static java.util.Arrays.sort;
import static jdk.incubator.foreign.CLinker.C_INT;
import static jdk.incubator.foreign.CLinker.C_POINTER;
import static jdk.incubator.foreign.CLinker.systemLookup;
import static jdk.incubator.foreign.CLinker.toCString;
import static org.apache.commons.lang3.ArrayUtils.toObject;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.itemis.fluffyj.exceptions.ThrowablePrettyfier;
import com.itemis.fluffyj.memory.error.FluffyMemoryException;
import com.itemis.fluffyj.memory.internal.PointerOfString;
import com.itemis.fluffyj.memory.internal.StringSegment;
import com.itemis.fluffyj.memory.internal.impl.CDataTypeConverter;
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
                .fromCStdLib()
                .returnType(long.class)
                .func("strlen")
                .args(C_POINTER);

        var expectedCause = new WrongMethodTypeException("cannot convert MethodHandle(MemoryAddress)long to ()Object");
        assertThatThrownBy(() -> underTest.call())
            .isInstanceOf(FluffyMemoryException.class)
            .hasMessage("Calling native code failed: " + ThrowablePrettyfier.pretty(expectedCause))
            .hasCause(expectedCause);
    }

    @Test
    void handle_constrcution_via_shortcut_works() {
        var testStr = "testString";
        var ptr = segment().of(testStr).allocate(scope).address();

        assertThat(strlen_shortcut_construction(ptr)).isEqualTo(testStr.length());
    }

    @Test
    void test_qsort() {
        var primitiveBuf = new byte[new Random().nextInt(MAX_RND_STR_LENGTH) + 1];
        new Random().nextBytes(primitiveBuf);
        var buf = toObject(primitiveBuf);

        var expectedResult = toObject(primitiveBuf);
        sort(expectedResult);

        var bufSeg = segment().ofArray(buf).allocate();
        var qsort = fromCStdLib()
            .noReturnType()
            .func("qsort")
            .args(C_POINTER, C_INT, C_INT, C_POINTER);
        var autoCompar = createComparPointerAuto();
        var manualCompar = createComparPointerManual();

        qsort.call(bufSeg.address(), buf.length, 1, autoCompar);
        Byte[] actualResult = bufSeg.getValue();
        assertThat(actualResult).isEqualTo(expectedResult);

        qsort.call(bufSeg.address(), buf.length, 1, manualCompar);
        actualResult = bufSeg.getValue();
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    private MemoryAddress createComparPointerAuto() {
        return pointer()
            .toCFunc("qsort_compar")
            .of(this)
            .autoBindTo(scope);
    }

    private MemoryAddress createComparPointerManual() {
        return pointer()
            .toFunc("qsort_compar")
            .ofType(this)
            .withTypeConverter(new CDataTypeConverter())
            .withArgs(C_POINTER, C_POINTER)
            .andReturnType(C_INT)
            .bindTo(scope);
    }

    int qsort_compar(MemoryAddress left, MemoryAddress right) {
        var leftByte = wrap(left.asSegment(1, scope)).as(Byte.class).getValue();
        var rightByte = wrap(right.asSegment(1, scope)).as(Byte.class).getValue();
        var result = 0;
        if (leftByte < rightByte) {
            result = -1;
        } else if (leftByte > rightByte) {
            result = 1;
        }
        return result;
    }

    private long strlen_shortcut_construction(MemoryAddress pointerToString) {
        var underTest =
            NativeMethodHandle
                .fromCStdLib()
                .returnType(long.class)
                .func("strlen")
                .args(C_POINTER);

        return underTest.call(pointerToString);
    }

    private long strlen(MemoryAddress pointerToString) {
        var underTest =
            NativeMethodHandle
                .fromLib(systemLookup())
                .withLinker((symbol, srcFuncType, targetMethodType) -> CLinker.getInstance().downcallHandle(symbol, targetMethodType, srcFuncType))
                .withTypeConverter(new CDataTypeConverter())
                .returnType(long.class)
                .func("strlen")
                .args(C_POINTER);

        return underTest.call(pointerToString);
    }
}
