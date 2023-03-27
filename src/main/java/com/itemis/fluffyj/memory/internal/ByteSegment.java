package com.itemis.fluffyj.memory.internal;

import static java.util.Objects.requireNonNull;

import com.itemis.fluffyj.memory.api.FluffyScalarSegment;
import com.itemis.fluffyj.memory.api.FluffySegment;
import com.itemis.fluffyj.memory.internal.impl.FluffySegmentImpl;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentScope;
import java.lang.foreign.ValueLayout;

/**
 * A {@link FluffySegment} that holds a {@link Byte}.
 */
public class ByteSegment extends FluffySegmentImpl implements FluffyScalarSegment<Byte> {

    /**
     * Allocate a new segment.
     *
     * @param initialValue - The new segment will hold this value.
     * @param scope - The new segment will be attached to this scope.
     */
    public ByteSegment(byte initialValue, SegmentScope scope) {
        super(new byte[] {initialValue}, requireNonNull(scope, "session"));
    }

    /**
     * Wrap the provided {@code backingSeg}. The constructed segment will be attached to the same
     * session as the {@code backingSeg}.
     *
     * @param backingSeg - The raw segment to wrap.
     */
    public ByteSegment(MemorySegment backingSeg) {
        super(backingSeg);
    }

    @Override
    public Byte getValue() {
        return backingSeg.get(ValueLayout.JAVA_BYTE, 0);
    }

    @Override
    public Class<? extends Byte> getContainedType() {
        return Byte.class;
    }
}
