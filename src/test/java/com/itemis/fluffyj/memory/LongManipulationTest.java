package com.itemis.fluffyj.memory;

import static com.google.common.primitives.Longs.toByteArray;
import static jdk.incubator.foreign.MemoryLayouts.JAVA_LONG;

import com.itemis.fluffyj.memory.tests.FluffyMemoryScalarTestValue;
import com.itemis.fluffyj.memory.tests.FluffyScalarDataManipulationTest;

import java.util.Random;

class LongManipulationTest extends FluffyScalarDataManipulationTest<Long> {

    LongManipulationTest() {
        super(new FluffyMemoryScalarTestValueIterator<Long>() {
            private final Random rnd = new Random();

            @Override
            public FluffyMemoryScalarTestValue<Long> next() {
                var typedValue = rnd.nextLong();
                var rawValue = toByteArray(typedValue);
                return new FluffyMemoryScalarTestValue<>(typedValue, rawValue);
            }
        }, JAVA_LONG);
    }
}
