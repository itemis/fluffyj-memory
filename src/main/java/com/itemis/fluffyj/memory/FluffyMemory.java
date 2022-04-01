package com.itemis.fluffyj.memory;

import com.itemis.fluffyj.exceptions.InstantiationNotPermittedException;

import jdk.incubator.foreign.MemorySegment;

public final class FluffyMemory {

    private FluffyMemory() {
        throw new InstantiationNotPermittedException();
    }

    public static FluffyMemorySegmentBuilder segment() {
        return new FluffyMemorySegmentBuilder();
    }

    public static FluffyMemoryPointerBuilder pointer() {
        return new FluffyMemoryPointerBuilder();
    }

    public static FluffyMemorySegmentWrapper wrap(MemorySegment nativeSeg) {
        return new FluffyMemorySegmentWrapper(nativeSeg);
    }

}
