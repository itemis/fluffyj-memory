package com.itemis.fluffyj.memory;

import static java.util.Objects.requireNonNull;

import com.itemis.fluffyj.memory.api.FluffyScalarSegment;
import com.itemis.fluffyj.memory.api.FluffyVectorSegment;

import jdk.incubator.foreign.MemoryAddress;

/**
 * Intermediate pointer creation helper.
 */
public final class FluffyMemoryPointerBuilder {

    /**
     * @param <T> - Type of data the pointer should point to.
     * @param toHere - The resulting pointer will point to this segment's address.
     * @return A {@link FluffyMemoryScalarPointerAllocator} instance that is able to allocate
     *         pointers to data of type {@code T}.
     */
    public <T> FluffyMemoryScalarPointerAllocator<T> to(FluffyScalarSegment<? extends T> toHere) {
        requireNonNull(toHere, "toHere");
        return new FluffyMemoryScalarPointerAllocator<T>(toHere);
    }

    /**
     * @param <T> - Type of data the pointer should point to.
     * @param toHere - The resulting pointer will point to this segment's address.
     * @return A {@link FluffyMemoryScalarPointerAllocator} instance that is able to allocate
     *         pointers to data of type {@code T}.
     */
    public <T> FluffyMemoryVectorPointerAllocator<T> toArray(FluffyVectorSegment<? extends T> toHere) {
        requireNonNull(toHere, "toHere");
        return new FluffyMemoryVectorPointerAllocator<>(toHere);
    }

    /**
     * @param <T> - Type of data the pointer should point to.
     * @param address - The resulting pointer will point to this address.
     * @return A {@link FluffyMemoryScalarPointerAllocator} instance that is able to allocate
     *         pointers to data of type {@code T}.
     */
    public FluffyMemoryTypedPointerBuilder to(MemoryAddress address) {
        requireNonNull(address, "address");
        return new FluffyMemoryTypedPointerBuilder(address);
    }

    public static final class FluffyMemoryTypedPointerBuilder {
        private final MemoryAddress address;

        public FluffyMemoryTypedPointerBuilder(MemoryAddress address) {
            requireNonNull(address, "address");
            this.address = address;
        }

        public <T> FluffyMemoryScalarPointerAllocator<? extends T> as(Class<? extends T> type) {
            return new FluffyMemoryScalarPointerAllocator<T>(address, type);
        }

        /**
         * @param byteSize - The size of the array the pointer shall point to in bytes.
         */
        public FluffyMemoryTypedArrayPointerBuilder asArray(int byteSize) {
            return new FluffyMemoryTypedArrayPointerBuilder(address, byteSize);
        }
    }

    public static final class FluffyMemoryTypedArrayPointerBuilder {
        private final long byteSize;
        private final MemoryAddress address;

        public FluffyMemoryTypedArrayPointerBuilder(MemoryAddress address, long byteSize) {
            this.address = requireNonNull(address, "address");
            this.byteSize = byteSize;
        }

        public <T> FluffyMemoryVectorPointerAllocator<? extends T> of(Class<? extends T[]> arrayType) {
            return new FluffyMemoryVectorPointerAllocator<>(address, byteSize, arrayType);
        }
    }
}
