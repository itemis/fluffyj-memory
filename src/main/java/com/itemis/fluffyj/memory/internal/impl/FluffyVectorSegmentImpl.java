package com.itemis.fluffyj.memory.internal.impl;

import com.itemis.fluffyj.memory.api.FluffyVectorSegment;

import java.nio.ByteBuffer;

import jdk.incubator.foreign.MemoryLayout;
import jdk.incubator.foreign.MemorySegment;
import jdk.incubator.foreign.ResourceScope;

/**
 * Default implementation of a generic segment that holds vectorized (i. e. array) data.
 *
 * @param <T> - Type of data this segment holds.
 */
public abstract class FluffyVectorSegmentImpl<T> extends FluffySegmentImpl implements FluffyVectorSegment<T> {

    public FluffyVectorSegmentImpl(MemorySegment backingSeg) {
        super(backingSeg);
    }

    public FluffyVectorSegmentImpl(byte[] initialValue, MemoryLayout layout, ResourceScope scope) {
        super(initialValue, layout, scope);
    }

    /**
     * @param rawValue - A read only {@link ByteBuffer} that contains the bytes this segment holds.
     * @return The typed interpretation if this segment's bytes.
     */
    protected abstract T[] getTypedValue(ByteBuffer rawValue);

    @Override
    public T[] getValue() {
        return getTypedValue(backingSeg.asByteBuffer().asReadOnlyBuffer().order(FLUFFY_SEGMENT_BYTE_ORDER));
    }

    @Override
    public long byteSize() {
        return backingSeg.byteSize();
    }
}
