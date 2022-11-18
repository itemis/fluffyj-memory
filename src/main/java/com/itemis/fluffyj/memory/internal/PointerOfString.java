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
        backingSeg.set(ADDRESS, 0, requireNonNull(addressPointedTo, "addressPointedTo"));
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
        return backingSeg.get(ADDRESS, 0);
    }

    @Override
    public String dereference() {
        return getValue().getUtf8String(0);
    }
}
