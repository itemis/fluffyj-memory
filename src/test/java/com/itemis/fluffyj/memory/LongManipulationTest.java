package com.itemis.fluffyj.memory;

import static com.google.common.primitives.Longs.toByteArray;
import static java.nio.ByteOrder.LITTLE_ENDIAN;
import static java.nio.ByteOrder.nativeOrder;
import static jdk.incubator.foreign.MemoryLayouts.JAVA_LONG;

import com.itemis.fluffyj.memory.tests.FluffyMemoryScalarTestValue;
import com.itemis.fluffyj.memory.tests.FluffyScalarDataManipulationTest;

import org.apache.commons.lang3.ArrayUtils;

import java.util.Random;

class LongManipulationTest extends FluffyScalarDataManipulationTest<Long> {

    LongManipulationTest() {
        super(new FluffyMemoryScalarTestValueIterator<Long>() {
            private final Random rnd = new Random();

            @Override
            public FluffyMemoryScalarTestValue<Long> next() {
                var typedValue = rnd.nextLong();
                var rawValue = toByteArray(typedValue);
                if (nativeOrder().equals(LITTLE_ENDIAN)) {
                    ArrayUtils.reverse(rawValue);
                }
                return new FluffyMemoryScalarTestValue<>(typedValue, rawValue);
            }
        }, JAVA_LONG);
    }
}
