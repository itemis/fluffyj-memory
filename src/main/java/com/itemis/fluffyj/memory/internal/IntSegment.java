package com.itemis.fluffyj.memory.internal;

import static java.lang.foreign.SegmentAllocator.newNativeArena;

import com.itemis.fluffyj.memory.api.FluffyScalarSegment;
import com.itemis.fluffyj.memory.api.FluffySegment;
import com.itemis.fluffyj.memory.internal.impl.FluffySegmentImpl;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.MemorySession;
import java.lang.foreign.ValueLayout;

/**
 * A {@link FluffySegment} that holds an {@link Integer}.
 */
public class IntSegment extends FluffySegmentImpl implements FluffyScalarSegment<Integer> {

    /**
     * Instances of this class hold this value as default if no other has been set upon
     * construction.
     */
    public static final int DEFAULT_VALUE = -1;

    /**
     * Allocate a new segment.
     *
     * @param initialValue - The new segment will hold this value.
     * @param session - The new segment will be attached to this session, i. e. if the session is
     *        closed, the new segment will not be alive anymore.
     */
    public IntSegment(int initialValue, MemorySession session) {
        this(newNativeArena(session).allocate(ValueLayout.JAVA_INT, initialValue));
    }

    /**
     * Wrap the provided {@code backingSeg}. The constructed segment will be attached to the same
     * session as the {@code backingSeg}.
     *
     * @param backingSeg - The raw segment to wrap.
     */
    public IntSegment(MemorySegment backingSeg) {
        super(backingSeg);
    }

    @Override
    public Class<Integer> getContainedType() {
        return Integer.class;
    }

    @Override
    public Integer getValue() {
        return backingSeg.get(ValueLayout.JAVA_INT, 0);
    }
}
