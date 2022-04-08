package com.itemis.fluffyj.memory.api;

import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;
import jdk.incubator.foreign.ResourceScope;

/**
 * A representation of an allocated area of off heap memory. Unlike {@link MemorySegment},
 * implementations of this class provide a way to access the segment's contents in a type safe
 * fashion.
 *
 * @param <T> - Type of data this segment holds.
 */
public interface FluffySegment<T> {

    /**
     * @see MemorySegment#address()
     */
    MemoryAddress address();

    /**
     * @see ResourceScope#isAlive()
     */
    boolean isAlive();

    /**
     * The correctly typed value that this segment holds.
     */
    T getValue();
}
