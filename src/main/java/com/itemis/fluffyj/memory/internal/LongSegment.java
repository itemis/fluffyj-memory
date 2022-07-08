package com.itemis.fluffyj.memory.internal;

import static java.nio.ByteOrder.LITTLE_ENDIAN;
import static java.util.Objects.requireNonNull;
import static jdk.incubator.foreign.MemoryLayouts.JAVA_LONG;

import com.itemis.fluffyj.memory.api.FluffySegment;
import com.itemis.fluffyj.memory.internal.impl.FluffyScalarSegmentImpl;
import com.tngtech.archunit.thirdparty.com.google.common.primitives.Longs;

import org.apache.commons.lang3.ArrayUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import jdk.incubator.foreign.MemoryLayout;
import jdk.incubator.foreign.MemorySegment;
import jdk.incubator.foreign.ResourceScope;

/**
 * A {@link FluffySegment} that holds a {@link Long}.
 */
public class LongSegment extends FluffyScalarSegmentImpl<Long> {

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
        super(toByteArray(initialValue, FLUFFY_SEGMENT_BYTE_ORDER), MY_LAYOUT,
            requireNonNull(scope, "scope"));
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
        return rawValue.order(FLUFFY_SEGMENT_BYTE_ORDER).getLong();
    }

    @Override
    public Class<Long> getContainedType() {
        return Long.class;
    }

    private static final byte[] toByteArray(long val, ByteOrder byteOrder) {
        var result = Longs.toByteArray(val);
        if (byteOrder.equals(LITTLE_ENDIAN)) {
            ArrayUtils.reverse(result);
        }
        return result;
    }
}
