package com.itemis.fluffyj.memory;

import static com.itemis.fluffyj.memory.FluffyMemory.segment;
import static com.itemis.fluffyj.memory.FluffyMemory.wrap;
import static com.itemis.fluffyj.tests.FluffyTestHelper.assertNullArgNotAccepted;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.itemis.fluffyj.memory.api.FluffyScalarSegment;
import com.itemis.fluffyj.memory.api.FluffyVectorSegment;
import com.itemis.fluffyj.memory.error.FluffyMemoryException;
import com.itemis.fluffyj.memory.tests.ArenafiedTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.foreign.MemorySegment;
import java.util.stream.Stream;

class SegmentBasicsTest extends ArenafiedTest {

    private MemorySegment nativeSeg;

    @BeforeEach
    void setUp() {
        nativeSeg = arena.allocate(1);
    }

    @Test
    void address_rawAddress_equality() {
        final var segment = new FluffyMemoryScalarSegmentAllocator<>("test").allocate(arena);
        assertThat(segment.address().address()).isEqualTo(segment.rawAddress());
    }

    @Test
    void allocate_with_null_yields_npe() {
        final var pointerAlloc = new FluffyMemoryScalarPointerAllocator<>(0L, Object.class);
        assertNullArgNotAccepted(() -> pointerAlloc.allocate(null), "arena");

        final var segmentAlloc = new FluffyMemoryScalarSegmentAllocator<>(0L);
        assertNullArgNotAccepted(() -> segmentAlloc.allocate(null), "arena");
    }

    @Test
    void allocate_scalar_seg_with_unknown_type_yields_exception() {
        assertThatThrownBy(() -> segment().of(new MyType()).allocate())
            .isInstanceOf(FluffyMemoryException.class)
            .hasMessage("Cannot allocate scalar segment of unknown type: " + MyType.class.getCanonicalName());
    }

    @Test
    void allocate_scalar_seg_with_arena_and_unknown_type_yields_exception() {
        assertThatThrownBy(() -> segment().of(new MyType()).allocate(arena))
            .isInstanceOf(FluffyMemoryException.class)
            .hasMessage("Cannot allocate scalar segment of unknown type: " + MyType.class.getCanonicalName());
    }

    @Test
    void allocate_vector_seg_with_arena_and_unknown_type_yields_exception() {
        assertThatThrownBy(() -> segment().ofArray(new MyType[0]).allocate(arena))
            .isInstanceOf(FluffyMemoryException.class)
            .hasMessage(
                "Cannot allocate vector segment of unknown type: " + MyType.class.arrayType().getCanonicalName());
    }

    @Test
    void allocate_vector_seg_with_unknown_type_yields_exception() {
        assertThatThrownBy(() -> segment().ofArray(new MyType[0]).allocate())
            .isInstanceOf(FluffyMemoryException.class)
            .hasMessage(
                "Cannot allocate vector segment of unknown type: " + MyType.class.arrayType().getCanonicalName());
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
    void wrap_null_yields_npe() {
        assertNullArgNotAccepted(() -> wrap(null), "nativeSeg");
    }

    @Test
    void wrap_as_null_yields_npe() {
        assertNullArgNotAccepted(() -> wrap(nativeSeg).as(null), "type");
    }

    @Test
    void wrap_asArray_null_yields_npe() {
        assertNullArgNotAccepted(() -> wrap(nativeSeg).asArray(null), "type");
    }

    @Test
    void wrap_to_unknown_scalar_type_yields_exception() {
        assertThatThrownBy(() -> wrap(nativeSeg).as(MyType.class)).isInstanceOf(FluffyMemoryException.class)
            .hasMessage("Cannot wrap scalar segment of unknown type: " + MyType.class.getCanonicalName());
    }

    @Test
    void wrap_to_unknown_vector_type_yields_exception() {
        assertThatThrownBy(() -> wrap(nativeSeg).asArray(MyType[].class)).isInstanceOf(FluffyMemoryException.class)
            .hasMessage("Cannot wrap vector segment of unknown type: " + MyType[].class.getCanonicalName());
    }

    @ParameterizedTest
    @MethodSource("typeMappings")
    void wrap_should_support_known_types(final Class<?> inputType, final Class<?> expectedOutputType) {
        final var actualOutputType = wrap(nativeSeg).as(inputType).getContainedType();

        assertThat(actualOutputType).isEqualTo(expectedOutputType);
    }

    private static Stream<Arguments> typeMappings() {
        return Stream.of(Arguments.of(long.class, Long.class),
            Arguments.of(Long.class, Long.class),
            Arguments.of(int.class, Integer.class),
            Arguments.of(Integer.class, Integer.class),
            Arguments.of(byte.class, Byte.class),
            Arguments.of(Byte.class, Byte.class),
            Arguments.of(byte.class, Byte.class),
            Arguments.of(String.class, String.class));
    }

    private static final class MyType {
    }
}
