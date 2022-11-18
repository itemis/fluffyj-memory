package com.itemis.fluffyj.memory.internal.impl;

import static java.lang.foreign.SegmentAllocator.newNativeArena;
import static java.util.Objects.requireNonNull;

import com.itemis.fluffyj.memory.api.FluffySegment;

import java.lang.foreign.MemoryAddress;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.MemorySession;
import java.lang.foreign.ValueLayout;

/**
 * Default implementation of a segment.
 */
public abstract class FluffySegmentImpl implements FluffySegment {

    protected final MemorySegment backingSeg;
    protected final MemorySession session;

    /**
     * Wrap the provided {@link MemorySegment}.
     *
     * @param backingSeg - The raw segment to wrap.
     */
    public FluffySegmentImpl(MemorySegment backingSeg) {
        this.backingSeg = backingSeg;
        this.session = backingSeg.session();
    }

    /**
     * Allocate new segment.
     *
     * @param initialValue - The new segment will hold this value.
     * @param session - The new segment will be attached to this session, i. e. if this session is
     *        closed, the segment will not be alive anymore.
     */
    public FluffySegmentImpl(byte[] initialValue, MemorySession session) {
        this(newNativeArena(requireNonNull(initialValue, "initialValue").length, requireNonNull(session, "session"))
            .allocateArray(ValueLayout.JAVA_BYTE, initialValue));
    }

    @Override
    public boolean isAlive() {
        return session.isAlive();
    }

    @Override
    public MemoryAddress address() {
        return backingSeg.address();
    }
}
