package com.itemis.fluffyj.memory.internal.impl;

import com.itemis.fluffyj.memory.api.FluffyVectorSegment;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

/**
 * Default implementation of a generic segment that holds vectorized (i. e. array) data.
 *
 * @param <T> - Type of data this segment holds.
 */
public abstract class FluffyVectorSegmentImpl<T> extends FluffySegmentImpl implements FluffyVectorSegment<T> {

    public FluffyVectorSegmentImpl(final MemorySegment backingSeg) {
        super(backingSeg);
    }

    public FluffyVectorSegmentImpl(final byte[] initialValue, final Arena arena) {
        super(initialValue, arena);
    }

    @Override
    public long byteSize() {
        return backingSeg.byteSize();
    }
}
