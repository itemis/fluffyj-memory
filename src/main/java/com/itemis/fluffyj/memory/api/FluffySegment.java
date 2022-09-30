package com.itemis.fluffyj.memory.api;

import java.lang.foreign.MemoryAddress;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.MemorySession;
import java.nio.ByteOrder;

/**
 * A representation of an allocated area of off heap memory.
 */
public interface FluffySegment {

    /**
     * The byte order used within a segment, i. e. in which direction to read a value's bytes.
     */
    ByteOrder FLUFFY_SEGMENT_BYTE_ORDER = ByteOrder.nativeOrder();

    /**
     * @see MemorySegment#address()
     */
    MemoryAddress address();

    /**
     * @see MemorySession#isAlive()
     */
    boolean isAlive();
}
