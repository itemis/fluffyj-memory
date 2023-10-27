package com.itemis.fluffyj.memory.internal;

import com.itemis.fluffyj.memory.api.FluffyScalarSegment;
import com.itemis.fluffyj.memory.api.FluffySegment;
import com.itemis.fluffyj.memory.internal.impl.FluffySegmentImpl;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
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
     * @param arena - The new segment will be attached to this arena.
     */
    public LongSegment(final long initialValue, final Arena arena) {
        this(arena.allocate(ValueLayout.JAVA_LONG, initialValue));
    }

    /**
     * Wrap the provided {@code backingSeg}. The constructed segment will be attached to the same
     * arena as the {@code backingSeg}.
     *
     * @param backingSeg - The raw segment to wrap.
     */
    public LongSegment(final MemorySegment backingSeg) {
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
