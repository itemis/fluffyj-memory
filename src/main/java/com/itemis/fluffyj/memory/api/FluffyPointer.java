package com.itemis.fluffyj.memory.api;

import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;
import jdk.incubator.foreign.ResourceScope;

/**
 * Provides convenience methods that help with working with {@link MemorySegment}s that hold
 * addresses to other segments.
 *
 * @param <T> - The kind of data that is accessible via this pointer.
 */
public interface FluffyPointer<T> {

    /**
     * @see ResourceScope#isAlive()
     */
    boolean isAlive();

    /**
     * @return The address this pointer points to or {@link MemoryAddress#NULL} if this is a null
     *         pointer.
     */
    MemoryAddress address();

    /**
     * @return The value that this pointer points to interpreted as type {@code T}.
     */
    T dereference();

    /**
     * @see #address()
     */
    MemoryAddress getValue();

}
