package com.itemis.fluffyj.memory;

import static jdk.incubator.foreign.MemoryLayout.sequenceLayout;
import static jdk.incubator.foreign.MemoryLayouts.JAVA_BYTE;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

import com.itemis.fluffyj.memory.tests.FluffyMemoryScalarTestValue;
import com.itemis.fluffyj.memory.tests.FluffyScalarDataManipulationTest;

import jdk.incubator.foreign.CLinker;
import jdk.incubator.foreign.ResourceScope;

public class StringManipulationTest extends FluffyScalarDataManipulationTest<String> {

    private static final int RND_STR_LENGTH = 10;

    protected StringManipulationTest() {
        super(new FluffyMemoryScalarTestValueIterator<String>() {
            @Override
            public FluffyMemoryScalarTestValue<String> next() {
                var typedValue = randomAlphanumeric(RND_STR_LENGTH);
                var cString = CLinker.toCString(typedValue, ResourceScope.globalScope());
                var rawValue = new byte[(int) cString.byteSize()];
                cString.asByteBuffer().get(rawValue);
                return new FluffyMemoryScalarTestValue<>(typedValue, rawValue);
            }
        }, sequenceLayout(RND_STR_LENGTH + 1, JAVA_BYTE));
    }
}
