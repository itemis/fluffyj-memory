package com.itemis.fluffyj.memory.internal;

import com.itemis.fluffyj.memory.api.FluffyScalarSegment;
import com.itemis.fluffyj.memory.api.FluffySegment;
import com.itemis.fluffyj.memory.internal.impl.FluffySegmentImpl;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
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
     * @param arena - The new segment will be attached to this arena.
     */
    public IntSegment(final int initialValue, final Arena arena) {
        this(arena.allocateFrom(ValueLayout.JAVA_INT, initialValue));
    }

    /**
     * Wrap the provided {@code backingSeg}. The constructed segment will be attached to the same
     * arena as the {@code backingSeg}.
     *
     * @param backingSeg - The raw segment to wrap.
     */
    public IntSegment(final MemorySegment backingSeg) {
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
