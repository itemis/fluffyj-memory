package com.itemis.fluffyj.memory.internal.impl;

import com.itemis.fluffyj.memory.api.FluffyVectorSegment;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentScope;

/**
 * Default implementation of a generic segment that holds vectorized (i. e. array) data.
 *
 * @param <T> - Type of data this segment holds.
 */
public abstract class FluffyVectorSegmentImpl<T> extends FluffySegmentImpl implements FluffyVectorSegment<T> {

    public FluffyVectorSegmentImpl(MemorySegment backingSeg) {
        super(backingSeg);
    }

    public FluffyVectorSegmentImpl(byte[] initialValue, SegmentScope scope) {
        super(initialValue, scope);
    }

    @Override
    public long byteSize() {
        return backingSeg.byteSize();
    }
}
