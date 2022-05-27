package com.itemis.fluffyj.memory.internal;

import static java.util.Objects.requireNonNull;
import static jdk.incubator.foreign.MemoryLayouts.JAVA_INT;

import com.itemis.fluffyj.memory.api.FluffySegment;
import com.itemis.fluffyj.memory.internal.impl.FluffyScalarSegmentImpl;

import java.nio.ByteBuffer;

import jdk.incubator.foreign.MemoryLayout;
import jdk.incubator.foreign.MemorySegment;
import jdk.incubator.foreign.ResourceScope;

/**
 * A {@link FluffySegment} that holds an {@link Integer}.
 */
public class IntSegment extends FluffyScalarSegmentImpl<Integer> {

    private static final MemoryLayout MY_LAYOUT = JAVA_INT;
    /**
     * Instances of this class hold this value as default if no other has been set upon
     * construction.
     */
    public static final int DEFAULT_VALUE = -1;

    /**
     * Allocate a new segment.
     *
     * @param initialValue - The new segment will hold this value.
     * @param scope - The new segment will be attached to this scope, i. e. if the scope is closed,
     *        the new segment will not be alive anymore.
     */
    public IntSegment(int initialValue, ResourceScope scope) {
        super(ByteBuffer.allocate((int) MY_LAYOUT.byteSize()).putInt(initialValue).array(), MY_LAYOUT, requireNonNull(scope, "scope"));
    }

    /**
     * Wrap the provided {@code backingSeg}. The constructed segment will be attached to the same
     * scope as the {@code backingSeg}.
     *
     * @param backingSeg - The raw segment to wrap.
     */
    public IntSegment(MemorySegment backingSeg) {
        super(backingSeg);
    }

    @Override
    protected Integer getTypedValue(ByteBuffer rawValue) {
        return rawValue.getInt();
    }

    @Override
    public Class<Integer> getContainedType() {
        return Integer.class;
    }
}
