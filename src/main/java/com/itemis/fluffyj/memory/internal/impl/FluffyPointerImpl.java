package com.itemis.fluffyj.memory.internal.impl;

import static java.lang.foreign.MemorySegment.allocateNative;
import static java.util.Objects.requireNonNull;

import com.itemis.fluffyj.memory.api.FluffyPointer;

import java.lang.foreign.MemoryAddress;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.MemorySession;
import java.lang.foreign.ValueLayout;

/**
 * Default implementation of a pointer.
 */
public abstract class FluffyPointerImpl implements FluffyPointer {

    protected final MemorySegment addressSeg;
    protected final MemorySession session;

    /**
     * @param addressPointedTo - The address this pointer will point to.
     * @param session - The session to attach this pointer to. If the session is closed, the pointer
     *        will not be alive anymore.
     */
    public FluffyPointerImpl(MemoryAddress addressPointedTo, MemorySession session) {
        this.addressSeg = allocateNative(ValueLayout.ADDRESS, requireNonNull(session, "session"));
        this.addressSeg.asByteBuffer().order(FLUFFY_POINTER_BYTE_ORDER).putLong(requireNonNull(addressPointedTo, "addressPointedTo").toRawLongValue());
        this.session = session;
    }

    @Override
    public final boolean isAlive() {
        return session.isAlive();
    }

    @Override
    public final MemoryAddress address() {
        return addressSeg.address();
    }

    @Override
    public final MemoryAddress getValue() {
        return MemoryAddress.ofLong(addressSeg.asByteBuffer().order(FLUFFY_POINTER_BYTE_ORDER).getLong());
    }
}
