package com.itemis.fluffyj.memory.internal.impl;

import static java.lang.foreign.MemorySegment.ofAddress;
import static java.lang.foreign.ValueLayout.JAVA_LONG;

import com.itemis.fluffyj.memory.api.FluffyPointer;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

/**
 * Default implementation of a pointer.
 */
public abstract class FluffyPointerImpl implements FluffyPointer {

    protected final MemorySegment addressSeg;
    protected final Arena arena;

    /**
     * @param addressPointedTo - The address this pointer will point to.
     * @param arena - The arena to attach this pointer to.
     */
    protected FluffyPointerImpl(final long addressPointedTo, final Arena arena) {
        this.addressSeg = arena.allocateFrom(JAVA_LONG, addressPointedTo);
        this.arena = arena;
    }

    @Override
    public final boolean isAlive() {
        return arena.scope().isAlive();
    }

    @Override
    public final long rawAddress() {
        return addressSeg.address();
    }

    @Override
    public MemorySegment address() {
        return ofAddress(rawAddress());
    }

    @Override
    public final long getRawValue() {
        return addressSeg.get(JAVA_LONG, 0);
    }

    @Override
    public MemorySegment getValue() {
        return MemorySegment.ofAddress(getRawValue());
    }

    @Override
    public MemorySegment rawDereference() {
        return MemorySegment.ofAddress(getRawValue()).reinterpret(Long.MAX_VALUE, arena, null);
    }
}
