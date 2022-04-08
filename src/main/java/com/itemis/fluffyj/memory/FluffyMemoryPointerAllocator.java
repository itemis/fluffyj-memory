package com.itemis.fluffyj.memory;

import static jdk.incubator.foreign.ResourceScope.globalScope;

import com.itemis.fluffyj.memory.api.FluffyPointer;
import com.itemis.fluffyj.memory.api.FluffySegment;
import com.itemis.fluffyj.memory.internal.PointerOfLong;

import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.ResourceScope;

/**
 * Helps with allocating pointers to off heap memory areas.
 *
 * @param <T> - The type of data the allocated pointer points to.
 */
public class FluffyMemoryPointerAllocator<T> {
    private final MemoryAddress initialValue;

    /**
     * Prepare to allocate a pointer to {@code toHere}.
     *
     * @param toHere - The constructed pointer will point to the address of this segment.
     */
    public FluffyMemoryPointerAllocator(FluffySegment<T> toHere) {
        initialValue = toHere.address();
    }

    /**
     * Prepare to allocate a pointer to the provided {@code address}.
     *
     * @param toHere - The constructed pointer will point to this address.
     */
    public FluffyMemoryPointerAllocator(MemoryAddress address) {
        initialValue = address;
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
    // As long as there is only the Long Pointer, there is no other way to cast this. The cast is
    // indeed unsafe but it won't produce any ClassCastExceptions since the value the pointer points
    // to will be interpreted as T which may be false but does not cause any error.
    @SuppressWarnings("unchecked")
    public FluffyPointer<T> allocate(ResourceScope scope) {
        return (FluffyPointer<T>) (initialValue == null ? new PointerOfLong(MemoryAddress.NULL, scope) : new PointerOfLong(initialValue, scope));
    }
}