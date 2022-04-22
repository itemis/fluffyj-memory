package com.itemis.fluffyj.memory;

import static com.itemis.fluffyj.tests.FluffyTestHelper.assertFinal;
import static com.itemis.fluffyj.tests.FluffyTestHelper.assertNullArgNotAccepted;
import static jdk.incubator.foreign.ResourceScope.globalScope;

import com.itemis.fluffyj.memory.FluffyMemoryPointerBuilder.FluffyMemoryTypedPointerBuilder;
import com.itemis.fluffyj.memory.api.FluffySegment;
import com.itemis.fluffyj.memory.internal.PointerOfLong;

import org.junit.jupiter.api.Test;

import jdk.incubator.foreign.MemoryAddress;

class ClassBasicsTest {

    @Test
    void test_is_final() {
        assertFinal(FluffyMemoryPointerAllocator.class);
        assertFinal(FluffyMemory.class);
        assertFinal(FluffyMemoryPointerBuilder.class);
        assertFinal(FluffyMemoryTypedPointerBuilder.class);
        assertFinal(FluffyMemorySegmentAllocator.class);
        assertFinal(FluffyMemorySegmentBuilder.class);
        assertFinal(FluffyMemorySegmentWrapper.class);
    }

    @Test
    void constructor_with_null_arg_yields_npe() {
        assertNullArgNotAccepted(() -> new FluffyMemoryPointerAllocator<>((FluffySegment<?>) null), "toHere");
        assertNullArgNotAccepted(() -> new FluffyMemoryPointerAllocator<Object>((MemoryAddress) null), "address");
        assertNullArgNotAccepted(() -> new FluffyMemoryTypedPointerBuilder(null), "address");
        assertNullArgNotAccepted(() -> new FluffyMemorySegmentAllocator<>(null), "initialValue");
        assertNullArgNotAccepted(() -> new FluffyMemorySegmentWrapper(null), "nativeSegment");
        assertNullArgNotAccepted(() -> new PointerOfLong(null, globalScope()) {}, "addressPointedTo");
        assertNullArgNotAccepted(() -> new PointerOfLong(MemoryAddress.NULL, null) {}, "scope");
    }

    @Test
    void allocate_with_null_yields_npe() {
        var pointerAlloc = new FluffyMemoryPointerAllocator<>(MemoryAddress.NULL);
        assertNullArgNotAccepted(() -> pointerAlloc.allocate(null), "scope");

        var segmentAlloc = new FluffyMemorySegmentAllocator<>(MemoryAddress.NULL);
        assertNullArgNotAccepted(() -> segmentAlloc.allocate(null), "scope");
    }

    @Test
    void to_null_seg_yields_npe() {
        var underTest = new FluffyMemoryPointerBuilder();
        assertNullArgNotAccepted(() -> underTest.to((FluffySegment<?>) null), "toHere");
    }

    @Test
    void to_null_addr_yields_npe() {
        var underTest = new FluffyMemoryPointerBuilder();
        assertNullArgNotAccepted(() -> underTest.to((MemoryAddress) null), "address");
    }
}
