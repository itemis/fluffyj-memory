package com.itemis.fluffyj.memory;

import com.itemis.fluffyj.memory.internal.LongSegment;

/**
 * Intermediate segment creation stage.
 */
public final class FluffyMemorySegmentBuilder {

    /**
     * @return A {@link FluffyMemorySegmentAllocator} instance that is able to allocate segments
     *         that hold data of type {@link Long}.
     */
    public FluffyMemorySegmentAllocator<Long> ofLong() {
        return new FluffyMemorySegmentAllocator<Long>(LongSegment.DEFAULT_VALUE);
    }

    /**
     * @return A {@link FluffyMemorySegmentAllocator} instance that is able to allocate segments
     *         that hold the provided {@code initialValue}.
     */
    public FluffyMemorySegmentAllocator<Long> of(long initialValue) {
        return new FluffyMemorySegmentAllocator<Long>(initialValue);
    }

    /**
     * @return A {@link FluffyMemorySegmentAllocator} instance that is able to allocate segments
     *         that hold the provided {@code initialValue}.
     */
    public FluffyMemorySegmentAllocator<byte[]> of(byte[] initialValue) {
        return new FluffyMemorySegmentAllocator<byte[]>(initialValue);
    }
}
