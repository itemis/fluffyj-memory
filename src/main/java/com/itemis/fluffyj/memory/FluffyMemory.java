package com.itemis.fluffyj.memory;

import static java.util.Objects.requireNonNull;

import com.itemis.fluffyj.exceptions.InstantiationNotPermittedException;
import com.itemis.fluffyj.memory.api.FluffySegment;

import jdk.incubator.foreign.MemorySegment;

/**
 * Entry point to the FluffyJ Memory API
 */
public final class FluffyMemory {

    private FluffyMemory() {
        throw new InstantiationNotPermittedException();
    }

    /**
     * Prepare to allocate an area of off heap memory.
     */
    public static FluffyMemorySegmentBuilder segment() {
        return new FluffyMemorySegmentBuilder();
    }

    /**
     * Prepare to construct a pointer to an off heap memory area.
     */
    public static FluffyMemoryPointerBuilder pointer() {
        return new FluffyMemoryPointerBuilder();
    }

    /**
     * Prepare to wrap a {@link MemorySegment} into a {@link FluffySegment}. This can be used to
     * access contents of a {@link MemorySegment} in a type safe way.
     *
     * @param nativeSeg - The raw {@link MemorySegment} to wrap.
     */
    public static FluffyMemorySegmentWrapper wrap(MemorySegment nativeSeg) {
        requireNonNull(nativeSeg, "nativeSeg");
        return new FluffyMemorySegmentWrapper(nativeSeg);
    }

}
