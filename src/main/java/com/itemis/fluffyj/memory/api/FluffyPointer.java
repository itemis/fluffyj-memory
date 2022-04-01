package com.itemis.fluffyj.memory.api;

import jdk.incubator.foreign.MemoryAddress;

/**
 * @param <T> - The kind of data this pointer points to.
 */
public interface FluffyPointer<T> {

    boolean isAlive();

    MemoryAddress address();

    T dereference();

    MemoryAddress getValue();

}
