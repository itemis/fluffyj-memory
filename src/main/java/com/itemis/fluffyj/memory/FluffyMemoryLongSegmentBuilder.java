package com.itemis.fluffyj.memory;

import static jdk.incubator.foreign.ResourceScope.globalScope;

import jdk.incubator.foreign.ResourceScope;

public class FluffyMemoryLongSegmentBuilder {
    private final long initialValue;

    public FluffyMemoryLongSegmentBuilder(long initialValue) {
        this.initialValue = initialValue;
    }

    public LongSegment allocate() {
        return allocate(globalScope());
    }

    public LongSegment allocate(ResourceScope scope) {
        return new LongSegment(initialValue, scope);
    }
}
