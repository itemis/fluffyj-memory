package com.itemis.fluffyj.memory.api;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentScope;

/**
 * Provides convenience methods that help with working with {@link MemorySegment}s that hold
 * addresses to other segments.
 */
public interface FluffyPointer {

    /**
     * @see SegmentScope#isAlive()
     */
    boolean isAlive();

    /**
     * @return The address of this pointer's segment, i. e. the address of the pointer itself.
     */
    long rawAddress();

    /**
     * @return The address of this pointer's segment modeled as a zero size {@link MemorySegment}.
     *         Note that {@link #rawAddress()} == {@link address().address()}
     */
    MemorySegment address();

    /**
     * The raw address this pointer points to. Will be 0L if this is a null pointer.
     */
    long getRawValue();

    /**
     * The address this pointer points to. Will be {@link MemorySegment#NULL} if this is a null
     * pointer.
     */
    MemorySegment getValue();
}
