package com.itemis.fluffyj.memory;

import com.itemis.fluffyj.memory.api.FluffySegment;

import jdk.incubator.foreign.MemoryAddress;

/**
 * Intermediate pointer creation helper.
 */
public class FluffyMemoryPointerBuilder {

    /**
     * @return A {@link FluffyMemoryPointerAllocator} instance that is able to allocate pointers to
     *         {@link Long Longs}.
     */
    public FluffyMemoryPointerAllocator<Long> toLong() {
        return new FluffyMemoryPointerAllocator<Long>();
    }

    /**
     * @param <T> - Type of data the pointer should point to.
     * @param toHere - The resulting pointer will point to this segment's address.
     * @return A {@link FluffyMemoryPointerAllocator} instance that is able to allocate pointers to
     *         data of type {@code T}.
     */
    public <T> FluffyMemoryPointerAllocator<T> to(FluffySegment<T> toHere) {
        return new FluffyMemoryPointerAllocator<T>(toHere);
    }

    /**
     * @param <T> - Type of data the pointer should point to.
     * @param address - The resulting pointer will point to this address.
     * @return A {@link FluffyMemoryPointerAllocator} instance that is able to allocate pointers to
     *         data of type {@code T}.
     */
    public <T> FluffyMemoryPointerAllocator<T> to(MemoryAddress address) {
        return new FluffyMemoryPointerAllocator<T>(address);
    }
}
