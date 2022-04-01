package com.itemis.fluffyj.memory;

import com.itemis.fluffyj.memory.api.FluffySegment;
import com.itemis.fluffyj.memory.internal.LongSegment;

import jdk.incubator.foreign.MemorySegment;

public class FluffyMemorySegmentWrapper {

    private final MemorySegment nativeSegment;

    public FluffyMemorySegmentWrapper(MemorySegment nativeSegment) {
        this.nativeSegment = nativeSegment;
    }

    public FluffySegment<Long> asLong() {
        return new LongSegment(nativeSegment);
    }
}
