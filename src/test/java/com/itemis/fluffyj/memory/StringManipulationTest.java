package com.itemis.fluffyj.memory;

import static java.lang.foreign.MemoryLayout.sequenceLayout;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

import com.itemis.fluffyj.memory.tests.FluffyMemoryScalarTestValue;
import com.itemis.fluffyj.memory.tests.FluffyScalarDataManipulationTest;

import java.lang.foreign.Arena;
import java.lang.foreign.ValueLayout;

public class StringManipulationTest extends FluffyScalarDataManipulationTest<String> {

    private static final int RND_STR_LENGTH = 10;

    protected StringManipulationTest() {
        super(new FluffyMemoryScalarTestValueIterator<String>() {
            @Override
            public FluffyMemoryScalarTestValue<String> next() {
                final var typedValue = randomAlphanumeric(RND_STR_LENGTH);
                final var cString = Arena.ofAuto().allocateUtf8String(typedValue);
                final var rawValue = new byte[(int) cString.byteSize()];
                cString.asByteBuffer().get(rawValue);
                return new FluffyMemoryScalarTestValue<>(typedValue, rawValue);
            }
        }, sequenceLayout(RND_STR_LENGTH + 1, ValueLayout.JAVA_BYTE));
    }
}
