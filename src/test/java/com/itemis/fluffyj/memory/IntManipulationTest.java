package com.itemis.fluffyj.memory;

import static com.google.common.primitives.Ints.toByteArray;
import static jdk.incubator.foreign.MemoryLayouts.JAVA_LONG;

import com.itemis.fluffyj.memory.tests.FluffyMemoryScalarTestValue;
import com.itemis.fluffyj.memory.tests.FluffyScalarDataManipulationTest;

import java.util.Random;

class IntManipulationTest extends FluffyScalarDataManipulationTest<Integer> {

    IntManipulationTest() {
        super(new FluffyMemoryScalarTestValueIterator<Integer>() {
            private final Random rnd = new Random();

            @Override
            public FluffyMemoryScalarTestValue<Integer> next() {
                var typedValue = rnd.nextInt();
                var rawValue = toByteArray(typedValue);
                return new FluffyMemoryScalarTestValue<>(typedValue, rawValue);
            }
        }, JAVA_LONG);
    }
}
