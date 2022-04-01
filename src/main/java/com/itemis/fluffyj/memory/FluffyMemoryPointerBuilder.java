package com.itemis.fluffyj.memory;

import com.itemis.fluffyj.memory.api.FluffySegment;

import jdk.incubator.foreign.MemoryAddress;

public class FluffyMemoryPointerBuilder {

    public FluffyMemoryPointerAllocator<Long> toLong() {
        return new FluffyMemoryPointerAllocator<Long>();
    }

    public <T> FluffyMemoryPointerAllocator<T> to(FluffySegment<T> toHere) {
        return new FluffyMemoryPointerAllocator<T>(toHere);
    }

    public <T> FluffyMemoryPointerAllocator<T> to(MemoryAddress address) {
        return new FluffyMemoryPointerAllocator<T>(address);
    }
}
