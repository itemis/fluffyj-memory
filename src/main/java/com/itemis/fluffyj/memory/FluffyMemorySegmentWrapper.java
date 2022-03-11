package com.itemis.fluffyj.memory;

import jdk.incubator.foreign.MemorySegment;

public class FluffyMemorySegmentWrapper {

    private final MemorySegment nativeSegment;

    public FluffyMemorySegmentWrapper(MemorySegment nativeSegment) {
        this.nativeSegment = nativeSegment;
    }

    public LongSegment asLong() {
        return new LongSegment(nativeSegment);
    }
}
