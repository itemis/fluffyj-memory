package com.itemis.fluffyj.memory.internal;

import static java.util.Objects.requireNonNull;
import static jdk.incubator.foreign.MemoryLayouts.JAVA_BYTE;

import com.itemis.fluffyj.memory.api.FluffySegment;
import com.itemis.fluffyj.memory.internal.impl.FluffyScalarSegmentImpl;

import java.nio.ByteBuffer;

import jdk.incubator.foreign.MemoryLayout;
import jdk.incubator.foreign.MemorySegment;
import jdk.incubator.foreign.ResourceScope;

/**
 * A {@link FluffySegment} that holds a {@link Byte}.
 */
public class ByteSegment extends FluffyScalarSegmentImpl<Byte> {

    private static final MemoryLayout MY_LAYOUT = JAVA_BYTE;

    /**
     * Allocate a new segment.
     *
     * @param initialValue - The new segment will hold this value.
     * @param scope - The new segment will be attached to this scope, i. e. if the scope is closed,
     *        the new segment will not be alive anymore.
     */
    public ByteSegment(byte initialValue, ResourceScope scope) {
        super(ByteBuffer.allocate((int) MY_LAYOUT.byteSize()).put(initialValue).array(), MY_LAYOUT, requireNonNull(scope, "scope"));
    }

    /**
     * Wrap the provided {@code backingSeg}. The constructed segment will be attached to the same
     * scope as the {@code backingSeg}.
     *
     * @param backingSeg - The raw segment to wrap.
     */
    public ByteSegment(MemorySegment backingSeg) {
        super(backingSeg);
    }

    @Override
    protected Byte getTypedValue(ByteBuffer rawValue) {
        return rawValue.get();
    }

    @Override
    public Class<Byte> getContainedType() {
        return Byte.class;
    }
}
