package com.itemis.fluffyj.memory;

import static jdk.incubator.foreign.ResourceScope.globalScope;

import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.ResourceScope;

public class FluffyMemoryPointerOfLongBuilder {
    private final MemoryAddress initialValue;

    public FluffyMemoryPointerOfLongBuilder() {
        initialValue = null;
    }

    public FluffyMemoryPointerOfLongBuilder(LongSegment toHere) {
        initialValue = toHere.address();
    }

    public FluffyMemoryPointerOfLongBuilder(MemoryAddress address) {
        initialValue = address;
    }

    public PointerOfLong allocate() {
        return allocate(globalScope());
    }

    public PointerOfLong allocate(ResourceScope scope) {
        return initialValue == null ? new PointerOfLong(MemoryAddress.NULL, scope) : new PointerOfLong(initialValue, scope);
    }
}