package com.itemis.fluffyj.memory.internal;

import static java.util.Objects.requireNonNull;
import static jdk.incubator.foreign.MemoryLayouts.JAVA_LONG;

import com.itemis.fluffyj.memory.api.FluffySegment;

import java.nio.ByteBuffer;

import jdk.incubator.foreign.MemoryLayout;
import jdk.incubator.foreign.MemorySegment;
import jdk.incubator.foreign.ResourceScope;

/**
 * A {@link FluffySegment} that holds a {@link Long}.
 */
public class LongSegment extends FluffySegmentImpl<Long> {

    private static final MemoryLayout MY_LAYOUT = JAVA_LONG;
    /**
     * Instances of this class hold this value as default if no other has been set upon
     * construction.
     */
    public static final long DEFAULT_VALUE = -1;

    /**
     * Allocate a new segment.
     *
     * @param initialValue - The new segment will hold this value.
     * @param scope - The new segment will be attached to this scope, i. e. if the scope is closed,
     *        the new segment will not be alive anymore.
     */
    public LongSegment(long initialValue, ResourceScope scope) {
        super(ByteBuffer.allocate((int) MY_LAYOUT.byteSize()).putLong(initialValue).array(), MY_LAYOUT, requireNonNull(scope, "scope"));
    }

    /**
     * Wrap the provided {@code backingSeg}. The constructed segment will be attached to the same
     * scope as the {@code backingSeg}.
     *
     * @param backingSeg - The raw segment to wrap.
     */
    public LongSegment(MemorySegment backingSeg) {
        super(backingSeg);
    }

    @Override
    protected Long getTypedValue(ByteBuffer rawValue) {
        return rawValue.getLong();
    }

    @Override
    public Class<Long> getContainedType() {
        return Long.class;
    }

    @Override
    public int byteSize() {
        long result = MY_LAYOUT.byteSize();
        if (result > Integer.MAX_VALUE) {
            throw new RuntimeException("Segment size is larger than " + Integer.MAX_VALUE + " bytes.");
        }
        return (int) result;
    }
}
