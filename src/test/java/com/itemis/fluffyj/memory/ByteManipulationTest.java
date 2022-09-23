package com.itemis.fluffyj.memory;

import static java.lang.foreign.ValueLayout.JAVA_BYTE;

import com.itemis.fluffyj.memory.tests.FluffyMemoryScalarTestValue;
import com.itemis.fluffyj.memory.tests.FluffyScalarDataManipulationTest;

import java.util.Random;

class ByteManipulationTest extends FluffyScalarDataManipulationTest<Byte> {

    ByteManipulationTest() {
        super(new FluffyMemoryScalarTestValueIterator<Byte>() {
            private final Random rnd = new Random();

            @Override
            public FluffyMemoryScalarTestValue<Byte> next() {
                var buf = new byte[1];
                rnd.nextBytes(buf);
                return new FluffyMemoryScalarTestValue<>(buf[0], buf);
            }
        }, JAVA_BYTE);
    }
}
