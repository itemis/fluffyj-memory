package com.itemis.fluffyj.memory;

public final class FluffyMemory {

    public static FluffyMemorySegmentBuilder segment() {
        return new FluffyMemorySegmentBuilder();
    }

    public static FluffyMemoryPointerBuilder pointer() {
        return new FluffyMemoryPointerBuilder();
    }

}
