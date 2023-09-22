package com.itemis.fluffyj.memory;

import static java.util.Objects.requireNonNull;

import com.itemis.fluffyj.exceptions.InstantiationNotPermittedException;
import com.itemis.fluffyj.memory.api.FluffySegment;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

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
    public static FluffyMemorySegmentWrapper wrap(final MemorySegment nativeSeg) {
        requireNonNull(nativeSeg, "nativeSeg");
        return new FluffyMemorySegmentWrapper(nativeSeg);
    }

    /**
     * Dereference the provided {@code nativePtr} into a typed instance.
     *
     * @param nativePtr - Dereference the address found inside this ptr segment.
     * @return - Entry point for easy dereferenciation.
     */
    public static FluffyMemoryDereferencer dereference(final MemorySegment nativePtr) {
        return new FluffyMemoryDereferencer(wrap(nativePtr), Arena.ofAuto());
    }

    /**
     * Dereference the provided {@code address} into a typed instance.
     *
     * @param address - Dereference this address.
     * @return - Entry point for easy dereferenciation.
     */
    public static FluffyMemoryDereferencer dereference(final long address) {
        return dereference(MemorySegment.ofAddress(address));
    }

}
