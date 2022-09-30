package com.itemis.fluffyj.memory.internal;

import static java.lang.foreign.SegmentAllocator.newNativeArena;
import static java.util.Objects.requireNonNull;

import com.itemis.fluffyj.memory.api.FluffyScalarSegment;

import java.lang.foreign.MemoryAddress;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.MemorySession;

public class StringSegment implements FluffyScalarSegment<String> {

    private final MemorySegment backingSeg;

    /**
     * Allocate a new segment.
     *
     * @param initialValue - The new segment will hold this value.
     * @param session - The new segment will be attached to this session, i. e. if the session is
     *        closed, the new segment will not be alive anymore.
     */
    public StringSegment(String initialValue, MemorySession session) {
        this.backingSeg = newNativeArena(requireNonNull(session, "session"))
            .allocateUtf8String(requireNonNull(initialValue, "initialValue"));
    }

    /**
     * Wrap the provided {@code backingSeg}. The constructed segment will be attached to the same
     * session as the {@code backingSeg}.
     *
     * @param backingSeg - The raw segment to wrap.
     */
    public StringSegment(MemorySegment backingSeg) {
        this.backingSeg = requireNonNull(backingSeg, "backingSeg");
    }

    @Override
    public Class<? extends String> getContainedType() {
        return String.class;
    }

    @Override
    public MemoryAddress address() {
        return backingSeg.address();
    }

    @Override
    public boolean isAlive() {
        return backingSeg.session().isAlive();
    }

    @Override
    public String getValue() {
        return address().getUtf8String(0);
    }
}
