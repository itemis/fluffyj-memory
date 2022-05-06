package com.itemis.fluffyj.memory.api;

import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;
import jdk.incubator.foreign.ResourceScope;

/**
 * A representation of an allocated area of off heap memory.
 */
public interface FluffySegment {

    /**
     * @see MemorySegment#address()
     */
    MemoryAddress address();

    /**
     * @see ResourceScope#isAlive()
     */
    boolean isAlive();
}
