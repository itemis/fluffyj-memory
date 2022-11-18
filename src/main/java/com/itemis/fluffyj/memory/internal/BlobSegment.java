package com.itemis.fluffyj.memory.internal;

import static java.util.Objects.requireNonNull;

import com.itemis.fluffyj.memory.internal.impl.FluffyVectorSegmentImpl;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.MemorySession;
import java.lang.foreign.ValueLayout;


public class BlobSegment extends FluffyVectorSegmentImpl<Byte> {

    public BlobSegment(MemorySegment backingSeg) {
        super(backingSeg);
    }

    public BlobSegment(Byte[] initialValue, MemorySession session) {
        this(primitivize(requireNonNull(initialValue, "initialValue")), requireNonNull(session, "session"));
    }

    public BlobSegment(byte[] initialValue, MemorySession session) {
        super(requireNonNull(initialValue, "initialValue"), requireNonNull(session, "session"));
    }

    private static final byte[] primitivize(Byte[] value) {
        var result = new byte[value.length];
        for (var i = 0; i < value.length; i++) {
            result[i] = value[i];
        }

        return result;
    }

    @Override
    public Class<Byte[]> getContainedType() {
        return Byte[].class;
    }

    @Override
    public Byte[] getValue() {
        return backingSeg.elements(ValueLayout.JAVA_BYTE).map(seg -> seg.get(ValueLayout.JAVA_BYTE, 0))
            .toArray(Byte[]::new);
    }
}
