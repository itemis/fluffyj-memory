package com.itemis.fluffyj.memory.api;

import jdk.incubator.foreign.MemorySegment;

/**
 * A representation of an allocated area of off heap memory. Unlike {@link MemorySegment},
 * implementations of this class provide a way to access the array data contained in this segment in
 * a type safe fashion.
 *
 * @param <T> - Component type of the array this segment holds, e. g. for an array of Byte this
 *        would be Byte.
 */
public interface FluffyVectorSegment<T> extends FluffySegment {

    /**
     * The correctly typed value that this segment holds.
     */
    T[] getValue();

    /**
     * Convenience method to be used when casting things to the type of this segment's data is
     * required.
     *
     * @return The type of data this segment holds.
     */
    Class<? extends T[]> getContainedType();

    /**
     * @return The size of the array this segment holds in bytes.
     */
    long byteSize();
}
