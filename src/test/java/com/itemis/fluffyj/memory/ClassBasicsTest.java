package com.itemis.fluffyj.memory;

import static com.itemis.fluffyj.memory.FluffyMemory.wrap;
import static com.itemis.fluffyj.tests.FluffyTestHelper.assertFinal;
import static com.itemis.fluffyj.tests.FluffyTestHelper.assertIsStaticHelper;
import static com.itemis.fluffyj.tests.FluffyTestHelper.assertNullArgNotAccepted;
import static java.lang.foreign.MemorySegment.NULL;

import com.itemis.fluffyj.memory.FluffyMemoryPointerBuilder.FluffyMemoryTypedPointerBuilder;
import com.itemis.fluffyj.memory.api.FluffyScalarSegment;
import com.itemis.fluffyj.memory.api.FluffyVectorSegment;
import com.itemis.fluffyj.memory.internal.PointerOfBlob;
import com.itemis.fluffyj.memory.internal.PointerOfByte;
import com.itemis.fluffyj.memory.internal.PointerOfInt;
import com.itemis.fluffyj.memory.internal.PointerOfLong;
import com.itemis.fluffyj.memory.internal.PointerOfString;

import org.junit.jupiter.api.Test;

import java.lang.foreign.Arena;

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
        assertFinal(FluffyMemoryDereferencer.class);
    }

    @Test
    void constructor_with_null_arg_yields_npe() {
        assertNullArgNotAccepted(() -> new FluffyMemoryScalarPointerAllocator<>((FluffyScalarSegment<?>) null),
            "toHere");
        assertNullArgNotAccepted(() -> new FluffyMemoryScalarPointerAllocator<>(NULL.address(), null), "typeOfData");
        assertNullArgNotAccepted(() -> new FluffyMemoryVectorPointerAllocator<>((FluffyVectorSegment<?>) null),
            "toHere");
        assertNullArgNotAccepted(() -> new FluffyMemoryVectorPointerAllocator<>(NULL.address(), A_LONG, null),
            "typeOfData");
        assertNullArgNotAccepted(() -> new FluffyMemoryScalarSegmentAllocator<>(null), "initialValue");
        assertNullArgNotAccepted(() -> new FluffyMemoryVectorSegmentAllocator<>(null), "initialValue");
        assertNullArgNotAccepted(() -> new FluffyMemorySegmentWrapper(null), "nativeSegment");
        assertNullArgNotAccepted(() -> new PointerOfInt(NULL.address(), null) {}, "arena");
        assertNullArgNotAccepted(() -> new PointerOfLong(NULL.address(), null) {}, "arena");
        assertNullArgNotAccepted(() -> new PointerOfBlob(NULL.address(), A_LONG, null) {}, "arena");
        assertNullArgNotAccepted(() -> new PointerOfString(NULL.address(), null) {}, "arena");
        assertNullArgNotAccepted(() -> new PointerOfByte(NULL.address(), null) {}, "arena");
        assertNullArgNotAccepted(() -> new FluffyMemoryDereferencer(null, Arena.global()), "wrapper");
        assertNullArgNotAccepted(() -> new FluffyMemoryDereferencer(wrap(NULL), null), "arena");
    }
}
