package com.itemis.fluffyj.memory.internal.impl;

import com.itemis.fluffyj.memory.api.FluffyVectorSegment;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.MemorySession;

/**
 * Default implementation of a generic segment that holds vectorized (i. e. array) data.
 *
 * @param <T> - Type of data this segment holds.
 */
public abstract class FluffyVectorSegmentImpl<T> extends FluffySegmentImpl implements FluffyVectorSegment<T> {

    public FluffyVectorSegmentImpl(MemorySegment backingSeg) {
        super(backingSeg);
    }

    public FluffyVectorSegmentImpl(byte[] initialValue, MemorySession session) {
        super(initialValue, session);
    }

    @Override
    public long byteSize() {
        return backingSeg.byteSize();
    }
}
