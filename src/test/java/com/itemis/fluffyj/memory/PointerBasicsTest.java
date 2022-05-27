package com.itemis.fluffyj.memory;

import static com.itemis.fluffyj.memory.FluffyMemory.pointer;
import static com.itemis.fluffyj.tests.FluffyTestHelper.assertNullArgNotAccepted;
import static jdk.incubator.foreign.MemoryAddress.NULL;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.itemis.fluffyj.memory.api.FluffyScalarSegment;
import com.itemis.fluffyj.memory.api.FluffyVectorSegment;
import com.itemis.fluffyj.memory.error.FluffyMemoryException;
import com.itemis.fluffyj.memory.tests.MemoryScopedTest;

import org.junit.jupiter.api.Test;

import jdk.incubator.foreign.MemoryAddress;

class PointerBasicsTest extends MemoryScopedTest {

    @Test
    void allocate_scalar_pointer_with_unknown_type_yields_exception() {
        assertThatThrownBy(() -> pointer().to(NULL).as(MyType.class).allocate())
            .isInstanceOf(FluffyMemoryException.class)
            .hasMessage("Cannot allocate scalar pointer of unknown type: " + MyType.class.getCanonicalName());
    }

    @Test
    void allocate_scalar_pointer_and_scope_with_unknown_type_yields_exception() {
        assertThatThrownBy(() -> pointer().to(NULL).as(MyType.class).allocate(scope))
            .isInstanceOf(FluffyMemoryException.class)
            .hasMessage("Cannot allocate scalar pointer of unknown type: " + MyType.class.getCanonicalName());
    }

    @Test
    void allocate_vector_pointer_with_unknown_type_yields_exception() {
        assertThatThrownBy(() -> pointer().to(NULL).asArray(1).of(MyType[].class).allocate())
            .isInstanceOf(FluffyMemoryException.class)
            .hasMessage("Cannot allocate vector pointer of unknown type: " + MyType[].class.getCanonicalName());
    }

    @Test
    void allocate_vector_pointer_and_scope_with_unknown_type_yields_exception() {
        assertThatThrownBy(() -> pointer().to(NULL).asArray(1).of(MyType[].class).allocate(scope))
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
    void to_null_addr_yields_npe() {
        var underTest = new FluffyMemoryPointerBuilder();
        assertNullArgNotAccepted(() -> underTest.to((MemoryAddress) null), "address");
    }

    private static final class MyType {
    }
}
