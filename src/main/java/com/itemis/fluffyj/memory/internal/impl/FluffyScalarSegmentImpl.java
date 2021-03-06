package com.itemis.fluffyj.memory.internal.impl;

import com.itemis.fluffyj.memory.api.FluffyScalarSegment;

import java.nio.ByteBuffer;

import jdk.incubator.foreign.MemoryLayout;
import jdk.incubator.foreign.MemorySegment;
import jdk.incubator.foreign.ResourceScope;

/**
 * Default implementation of a segment that holds scalar values, i. e. non array ones.
 *
 * @param <T> - Type of data this segment holds.
 */
public abstract class FluffyScalarSegmentImpl<T> extends FluffySegmentImpl implements FluffyScalarSegment<T> {

    public FluffyScalarSegmentImpl(MemorySegment backingSeg) {
        super(backingSeg);
    }

    public FluffyScalarSegmentImpl(byte[] initialValue, MemoryLayout layout, ResourceScope scope) {
        super(initialValue, layout, scope);
    }

    /**
     * @param rawValue - A read only {@link ByteBuffer} that contains the bytes this segment holds.
     * @return The typed interpretation if this segment's bytes.
     */
    protected abstract T getTypedValue(ByteBuffer rawValue);

    @Override
    public T getValue() {
        return getTypedValue(backingSeg.asByteBuffer().asReadOnlyBuffer().order(FLUFFY_SEGMENT_BYTE_ORDER));
    }
}
