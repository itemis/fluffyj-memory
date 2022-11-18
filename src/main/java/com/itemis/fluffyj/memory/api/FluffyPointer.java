package com.itemis.fluffyj.memory.api;

import java.lang.foreign.MemoryAddress;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.MemorySession;

/**
 * Provides convenience methods that help with working with {@link MemorySegment}s that hold
 * addresses to other segments.
 */
public interface FluffyPointer {

    /**
     * @see MemorySession#isAlive()
     */
    boolean isAlive();

    /**
     * @return The address of this pointer's segment, i. e. the address of the pointer itself.
     */
    MemoryAddress address();

    /**
     * The address this pointer points to. Will be {@link MemoryAddress#NULL} if this is a null
     * pointer.
     */
    MemoryAddress getValue();
}
