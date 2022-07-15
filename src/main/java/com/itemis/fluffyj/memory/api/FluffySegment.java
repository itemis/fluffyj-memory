package com.itemis.fluffyj.memory.api;

import java.nio.ByteOrder;

import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;
import jdk.incubator.foreign.ResourceScope;

/**
 * A representation of an allocated area of off heap memory.
 */
public interface FluffySegment {

    /**
     * The byte order used within a segment, i. e. in which direction to read a value's bytes.
     */
    public static final ByteOrder FLUFFY_SEGMENT_BYTE_ORDER = ByteOrder.nativeOrder();

    /**
     * @see MemorySegment#address()
     */
    MemoryAddress address();

    /**
     * @see ResourceScope#isAlive()
     */
    boolean isAlive();
}
