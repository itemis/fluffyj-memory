package com.itemis.fluffyj.memory.internal;

import static java.util.Objects.requireNonNull;
import static jdk.incubator.foreign.MemoryLayout.sequenceLayout;
import static jdk.incubator.foreign.MemoryLayouts.JAVA_BYTE;

import com.itemis.fluffyj.memory.internal.impl.FluffyVectorSegmentImpl;

import java.nio.ByteBuffer;

import jdk.incubator.foreign.MemorySegment;
import jdk.incubator.foreign.ResourceScope;

public class BlobSegment extends FluffyVectorSegmentImpl<Byte> {

    public BlobSegment(MemorySegment backingSeg) {
        super(backingSeg);
    }

    public BlobSegment(Byte[] initialValue, ResourceScope scope) {
        super(primitivize(requireNonNull(initialValue, "initialValue")), sequenceLayout(initialValue.length, JAVA_BYTE), requireNonNull(scope, "scope"));
    }

    private static final byte[] primitivize(Byte[] value) {
        var result = new byte[value.length];
        for (int i = 0; i < value.length; i++) {
            result[i] = value[i];
        }

        return result;
    }

    @Override
    protected Byte[] getTypedValue(ByteBuffer rawValue) {
        var length = rawValue.capacity();
        var result = new Byte[length];
        for (int i = 0; i < length; i++) {
            result[i] = rawValue.asReadOnlyBuffer().get(i);
        }
        return result;
    }

    @Override
    public Class<Byte[]> getContainedType() {
        return Byte[].class;
    }
}
