package com.itemis.fluffyj.memory;

import static jdk.incubator.foreign.MemoryLayout.sequenceLayout;
import static jdk.incubator.foreign.MemoryLayouts.JAVA_BYTE;
import static jdk.incubator.foreign.MemorySegment.allocateNative;

import com.itemis.fluffyj.memory.tests.FluffyVectorDataManipulationTest;

import java.util.Random;

import jdk.incubator.foreign.MemorySegment;
import jdk.incubator.foreign.ResourceScope;

class BlobManipulationTest extends FluffyVectorDataManipulationTest<Byte> {

    protected BlobManipulationTest() {
        super(() -> {
            var rawResult = new byte[3];
            new Random().nextBytes(rawResult);
            var result = new Byte[3];
            for (int i = 0; i < result.length; i++) {
                result[i] = rawResult[i];
            }
            return result;
        });
    }

    @Override
    protected MemorySegment allocateNativeSeg(Byte[] initialValue, ResourceScope scope) {
        var result = allocateNative(sequenceLayout(initialValue.length, JAVA_BYTE), scope);

        changeNativeSegValue(result, initialValue);
        return result;
    }

    @Override
    protected void changeNativeSegValue(MemorySegment nativeSeg, Byte[] newValue) {
        for (int i = 0; i < newValue.length; i++) {
            nativeSeg.asByteBuffer().put(i, newValue[i]);
        }
    }
}
