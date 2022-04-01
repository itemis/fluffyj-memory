package com.itemis.fluffyj.memory;

import com.itemis.fluffyj.memory.internal.LongSegment;

public final class FluffyMemorySegmentBuilder {

    public FluffyMemorySegmentAllocator<Long> ofLong() {
        return new FluffyMemorySegmentAllocator<Long>(LongSegment.DEFAULT_VALUE);
    }

    public FluffyMemorySegmentAllocator<Long> of(long initialValue) {
        return new FluffyMemorySegmentAllocator<Long>(initialValue);
    }
}
