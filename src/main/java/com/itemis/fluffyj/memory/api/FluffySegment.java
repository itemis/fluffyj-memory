package com.itemis.fluffyj.memory.api;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentScope;

/**
 * A representation of an allocated area of off heap memory.
 */
public interface FluffySegment {

    /**
     * @see SegmentScope#isAlive()
     */
    boolean isAlive();

    /**
     * @see MemorySegment#address()
     */
    long rawAddress();

    /**
     * @return The address of this segment modeled as a zero size {@link MemorySegment}. Note that
     *         {@link #rawAddress()} == {@link address().address()}
     */
    MemorySegment address();
}
