package com.itemis.fluffyj.memory;

import com.itemis.fluffyj.memory.api.FluffySegment;

import jdk.incubator.foreign.MemoryAddress;

/**
 * Intermediate pointer creation helper.
 */
public class FluffyMemoryPointerBuilder {

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
    public FluffyMemoryTypedPointerBuilder to(MemoryAddress address) {
        return new FluffyMemoryTypedPointerBuilder(address);
    }

    public static final class FluffyMemoryTypedPointerBuilder {
        private final MemoryAddress address;

        public FluffyMemoryTypedPointerBuilder(MemoryAddress address) {
            this.address = address;
        }

        public FluffyMemoryPointerAllocator<Long> asLong() {
            return new FluffyMemoryPointerAllocator<Long>(address);
        }
    }
}
