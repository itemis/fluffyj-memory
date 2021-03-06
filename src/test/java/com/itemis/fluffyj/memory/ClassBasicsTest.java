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
import com.itemis.fluffyj.memory.internal.PointerOfByte;
import com.itemis.fluffyj.memory.internal.PointerOfInt;
import com.itemis.fluffyj.memory.internal.PointerOfLong;
import com.itemis.fluffyj.memory.internal.PointerOfString;

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
        assertNullArgNotAccepted(() -> new FluffyMemoryScalarPointerAllocator<>((MemoryAddress) null, Object.class), "address");
        assertNullArgNotAccepted(() -> new FluffyMemoryScalarPointerAllocator<>(NULL, null), "typeOfData");
        assertNullArgNotAccepted(() -> new FluffyMemoryVectorPointerAllocator<>((FluffyVectorSegment<?>) null), "toHere");
        assertNullArgNotAccepted(() -> new FluffyMemoryVectorPointerAllocator<>((MemoryAddress) null, A_LONG, Object[].class), "address");
        assertNullArgNotAccepted(() -> new FluffyMemoryVectorPointerAllocator<>(NULL, A_LONG, null), "typeOfData");
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
        assertNullArgNotAccepted(() -> new PointerOfString(null, globalScope()) {}, "addressPointedTo");
        assertNullArgNotAccepted(() -> new PointerOfString(NULL, null) {}, "scope");
        assertNullArgNotAccepted(() -> new PointerOfByte(null, globalScope()) {}, "addressPointedTo");
        assertNullArgNotAccepted(() -> new PointerOfByte(NULL, null) {}, "scope");
    }
}
