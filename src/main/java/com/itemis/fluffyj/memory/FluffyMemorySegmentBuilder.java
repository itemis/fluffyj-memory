package com.itemis.fluffyj.memory;

/**
 * Intermediate segment creation stage.
 */
public final class FluffyMemorySegmentBuilder {

    /**
     * @return A {@link FluffyMemoryScalarSegmentAllocator} instance that is able to allocate
     *         segments that hold the provided {@code initialValue}.
     */
    public <T> FluffyMemoryScalarSegmentAllocator<T> of(T initialValue) {
        return new FluffyMemoryScalarSegmentAllocator<>(initialValue);
    }

    /**
     * @return A {@link FluffyMemoryVectorSegmentAllocator} instance that is able to allocate
     *         segments that hold the provided {@code initialValue}.
     */
    public <T> FluffyMemoryVectorSegmentAllocator<T> ofArray(T[] initialValue) {
        return new FluffyMemoryVectorSegmentAllocator<>(initialValue);
    }
}
