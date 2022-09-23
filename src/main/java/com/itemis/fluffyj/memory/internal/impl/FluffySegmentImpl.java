package com.itemis.fluffyj.memory.internal.impl;

import static java.lang.foreign.MemorySegment.allocateNative;

import com.itemis.fluffyj.memory.api.FluffySegment;

import java.lang.foreign.MemoryAddress;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.MemorySession;

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
     * @param layout - The new segment will have this {@link MemoryLayout}.
     * @param session - The new segment will be attached to this session, i. e. if this session is
     *        closed, the segment will not be alive anymore.
     */
    public FluffySegmentImpl(byte[] initialValue, MemoryLayout layout, MemorySession session) {
        this(allocateNative(layout, session));
        backingSeg.asByteBuffer().order(FLUFFY_SEGMENT_BYTE_ORDER).put(initialValue);
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
