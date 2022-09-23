package com.itemis.fluffyj.memory.api;

import java.lang.foreign.MemoryAddress;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.MemorySession;
import java.nio.ByteOrder;

/**
 * Provides convenience methods that help with working with {@link MemorySegment}s that hold
 * addresses to other segments.
 */
public interface FluffyPointer {

    /**
     * The byte order used within a pointer segment, i. e. in which direction to read an address's
     * bytes.
     */
    ByteOrder FLUFFY_POINTER_BYTE_ORDER = ByteOrder.nativeOrder();

    /**
     * @see MemorySession#isAlive()
     */
    boolean isAlive();

    /**
     * @return The address this pointer points to or {@link MemoryAddress#NULL} if this is a null
     *         pointer.
     */
    MemoryAddress address();

    /**
     * @see #address()
     */
    MemoryAddress getValue();
}
