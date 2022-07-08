package com.itemis.fluffyj.memory;

import static java.nio.ByteOrder.LITTLE_ENDIAN;
import static java.nio.ByteOrder.nativeOrder;
import static jdk.incubator.foreign.MemoryLayouts.JAVA_INT;

import com.google.common.primitives.Ints;
import com.itemis.fluffyj.memory.tests.FluffyMemoryScalarTestValue;
import com.itemis.fluffyj.memory.tests.FluffyScalarDataManipulationTest;

import org.apache.commons.lang3.ArrayUtils;

import java.util.Random;

class IntManipulationTest extends FluffyScalarDataManipulationTest<Integer> {

    IntManipulationTest() {
        super(new FluffyMemoryScalarTestValueIterator<Integer>() {
            private final Random rnd = new Random();

            @Override
            public FluffyMemoryScalarTestValue<Integer> next() {
                var typedValue = rnd.nextInt();
                var rawValue = Ints.toByteArray(typedValue);
                if (nativeOrder().equals(LITTLE_ENDIAN)) {
                    ArrayUtils.reverse(rawValue);
                }
                return new FluffyMemoryScalarTestValue<>(typedValue, rawValue);
            }
        }, JAVA_INT);
    }
}
