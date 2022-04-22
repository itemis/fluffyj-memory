package com.itemis.fluffyj.memory.internal;

import static jdk.incubator.foreign.MemoryLayout.sequenceLayout;
import static jdk.incubator.foreign.MemoryLayouts.JAVA_BYTE;

import java.nio.ByteBuffer;

import jdk.incubator.foreign.MemorySegment;
import jdk.incubator.foreign.ResourceScope;

public class BlobSegment extends FluffySegmentImpl<byte[]> {

    public BlobSegment(byte[] initialValue, ResourceScope scope) {
        super(initialValue, sequenceLayout(initialValue.length, JAVA_BYTE), scope);
    }

    public BlobSegment(MemorySegment backingSeg) {
        super(backingSeg);
    }

    @Override
    protected byte[] getTypedValue(ByteBuffer rawValue) {
        var length = rawValue.capacity();
        var result = new byte[length];
        rawValue.asReadOnlyBuffer().get(result);

        return result;
    }

    @Override
    public Class<byte[]> getContainedType() {
        return byte[].class;
    }

    @Override
    public int byteSize() {
        return getValue().length;
    }
}
