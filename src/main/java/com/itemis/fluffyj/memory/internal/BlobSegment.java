package com.itemis.fluffyj.memory.internal;

import static java.util.Objects.requireNonNull;

import com.itemis.fluffyj.memory.internal.impl.FluffyVectorSegmentImpl;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;


public class BlobSegment extends FluffyVectorSegmentImpl<Byte> {

    public BlobSegment(final MemorySegment backingSeg) {
        super(backingSeg);
    }

    public BlobSegment(final Byte[] initialValue, final Arena arena) {
        this(primitivize(requireNonNull(initialValue, "initialValue")), requireNonNull(arena, "arena"));
    }

    public BlobSegment(final byte[] initialValue, final Arena arena) {
        super(requireNonNull(initialValue, "initialValue"), requireNonNull(arena, "arena"));
    }

    private static final byte[] primitivize(final Byte[] value) {
        final var result = new byte[value.length];
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
