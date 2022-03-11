package com.itemis.fluffyj.memory;

import jdk.incubator.foreign.MemoryAddress;

public class FluffyMemoryPointerBuilder {

    public FluffyMemoryPointerOfLongBuilder ofLong() {
        return new FluffyMemoryPointerOfLongBuilder();
    }

    public FluffyMemoryPointerOfLongBuilder ofLong(LongSegment toHere) {
        return new FluffyMemoryPointerOfLongBuilder(toHere);
    }

    public FluffyMemoryPointerOfLongBuilder ofLong(MemoryAddress address) {
        return new FluffyMemoryPointerOfLongBuilder(address);
    }
}
