package com.itemis.fluffyj.memory;

import com.itemis.fluffyj.memory.tests.FluffyMemoryVectorTestValue;
import com.itemis.fluffyj.memory.tests.FluffyVectorDataManipulationTest;

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.ValueLayout;
import java.util.Random;

class BlobManipulationTest extends FluffyVectorDataManipulationTest<Byte> {

    private static final int TEST_ARRAY_LENGTH = 3;

    protected BlobManipulationTest() {
        super(new FluffyMemoryVectorTestValueIterator<Byte>() {
            private final Random rnd = new Random();

            @Override
            public FluffyMemoryVectorTestValue<Byte> next() {
                var rawValue = new byte[TEST_ARRAY_LENGTH];
                rnd.nextBytes(rawValue);
                var typedValue = new Byte[TEST_ARRAY_LENGTH];
                for (var i = 0; i < rawValue.length; i++) {
                    typedValue[i] = rawValue[i];
                }
                return new FluffyMemoryVectorTestValue<>(typedValue, rawValue);
            }
        }, MemoryLayout.sequenceLayout(3, ValueLayout.JAVA_BYTE));
    }
}
