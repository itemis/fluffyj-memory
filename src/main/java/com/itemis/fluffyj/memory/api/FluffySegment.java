package com.itemis.fluffyj.memory.api;

import jdk.incubator.foreign.MemoryAddress;

/**
 * @param <T> - Type of data this segment holds.
 */
public interface FluffySegment<T> {

    MemoryAddress address();

    boolean isAlive();

    T getValue();
}
