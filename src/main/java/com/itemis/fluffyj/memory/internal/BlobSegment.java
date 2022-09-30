package com.itemis.fluffyj.memory.internal;

import static java.lang.foreign.MemoryLayout.sequenceLayout;
import static java.util.Objects.requireNonNull;

import com.itemis.fluffyj.memory.internal.impl.FluffyVectorSegmentImpl;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.MemorySession;
import java.lang.foreign.ValueLayout;
import java.nio.ByteBuffer;


public class BlobSegment extends FluffyVectorSegmentImpl<Byte> {

    public BlobSegment(MemorySegment backingSeg) {
        super(backingSeg);
    }

    public BlobSegment(Byte[] initialValue, MemorySession session) {
        super(primitivize(requireNonNull(initialValue, "initialValue")), sequenceLayout(initialValue.length, ValueLayout.JAVA_BYTE),
            requireNonNull(session, "session"));
    }

    private static final byte[] primitivize(Byte[] value) {
        var result = new byte[value.length];
        for (var i = 0; i < value.length; i++) {
            result[i] = value[i];
        }

        return result;
    }

    @Override
    protected Byte[] getTypedValue(ByteBuffer rawValue) {
        var length = rawValue.capacity();
        var result = new Byte[length];
        for (var i = 0; i < length; i++) {
            result[i] = rawValue.asReadOnlyBuffer().get(i);
        }
        return result;
    }

    @Override
    public Class<Byte[]> getContainedType() {
        return Byte[].class;
    }
}
