package com.itemis.fluffyj.memory;

import jdk.incubator.foreign.MemorySegment;

public final class FluffyMemorySegmentBuilder {

    public FluffyMemoryLongSegmentBuilder ofLong() {
        return new FluffyMemoryLongSegmentBuilder(LongSegment.DEFAULT_VALUE);
    }

    public FluffyMemoryLongSegmentBuilder ofLong(long initialValue) {
        return new FluffyMemoryLongSegmentBuilder(initialValue);
    }

    public FluffyMemorySegmentWrapper wrap(MemorySegment nativeSeg) {
        return new FluffyMemorySegmentWrapper(nativeSeg);
    }
}
