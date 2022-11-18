package com.itemis.fluffyj.memory.api;

import java.lang.foreign.MemoryAddress;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.MemorySession;

/**
 * A representation of an allocated area of off heap memory.
 */
public interface FluffySegment {

    /**
     * @see MemorySession#isAlive()
     */
    boolean isAlive();

    /**
     * @see MemorySegment#address()
     */
    MemoryAddress address();
}
