package com.itemis.fluffyj.memory.internal.impl;

import static java.lang.foreign.MemorySegment.ofAddress;

import com.itemis.fluffyj.memory.api.FluffySegment;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentAllocator;
import java.lang.foreign.SegmentScope;
import java.lang.foreign.ValueLayout;

/**
 * Default implementation of a segment.
 */
public abstract class FluffySegmentImpl implements FluffySegment {

    protected final MemorySegment backingSeg;
    protected final SegmentScope scope;

    /**
     * Wrap the provided {@link MemorySegment}.
     *
     * @param backingSeg - The raw segment to wrap.
     */
    public FluffySegmentImpl(MemorySegment backingSeg) {
        this.backingSeg = backingSeg;
        this.scope = backingSeg.scope();
    }

    /**
     * Allocate new segment.
     *
     * @param initialValue - The new segment will hold this value.
     * @param scope - The new segment will be attached to this scope.
     */
    public FluffySegmentImpl(byte[] initialValue, SegmentScope scope) {
        this(SegmentAllocator.nativeAllocator(scope).allocateArray(ValueLayout.JAVA_BYTE, initialValue));
    }

    @Override
    public boolean isAlive() {
        return scope.isAlive();
    }

    @Override
    public MemorySegment address() {
        return ofAddress(rawAddress(), 0, scope);
    }

    @Override
    public long rawAddress() {
        return backingSeg.address();
    }
}
