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
                final var buf = new byte[1];
                rnd.nextBytes(buf);
                if (buf[0] < 0) {
                    buf[0] = (byte) (buf[0] * (-1));
                }
                return new FluffyMemoryScalarTestValue<>(buf[0], buf);
            }
        }, JAVA_BYTE);
    }
}
