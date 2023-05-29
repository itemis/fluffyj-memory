package com.itemis.fluffyj.memory;

import static java.util.Objects.requireNonNull;

import java.lang.foreign.MemorySegment;

/**
 * Provides shortcuts for dereferencing pointers.
 */
public final class FluffyMemoryDereferencer {

    private FluffyMemorySegmentWrapper wrapper;
    private MemorySegment nativePtr;

    /**
     * Construct a new instance.
     *
     * @param wrapper - Acquired by using
     *        {@link FluffyMemory#wrap(java.lang.foreign.MemorySegment)}.
     * @param nativePtr - The segment that was wrapped with the {@code wrapper}.
     */
    FluffyMemoryDereferencer(FluffyMemorySegmentWrapper wrapper, MemorySegment nativePtr) {
        this.wrapper = requireNonNull(wrapper, "wrapper");
        this.nativePtr = requireNonNull(nativePtr, "nativePtr");
    }

    /**
     * Dereference an address to an instance of the provided target type.
     *
     * @param <T> - An instance of this type will be returned.
     * @param targetType - An instance of this class will be returned.
     * @return An instance of T that was read from an address of off heap memory.
     */
    public <T> T as(Class<? extends T> targetType) {
        return wrapper.asPointerOf(targetType).allocate(nativePtr.scope()).dereference();
    }
}
