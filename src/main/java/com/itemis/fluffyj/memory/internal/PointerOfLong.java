package com.itemis.fluffyj.memory.internal;

import static java.lang.foreign.ValueLayout.JAVA_LONG;
import static java.util.Objects.requireNonNull;

import com.itemis.fluffyj.memory.api.FluffyPointer;
import com.itemis.fluffyj.memory.internal.impl.FluffyScalarPointerImpl;

import java.lang.foreign.MemoryAddress;
import java.lang.foreign.MemorySession;

/**
 * A {@link FluffyPointer} that points to a segment that holds a {@link Long}.
 */
public class PointerOfLong extends FluffyScalarPointerImpl<Long> {

    /**
     * Allocate a new pointer.
     *
     * @param addressPointedTo - The {@link MemoryAddress} the new pointer will point to.
     * @param sessions - Attach the new pointer to this session.
     */
    public PointerOfLong(MemoryAddress addressPointedTo, MemorySession session) {
        super(requireNonNull(addressPointedTo, "addressPointedTo"), JAVA_LONG.byteSize(),
            requireNonNull(session, "session"));
    }

    @Override
    public Long dereference() {
        return getValue().get(JAVA_LONG, 0);
    }
}
