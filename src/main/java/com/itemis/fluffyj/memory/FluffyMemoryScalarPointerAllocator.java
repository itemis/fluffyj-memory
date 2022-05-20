package com.itemis.fluffyj.memory;

import static java.util.Objects.requireNonNull;
import static jdk.incubator.foreign.ResourceScope.globalScope;

import com.itemis.fluffyj.memory.api.FluffyPointer;
import com.itemis.fluffyj.memory.api.FluffyScalarPointer;
import com.itemis.fluffyj.memory.api.FluffyScalarSegment;
import com.itemis.fluffyj.memory.internal.PointerOfLong;

import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.ResourceScope;

/**
 * Helps with allocating pointers to off heap memory areas.
 *
 * @param <T> - The type of data the allocated pointer points to.
 */
public final class FluffyMemoryScalarPointerAllocator<T> {
    private final MemoryAddress initialValue;
    // Will be used as soon as there are multiple types of segments to allocate.
    @SuppressWarnings("unused")
    private final Class<? extends T> type;

    /**
     * Prepare to allocate a pointer to {@code toHere}.
     *
     * @param toHere - The constructed pointer will point to the address of this segment.
     */
    public FluffyMemoryScalarPointerAllocator(FluffyScalarSegment<? extends T> toHere) {
        requireNonNull(toHere, "toHere");
        initialValue = toHere.address();
        type = toHere.getContainedType();
    }

    /**
     * Prepare to allocate a pointer to the provided {@code address}.
     *
     * @param toHere - The constructed pointer will point to this address.
     * @param typeOfData - Type of data the segment the provided address points to.
     */
    public FluffyMemoryScalarPointerAllocator(MemoryAddress address, Class<? extends T> typeOfData) {
        initialValue = requireNonNull(address, "address");
        type = requireNonNull(typeOfData, "typeOfData");
    }

    /**
     * Allocate the pointer. Its scope will be the global scope.
     *
     * @return A new {@link FluffyPointer} instance.
     */
    public FluffyScalarPointer<? extends T> allocate() {
        return allocate(globalScope());
    }

    /**
     * Allocate the pointer and attach it to the provided {@code scope}.
     *
     * @param scope - {@link ResourceScope} of the pointer.
     * @return A new {@link FluffyPointer} instance.
     */
    // The cast is indeed unsafe but it won't produce any ClassCastExceptions since the value the
    // pointer points to will be interpreted as T which may be false but does not cause any error.
    @SuppressWarnings("unchecked")
    public FluffyScalarPointer<? extends T> allocate(ResourceScope scope) {
        requireNonNull(scope, "scope");
        return (FluffyScalarPointer<? extends T>) new PointerOfLong(initialValue, scope);
    }
}