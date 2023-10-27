package com.itemis.fluffyj.memory;

import static java.util.Objects.requireNonNull;

import java.lang.foreign.Arena;

/**
 * Provides shortcuts for dereferencing pointers.
 */
public final class FluffyMemoryDereferencer {

    private final FluffyMemorySegmentWrapper wrapper;
    private final Arena arena;

    /**
     * Construct a new instance.
     *
     * @param wrapper - Acquired by using
     *        {@link FluffyMemory#wrap(java.lang.foreign.MemorySegment)}.
     * @param nativePtr - The segment that was wrapped with the {@code wrapper}.
     */
    FluffyMemoryDereferencer(final FluffyMemorySegmentWrapper wrapper, final Arena arena) {
        this.wrapper = requireNonNull(wrapper, "wrapper");
        this.arena = requireNonNull(arena, "arena");
    }

    /**
     * Dereference an address to an instance of the provided target type.
     *
     * @param <T> - An instance of this type will be returned.
     * @param targetType - An instance of this class will be returned.
     * @return An instance of T that was read from an address of off heap memory.
     */
    public <T> T as(final Class<? extends T> targetType) {
        return wrapper.asPointerOf(targetType).allocate(arena).dereference();
    }
}
