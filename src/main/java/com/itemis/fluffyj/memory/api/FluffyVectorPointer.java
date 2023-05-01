package com.itemis.fluffyj.memory.api;

import java.lang.foreign.MemorySegment;

/**
 * A pointer to a segment that holds a vector (i. e. an array) value.
 *
 * @param <T> - The component type of the array this pointer points to, e. g. this would be Byte for
 *        an array of Byte.
 */
public interface FluffyVectorPointer<T> extends FluffyPointer {

    /**
     * @return The value that this pointer points to interpreted as an array type {@code T[]}.
     */
    T[] dereference();

    /**
     * @return The native segment that holds the value that this pointer points to.
     */
    MemorySegment rawDereference();
}
