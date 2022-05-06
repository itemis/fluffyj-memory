package com.itemis.fluffyj.memory;

import static jdk.incubator.foreign.MemoryLayouts.JAVA_LONG;

import com.itemis.fluffyj.memory.tests.FluffyScalarDataManipulationTest;

import jdk.incubator.foreign.MemorySegment;

class LongManipulationTest extends FluffyScalarDataManipulationTest<Long> {

    private static long LONG_VAL = 123L;

    LongManipulationTest() {
        super(LONG_VAL);
    }

    @Override
    protected final MemorySegment allocateNativeSeg(Long initialValue) {
        var result = MemorySegment.allocateNative(JAVA_LONG, scope);
        result.asByteBuffer().putLong(LONG_VAL);
        return result;
    }
}
