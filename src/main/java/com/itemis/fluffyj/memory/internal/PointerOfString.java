package com.itemis.fluffyj.memory.internal;

import static java.lang.foreign.MemorySegment.allocateNative;
import static java.lang.foreign.ValueLayout.ADDRESS;
import static java.util.Objects.requireNonNull;

import com.itemis.fluffyj.memory.api.FluffyScalarPointer;

import java.lang.foreign.MemoryAddress;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.MemorySession;

public class PointerOfString implements FluffyScalarPointer<String> {

    private final MemorySession session;
    private final MemorySegment backingSeg;

    /**
     * Allocate a new pointer.
     *
     * @param addressPointedTo - The {@link MemoryAddress} the new pointer will point to.
     * @param session - Attach the new pointer to this session.
     */
    public PointerOfString(MemoryAddress addressPointedTo, MemorySession session) {
        this.session = requireNonNull(session, "session");

        backingSeg = allocateNative(ADDRESS, session);
        backingSeg.asByteBuffer().order(FLUFFY_POINTER_BYTE_ORDER).putLong(requireNonNull(addressPointedTo, "addressPointedTo").toRawLongValue());
    }

    @Override
    public boolean isAlive() {
        return session.isAlive();
    }

    @Override
    public MemoryAddress address() {
        return backingSeg.address();
    }

    @Override
    public MemoryAddress getValue() {
        return MemoryAddress.ofLong(backingSeg.asByteBuffer().order(FLUFFY_POINTER_BYTE_ORDER).getLong());
    }

    @Override
    public String dereference() {
        return getValue().getUtf8String(0);
    }
}
