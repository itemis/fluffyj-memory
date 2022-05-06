package com.itemis.fluffyj.memory;

import static java.util.Objects.requireNonNull;
import static jdk.incubator.foreign.ResourceScope.globalScope;

import com.itemis.fluffyj.memory.api.FluffyPointer;
import com.itemis.fluffyj.memory.api.FluffySegment;
import com.itemis.fluffyj.memory.internal.PointerOfBlob;

import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.ResourceScope;

/**
 * Helps with allocating pointers to off heap memory areas that hold arrays.
 *
 * @param <T> - The type of data the allocated pointer points to.
 */
public final class FluffyMemoryArrayPointerAllocator<T> {
    private final MemoryAddress initialValue;
    private final int byteCount;
    // Will be used as soon as there are multiple types of segments to allocate.
    @SuppressWarnings("unused")
    private final Class<T> type;

    /**
     * Prepare to allocate a pointer to {@code toHere}.
     *
     * @param toHere - The constructed pointer will point to the address of this segment.
     */
    public FluffyMemoryArrayPointerAllocator(FluffySegment<T> toHere) {
        requireNonNull(toHere, "toHere");
        initialValue = toHere.address();
        byteCount = toHere.byteSize();
        type = toHere.getContainedType();
    }

    /**
     * Prepare to allocate a pointer to the provided {@code address}.
     *
     * @param toHere - The constructed pointer will point to this address.
     * @param typeOfData - Type of data the segment the provided address points to.
     */
    public FluffyMemoryArrayPointerAllocator(MemoryAddress address, int byteCount, Class<T> typeOfData) {
        this.initialValue = requireNonNull(address, "address");
        this.byteCount = byteCount;
        this.type = requireNonNull(typeOfData, "typeOfData");
    }

    /**
     * Allocate the pointer. Its scope will be the global scope.
     *
     * @return A new {@link FluffyPointer} instance.
     */
    public FluffyPointer<T> allocate() {
        return allocate(globalScope());
    }

    /**
     * Allocate the pointer and attach it to the provided {@code scope}.
     *
     * @param scope - {@link ResourceScope} of the pointer.
     * @return A new {@link FluffyPointer} instance.
     */
    // The cast is indeed unsafe but it won't produce any ClassCastExceptions since the value the
    // pointer points to will be interpreted as T anyway which may be false but does not cause any
    // error.
    @SuppressWarnings("unchecked")
    public FluffyPointer<T> allocate(ResourceScope scope) {
        requireNonNull(scope, "scope");

        return (FluffyPointer<T>) new PointerOfBlob(initialValue, byteCount, scope);
    }
}