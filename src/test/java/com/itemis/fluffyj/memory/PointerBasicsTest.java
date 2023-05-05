package com.itemis.fluffyj.memory;

import static com.itemis.fluffyj.memory.FluffyMemory.pointer;
import static com.itemis.fluffyj.tests.FluffyTestHelper.assertNullArgNotAccepted;
import static java.lang.foreign.SegmentAllocator.nativeAllocator;
import static java.lang.foreign.SegmentScope.global;
import static java.lang.foreign.ValueLayout.JAVA_BYTE;
import static java.lang.foreign.ValueLayout.JAVA_INT;
import static java.lang.foreign.ValueLayout.JAVA_LONG;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.itemis.fluffyj.memory.api.FluffyPointer;
import com.itemis.fluffyj.memory.api.FluffyScalarSegment;
import com.itemis.fluffyj.memory.api.FluffyVectorSegment;
import com.itemis.fluffyj.memory.error.FluffyMemoryException;
import com.itemis.fluffyj.memory.tests.MemoryScopeEnabledTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.foreign.MemorySegment;
import java.util.stream.Stream;

class PointerBasicsTest extends MemoryScopeEnabledTest {

    @Test
    void allocate_scalar_pointer_with_unknown_type_yields_exception() {
        assertThatThrownBy(() -> pointer().to(0L).as(MyType.class).allocate())
            .isInstanceOf(FluffyMemoryException.class)
            .hasMessage("Cannot allocate scalar pointer of unknown type: " + MyType.class.getCanonicalName());
    }

    @Test
    void allocate_scalar_pointer_and_scope_with_unknown_type_yields_exception() {
        assertThatThrownBy(() -> pointer().to(0L).as(MyType.class).allocate(scope))
            .isInstanceOf(FluffyMemoryException.class)
            .hasMessage("Cannot allocate scalar pointer of unknown type: " + MyType.class.getCanonicalName());
    }

    @Test
    void allocate_vector_pointer_with_unknown_type_yields_exception() {
        assertThatThrownBy(() -> pointer().to(0L).asArray(1).of(MyType[].class).allocate())
            .isInstanceOf(FluffyMemoryException.class)
            .hasMessage("Cannot allocate vector pointer of unknown type: " + MyType[].class.getCanonicalName());
    }

    @Test
    void allocate_vector_pointer_and_scope_with_unknown_type_yields_exception() {
        assertThatThrownBy(() -> pointer().to(0L).asArray(1).of(MyType[].class).allocate(scope))
            .isInstanceOf(FluffyMemoryException.class)
            .hasMessage("Cannot allocate vector pointer of unknown type: " + MyType[].class.getCanonicalName());
    }

    @Test
    void to_null_seg_yields_npe() {
        var underTest = new FluffyMemoryPointerBuilder();
        assertNullArgNotAccepted(() -> underTest.to((FluffyScalarSegment<?>) null), "toHere");
    }

    @Test
    void to_null_array_seg_yields_npe() {
        var underTest = new FluffyMemoryPointerBuilder();
        assertNullArgNotAccepted(() -> underTest.toArray((FluffyVectorSegment<?>) null), "toHere");
    }

    @Test
    void can_create_empty_pointer() {
        var result = pointer().allocate();
        assertThat(result).isInstanceOf(FluffyPointer.class);
    }

    @Test
    void can_create_scoped_empty_pointer() {
        var result = pointer().allocate(scope);
        assertThat(result).isInstanceOf(FluffyPointer.class);
    }

    @Test
    void empty_pointer_creation_does_not_accept_null_scope() {
        assertNullArgNotAccepted(() -> pointer().allocate(null), "scope");
    }

    @ParameterizedTest
    @MethodSource("typeMappings")
    void dereference_should_support_known_types(MemorySegment dataSeg, Class<?> inputType,
            Class<?> expectedOutputType) {
        var actualOutputType = FluffyMemory.dereference(dataSeg).as(inputType);

        assertThat(actualOutputType.getClass()).isEqualTo(expectedOutputType);
    }

    private static Stream<Arguments> typeMappings() {
        var longSeg = nativeAllocator(global()).allocate(JAVA_LONG, 123L);
        var intSeg = nativeAllocator(global()).allocate(JAVA_INT, 123);
        var byteSeg = nativeAllocator(global()).allocate(JAVA_BYTE, (byte) 123);
        var stringSeg = nativeAllocator(global()).allocateUtf8String("123");

        return Stream.of(
            Arguments.of(longSeg, long.class, Long.class),
            Arguments.of(longSeg, Long.class, Long.class),
            Arguments.of(intSeg, int.class, Integer.class),
            Arguments.of(intSeg, Integer.class, Integer.class),
            Arguments.of(byteSeg, byte.class, Byte.class),
            Arguments.of(byteSeg, Byte.class, Byte.class),
            Arguments.of(stringSeg, String.class, String.class));
    }

    private static final class MyType {
    }
}
