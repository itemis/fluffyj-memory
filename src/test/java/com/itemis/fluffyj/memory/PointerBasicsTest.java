package com.itemis.fluffyj.memory;

import static com.itemis.fluffyj.memory.FluffyMemory.pointer;
import static com.itemis.fluffyj.tests.FluffyTestHelper.assertNullArgNotAccepted;
import static java.lang.foreign.Arena.global;
import static java.lang.foreign.ValueLayout.JAVA_BYTE;
import static java.lang.foreign.ValueLayout.JAVA_INT;
import static java.lang.foreign.ValueLayout.JAVA_LONG;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.itemis.fluffyj.memory.api.FluffyPointer;
import com.itemis.fluffyj.memory.api.FluffyScalarSegment;
import com.itemis.fluffyj.memory.api.FluffyVectorSegment;
import com.itemis.fluffyj.memory.error.FluffyMemoryException;
import com.itemis.fluffyj.memory.tests.ArenafiedTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.util.stream.Stream;

class PointerBasicsTest extends ArenafiedTest {

    @Test
    void allocate_scalar_pointer_with_unknown_type_yields_exception() {
        assertThatThrownBy(() -> pointer().to(0L).as(MyType.class).allocate())
            .isInstanceOf(FluffyMemoryException.class)
            .hasMessage("Cannot allocate scalar pointer of unknown type: " + MyType.class.getCanonicalName());
    }

    @Test
    void allocate_scalar_pointer_with_arena_and_unknown_type_yields_exception() {
        assertThatThrownBy(() -> pointer().to(0L).as(MyType.class).allocate(arena))
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
    void allocate_vector_pointer_with_arena_and_unknown_type_yields_exception() {
        assertThatThrownBy(() -> pointer().to(0L).asArray(1).of(MyType[].class).allocate(arena))
            .isInstanceOf(FluffyMemoryException.class)
            .hasMessage("Cannot allocate vector pointer of unknown type: " + MyType[].class.getCanonicalName());
    }

    @Test
    void to_null_seg_yields_npe() {
        final var underTest = new FluffyMemoryPointerBuilder();
        assertNullArgNotAccepted(() -> underTest.to((FluffyScalarSegment<?>) null), "toHere");
    }

    @Test
    void to_null_array_seg_yields_npe() {
        final var underTest = new FluffyMemoryPointerBuilder();
        assertNullArgNotAccepted(() -> underTest.toArray((FluffyVectorSegment<?>) null), "toHere");
    }

    @Test
    void can_create_empty_pointer() {
        final var result = pointer().allocate();
        assertThat(result).isInstanceOf(FluffyPointer.class);
    }

    @Test
    void can_create_arenafied_empty_pointer() {
        final var result = pointer().allocate(arena);
        assertThat(result).isInstanceOf(FluffyPointer.class);
    }

    @Test
    void empty_pointer_creation_does_not_accept_null_arena() {
        assertNullArgNotAccepted(() -> pointer().allocate(null), "arena");
    }

    @ParameterizedTest
    @MethodSource("typeMappings")
    void dereference_should_support_known_types(final MemorySegment ptrSeg, final Class<?> inputType,
            final Class<?> expectedOutputType) {
        final var dereferencedValue = FluffyMemory.dereference(ptrSeg).as(inputType);

        assertThat(dereferencedValue.getClass()).isEqualTo(expectedOutputType);
    }

    private static Stream<Arguments> typeMappings() {
        final var longSeg = global().allocate(JAVA_LONG, 123L);
        final var longSegPtr = global().allocate(ValueLayout.JAVA_LONG, longSeg.address());
        final var intSeg = global().allocate(JAVA_INT, 123);
        final var intSegPtr = global().allocate(ValueLayout.JAVA_LONG, intSeg.address());
        final var byteSeg = global().allocate(JAVA_BYTE, (byte) 123);
        final var byteSegPtr = global().allocate(ValueLayout.JAVA_LONG, byteSeg.address());
        final var stringSeg = global().allocateUtf8String("123");
        final var stringSegPtr = global().allocate(ValueLayout.JAVA_LONG, stringSeg.address());

        return Stream.of(
            Arguments.of(longSegPtr, long.class, Long.class),
            Arguments.of(longSegPtr, Long.class, Long.class),
            Arguments.of(intSegPtr, int.class, Integer.class),
            Arguments.of(intSegPtr, Integer.class, Integer.class),
            Arguments.of(byteSegPtr, byte.class, Byte.class),
            Arguments.of(byteSegPtr, Byte.class, Byte.class),
            Arguments.of(stringSegPtr, String.class, String.class));
    }

    private static final class MyType {
    }
}
