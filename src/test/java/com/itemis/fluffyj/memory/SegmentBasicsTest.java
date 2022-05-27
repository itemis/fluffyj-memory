package com.itemis.fluffyj.memory;

import static com.itemis.fluffyj.memory.FluffyMemory.segment;
import static com.itemis.fluffyj.memory.FluffyMemory.wrap;
import static com.itemis.fluffyj.tests.FluffyTestHelper.assertNullArgNotAccepted;
import static jdk.incubator.foreign.MemoryAddress.NULL;
import static jdk.incubator.foreign.MemorySegment.allocateNative;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.itemis.fluffyj.memory.api.FluffyScalarSegment;
import com.itemis.fluffyj.memory.api.FluffyVectorSegment;
import com.itemis.fluffyj.memory.error.FluffyMemoryException;
import com.itemis.fluffyj.memory.tests.MemoryScopedTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jdk.incubator.foreign.MemorySegment;

class SegmentBasicsTest extends MemoryScopedTest {

    private MemorySegment nativeSeg;

    @BeforeEach
    void setUp() {
        nativeSeg = allocateNative(1, scope);
    }

    @Test
    void allocate_with_null_yields_npe() {
        var pointerAlloc = new FluffyMemoryScalarPointerAllocator<>(NULL, Object.class);
        assertNullArgNotAccepted(() -> pointerAlloc.allocate(null), "scope");

        var segmentAlloc = new FluffyMemoryScalarSegmentAllocator<>(NULL);
        assertNullArgNotAccepted(() -> segmentAlloc.allocate(null), "scope");
    }

    @Test
    void allocate_scalar_seg_with_unknown_type_yields_exception() {
        assertThatThrownBy(() -> segment().of(new MyType()).allocate())
            .isInstanceOf(FluffyMemoryException.class)
            .hasMessage("Cannot allocate scalar segment of unknown type: " + MyType.class.getCanonicalName());
    }

    @Test
    void allocate_scalar_seg_with_scope_and_unknown_type_yields_exception() {
        assertThatThrownBy(() -> segment().of(new MyType()).allocate(scope))
            .isInstanceOf(FluffyMemoryException.class)
            .hasMessage("Cannot allocate scalar segment of unknown type: " + MyType.class.getCanonicalName());
    }

    @Test
    void allocate_vector_seg_with_scope_and_unknown_type_yields_exception() {
        assertThatThrownBy(() -> segment().ofArray(new MyType[0]).allocate(scope))
            .isInstanceOf(FluffyMemoryException.class)
            .hasMessage("Cannot allocate vector segment of unknown type: " + MyType.class.arrayType().getCanonicalName());
    }

    @Test
    void allocate_vector_seg_with_unknown_type_yields_exception() {
        assertThatThrownBy(() -> segment().ofArray(new MyType[0]).allocate())
            .isInstanceOf(FluffyMemoryException.class)
            .hasMessage("Cannot allocate vector segment of unknown type: " + MyType.class.arrayType().getCanonicalName());
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

    private static final class MyType {
    }
}
