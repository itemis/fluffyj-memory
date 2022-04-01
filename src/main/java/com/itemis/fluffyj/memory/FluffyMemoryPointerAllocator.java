package com.itemis.fluffyj.memory;

import static jdk.incubator.foreign.ResourceScope.globalScope;

import com.itemis.fluffyj.memory.api.FluffyPointer;
import com.itemis.fluffyj.memory.api.FluffySegment;
import com.itemis.fluffyj.memory.internal.PointerOfLong;

import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.ResourceScope;

/**
 * @param <T> - The type of data the allocated pointer points to.
 */
public class FluffyMemoryPointerAllocator<T> {
    private final MemoryAddress initialValue;

    public FluffyMemoryPointerAllocator() {
        initialValue = null;
    }

    public FluffyMemoryPointerAllocator(FluffySegment<T> toHere) {
        initialValue = toHere.address();
    }

    public FluffyMemoryPointerAllocator(MemoryAddress address) {
        initialValue = address;
    }

    public FluffyPointer<Long> allocate() {
        return allocate(globalScope());
    }

    public FluffyPointer<Long> allocate(ResourceScope scope) {
        return initialValue == null ? new PointerOfLong(MemoryAddress.NULL, scope) : new PointerOfLong(initialValue, scope);
    }
}