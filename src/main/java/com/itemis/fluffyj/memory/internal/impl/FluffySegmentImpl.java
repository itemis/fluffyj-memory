package com.itemis.fluffyj.memory.internal.impl;

import static java.lang.foreign.MemorySegment.ofAddress;

import com.itemis.fluffyj.memory.api.FluffySegment;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.MemorySegment.Scope;
import java.lang.foreign.ValueLayout;

/**
 * Default implementation of a segment.
 */
public abstract class FluffySegmentImpl implements FluffySegment {

    protected final MemorySegment backingSeg;
    protected final Scope scope;

    /**
     * Wrap the provided {@link MemorySegment}.
     *
     * @param backingSeg - The raw segment to wrap.
     */
    public FluffySegmentImpl(final MemorySegment backingSeg) {
        this.backingSeg = backingSeg;
        this.scope = backingSeg.scope();
    }

    /**
     * Allocate new segment.
     *
     * @param initialValue - The new segment will hold this value.
     * @param arena - The new segment will be attached to this arena.
     */
    public FluffySegmentImpl(final byte[] initialValue, final Arena arena) {
        this(arena.allocateFrom(ValueLayout.JAVA_BYTE, initialValue));
    }

    @Override
    public boolean isAlive() {
        return scope.isAlive();
    }

    @Override
    public MemorySegment address() {
        return ofAddress(rawAddress());
    }

    @Override
    public long rawAddress() {
        return backingSeg.address();
    }
}
