package com.itemis.fluffyj.memory;

import static com.itemis.fluffyj.tests.FluffyTestHelper.assertFinal;
import static com.itemis.fluffyj.tests.FluffyTestHelper.assertIsStaticHelper;
import static com.itemis.fluffyj.tests.FluffyTestHelper.assertNullArgNotAccepted;
import static jdk.incubator.foreign.MemoryAddress.NULL;
import static jdk.incubator.foreign.ResourceScope.globalScope;

import com.itemis.fluffyj.memory.FluffyMemoryPointerBuilder.FluffyMemoryTypedPointerBuilder;
import com.itemis.fluffyj.memory.api.FluffyScalarSegment;
import com.itemis.fluffyj.memory.api.FluffyVectorSegment;
import com.itemis.fluffyj.memory.internal.PointerOfBlob;
import com.itemis.fluffyj.memory.internal.PointerOfInt;
import com.itemis.fluffyj.memory.internal.PointerOfLong;

import org.junit.jupiter.api.Test;

import jdk.incubator.foreign.MemoryAddress;

class ClassBasicsTest {

    private static final int A_LONG = 1;

    @Test
    void is_static_helper() {
        assertIsStaticHelper(FluffyMemory.class);
    }

    @Test
    void test_is_final() {
        assertFinal(FluffyMemoryScalarPointerAllocator.class);
        assertFinal(FluffyMemoryVectorPointerAllocator.class);
        assertFinal(FluffyMemory.class);
        assertFinal(FluffyMemoryPointerBuilder.class);
        assertFinal(FluffyMemoryTypedPointerBuilder.class);
        assertFinal(FluffyMemoryScalarSegmentAllocator.class);
        assertFinal(FluffyMemoryVectorSegmentAllocator.class);
        assertFinal(FluffyMemorySegmentBuilder.class);
        assertFinal(FluffyMemorySegmentWrapper.class);
    }

    @Test
    void constructor_with_null_arg_yields_npe() {
        assertNullArgNotAccepted(() -> new FluffyMemoryScalarPointerAllocator<>((FluffyScalarSegment<?>) null), "toHere");
        assertNullArgNotAccepted(() -> new FluffyMemoryScalarPointerAllocator<Object>((MemoryAddress) null, Object.class), "address");
        assertNullArgNotAccepted(() -> new FluffyMemoryScalarPointerAllocator<Object>(NULL, null), "typeOfData");
        assertNullArgNotAccepted(() -> new FluffyMemoryVectorPointerAllocator<>((FluffyVectorSegment<?>) null), "toHere");
        assertNullArgNotAccepted(() -> new FluffyMemoryVectorPointerAllocator<Object>((MemoryAddress) null, A_LONG, Object[].class), "address");
        assertNullArgNotAccepted(() -> new FluffyMemoryVectorPointerAllocator<Object>(NULL, A_LONG, null), "typeOfData");
        assertNullArgNotAccepted(() -> new FluffyMemoryTypedPointerBuilder(null), "address");
        assertNullArgNotAccepted(() -> new FluffyMemoryScalarSegmentAllocator<>(null), "initialValue");
        assertNullArgNotAccepted(() -> new FluffyMemoryVectorSegmentAllocator<>(null), "initialValue");
        assertNullArgNotAccepted(() -> new FluffyMemorySegmentWrapper(null), "nativeSegment");
        assertNullArgNotAccepted(() -> new PointerOfInt(null, globalScope()) {}, "addressPointedTo");
        assertNullArgNotAccepted(() -> new PointerOfInt(NULL, null) {}, "scope");
        assertNullArgNotAccepted(() -> new PointerOfLong(null, globalScope()) {}, "addressPointedTo");
        assertNullArgNotAccepted(() -> new PointerOfLong(NULL, null) {}, "scope");
        assertNullArgNotAccepted(() -> new PointerOfBlob(null, A_LONG, globalScope()) {}, "addressPointedTo");
        assertNullArgNotAccepted(() -> new PointerOfBlob(NULL, A_LONG, null) {}, "scope");
    }
}
