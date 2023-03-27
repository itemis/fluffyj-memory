package com.itemis.fluffyj.memory;

import static com.itemis.fluffyj.memory.FluffyMemory.pointer;
import static com.itemis.fluffyj.memory.FluffyMemory.segment;
import static com.itemis.fluffyj.memory.FluffyMemory.wrap;
import static com.itemis.fluffyj.memory.FluffyNativeMethodHandle.fromCStdLib;
import static java.lang.foreign.Linker.nativeLinker;
import static java.lang.foreign.SegmentAllocator.nativeAllocator;
import static java.lang.foreign.ValueLayout.ADDRESS;
import static java.lang.foreign.ValueLayout.JAVA_INT;
import static java.util.Arrays.sort;
import static org.apache.commons.lang3.ArrayUtils.toObject;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.itemis.fluffyj.exceptions.ThrowablePrettyfier;
import com.itemis.fluffyj.memory.error.FluffyMemoryException;
import com.itemis.fluffyj.memory.internal.PointerOfString;
import com.itemis.fluffyj.memory.internal.StringSegment;
import com.itemis.fluffyj.memory.internal.impl.CDataTypeConverter;
import com.itemis.fluffyj.memory.tests.MemoryScopeEnabledTest;

import org.junit.jupiter.api.Test;

import java.lang.foreign.MemorySegment;
import java.lang.invoke.WrongMethodTypeException;
import java.util.Random;

public class RealWorldScenariosTest extends MemoryScopeEnabledTest {

    private static final int MAX_RND_STR_LENGTH = 100;

    @Test
    void can_call_strlen_with_address_of_string_seg() {
        var expectedStringLength = new Random().nextInt(MAX_RND_STR_LENGTH);
        var rndStr = randomAlphanumeric(expectedStringLength);

        var cStr = new StringSegment(rndStr, scope);
        var ptrCStr = new PointerOfString(cStr.rawAddress(), scope);

        assertThat(strlen(cStr.address())).isEqualTo(expectedStringLength);
        assertThat(strlen(ptrCStr.getValue())).isEqualTo(expectedStringLength);
    }

    @Test
    void can_call_strlen_with_results_of_fluffy_memory_builder() {
        var expectedStringLength = new Random().nextInt(MAX_RND_STR_LENGTH);
        var rndStr = randomAlphanumeric(expectedStringLength);

        var cStr = segment().of(rndStr).allocate(scope);
        var ptrCStr = pointer().to(cStr).allocate(scope);
        var strSeg = nativeAllocator(scope).allocateUtf8String(rndStr);
        var wrappedStrSeg = wrap(strSeg).as(String.class);

        assertThat(strlen(cStr.address())).isEqualTo(expectedStringLength);
        assertThat(strlen(ptrCStr.getValue())).isEqualTo(expectedStringLength);
        assertThat(strlen(wrappedStrSeg.address())).isEqualTo(expectedStringLength);
    }

    @Test
    void call_throws_exception_when_error_is_encountered() {
        var underTest =
            FluffyNativeMethodHandle
                .fromCStdLib()
                .returnType(long.class)
                .func("strlen")
                .args(ADDRESS);

        var expectedCause = new WrongMethodTypeException("cannot convert MethodHandle(MemorySegment)long to ()Object");
        assertThatThrownBy(() -> underTest.call())
            .isInstanceOf(FluffyMemoryException.class)
            .hasMessage("Calling native code failed: " + ThrowablePrettyfier.pretty(expectedCause))
            .hasCause(expectedCause);
    }

    @Test
    void handle_construction_via_shortcut_works() {
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
            .args(ADDRESS, JAVA_INT, JAVA_INT, ADDRESS);
        var autoCompar = createComparPointerAuto();
        var manualCompar = createComparPointerManual();

        qsort.call(bufSeg.address(), buf.length, 1, autoCompar);
        var actualResult = bufSeg.getValue();
        assertThat(actualResult).isEqualTo(expectedResult);

        qsort.call(bufSeg.address(), buf.length, 1, manualCompar);
        actualResult = bufSeg.getValue();
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    private MemorySegment createComparPointerAuto() {
        return pointer()
            .toCFunc("qsort_compar")
            .of(this)
            .autoBindTo(scope);
    }

    private MemorySegment createComparPointerManual() {
        return pointer()
            .toFunc("qsort_compar")
            .ofType(this)
            .withTypeConverter(new CDataTypeConverter())
            .withArgs(ADDRESS, ADDRESS)
            .andReturnType(JAVA_INT)
            .bindTo(scope);
    }

    int qsort_compar(MemorySegment left, MemorySegment right) {
        var leftByte = pointer().to(left.address()).as(Byte.class).allocate(scope).dereference();
        var rightByte = pointer().to(right.address()).as(Byte.class).allocate(scope).dereference();
        var result = 0;
        if (leftByte < rightByte) {
            result = -1;
        } else if (leftByte > rightByte) {
            result = 1;
        }
        return result;
    }

    private long strlen_shortcut_construction(MemorySegment pointerToString) {
        var underTest =
            FluffyNativeMethodHandle
                .fromCStdLib()
                .returnType(long.class)
                .func("strlen")
                .args(ADDRESS);

        return underTest.call(pointerToString);
    }

    private long strlen(MemorySegment pointerToString) {
        var underTest =
            FluffyNativeMethodHandle
                .fromLib(nativeLinker().defaultLookup())
                .withLinker((symbol, srcFuncType) -> nativeLinker().downcallHandle(symbol, srcFuncType))
                .withTypeConverter(new CDataTypeConverter())
                .returnType(long.class)
                .func("strlen")
                .args(ADDRESS);

        return underTest.call(pointerToString);
    }
}
