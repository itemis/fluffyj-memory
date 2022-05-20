package com.itemis.fluffyj.memory.api;

import jdk.incubator.foreign.MemorySegment;

/**
 * A representation of an allocated area of off heap memory. Unlike {@link MemorySegment},
 * implementations of this class provide a way to access the segment's contents in a type safe
 * fashion.
 *
 * @param <T> - Type of data this segment holds.
 */
public interface FluffyScalarSegment<T> extends FluffySegment {

    /**
     * The correctly typed value that this segment holds.
     */
    T getValue();

    /**
     * Convenience method to be used when casting things to the type of this segment's data is
     * required.
     *
     * @return The type of data this segment holds.
     */
    Class<? extends T> getContainedType();
}
