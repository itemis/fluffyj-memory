package com.itemis.fluffyj.memory.internal;

import static java.lang.foreign.SegmentAllocator.newNativeArena;

import com.itemis.fluffyj.memory.api.FluffyScalarSegment;
import com.itemis.fluffyj.memory.api.FluffySegment;
import com.itemis.fluffyj.memory.internal.impl.FluffySegmentImpl;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.MemorySession;
import java.lang.foreign.ValueLayout;

/**
 * A {@link FluffySegment} that holds an {@link Long}.
 */
public class LongSegment extends FluffySegmentImpl implements FluffyScalarSegment<Long> {

    /**
     * Instances of this class hold this value as default if no other has been set upon
     * construction.
     */
    public static final long DEFAULT_VALUE = -1;

    /**
     * Allocate a new segment.
     *
     * @param initialValue - The new segment will hold this value.
     * @param session - The new segment will be attached to this session, i. e. if the session is
     *        closed, the new segment will not be alive anymore.
     */
    public LongSegment(long initialValue, MemorySession session) {
        this(newNativeArena(session).allocate(ValueLayout.JAVA_LONG, initialValue));
    }

    /**
     * Wrap the provided {@code backingSeg}. The constructed segment will be attached to the same
     * session as the {@code backingSeg}.
     *
     * @param backingSeg - The raw segment to wrap.
     */
    public LongSegment(MemorySegment backingSeg) {
        super(backingSeg);
    }

    @Override
    public Class<Long> getContainedType() {
        return Long.class;
    }

    @Override
    public Long getValue() {
        return backingSeg.get(ValueLayout.JAVA_LONG, 0);
    }
}
