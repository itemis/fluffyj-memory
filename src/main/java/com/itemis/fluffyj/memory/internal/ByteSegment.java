package com.itemis.fluffyj.memory.internal;

import static java.lang.foreign.ValueLayout.JAVA_BYTE;
import static java.util.Objects.requireNonNull;

import com.itemis.fluffyj.memory.api.FluffySegment;
import com.itemis.fluffyj.memory.internal.impl.FluffyScalarSegmentImpl;

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.MemorySession;
import java.nio.ByteBuffer;

/**
 * A {@link FluffySegment} that holds a {@link Byte}.
 */
public class ByteSegment extends FluffyScalarSegmentImpl<Byte> {

    private static final MemoryLayout MY_LAYOUT = JAVA_BYTE;

    /**
     * Allocate a new segment.
     *
     * @param initialValue - The new segment will hold this value.
     * @param session - The new segment will be attached to this session, i. e. if the session is
     *        closed, the new segment will not be alive anymore.
     */
    public ByteSegment(byte initialValue, MemorySession session) {
        super(new byte[] {initialValue}, MY_LAYOUT, requireNonNull(session, "session"));
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
    protected Byte getTypedValue(ByteBuffer rawValue) {
        return rawValue.get();
    }

    @Override
    public Class<Byte> getContainedType() {
        return Byte.class;
    }
}
