package com.itemis.fluffyj.memory;

import jdk.incubator.foreign.MemorySegment;
import jdk.incubator.foreign.ResourceScope;

public final class FluffyMemorySegmentBuilder {

    private long initialLongValue = LongSegment.DEFAULT_VALUE;

    public FluffyMemorySegmentBuilder ofLong() {
        return this;
    }

    public FluffyMemorySegmentBuilder ofLong(long initialValue) {
        initialLongValue = initialValue;
        return this;
    }

    public LongSegment allocate() {
        return allocate(ResourceScope.globalScope());
    }

    public LongSegment allocate(ResourceScope scope) {
        return new LongSegment(initialLongValue, scope);
    }

    public LongSegment from(MemorySegment nativeSeg) {
        return new LongSegment(nativeSeg);
    }
}
