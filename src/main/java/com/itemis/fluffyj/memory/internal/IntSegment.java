package com.itemis.fluffyj.memory.internal;

import static java.lang.foreign.ValueLayout.JAVA_INT;
import static java.nio.ByteOrder.LITTLE_ENDIAN;
import static java.util.Objects.requireNonNull;

import com.google.common.primitives.Ints;
import com.itemis.fluffyj.memory.api.FluffySegment;
import com.itemis.fluffyj.memory.internal.impl.FluffyScalarSegmentImpl;

import org.apache.commons.lang3.ArrayUtils;

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.MemorySession;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

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
     * @param session - The new segment will be attached to this session, i. e. if the session is
     *        closed, the new segment will not be alive anymore.
     */
    public IntSegment(int initialValue, MemorySession session) {
        super(toByteArray(initialValue, FLUFFY_SEGMENT_BYTE_ORDER), MY_LAYOUT,
            requireNonNull(session, "session"));
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
    protected Integer getTypedValue(ByteBuffer rawValue) {
        return rawValue.order(FLUFFY_SEGMENT_BYTE_ORDER).getInt();
    }

    @Override
    public Class<Integer> getContainedType() {
        return Integer.class;
    }

    private static final byte[] toByteArray(int val, ByteOrder byteOrder) {
        var result = Ints.toByteArray(val);
        if (byteOrder.equals(LITTLE_ENDIAN)) {
            ArrayUtils.reverse(result);
        }
        return result;
    }
}
