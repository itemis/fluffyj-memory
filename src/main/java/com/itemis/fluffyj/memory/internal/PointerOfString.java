package com.itemis.fluffyj.memory.internal;

import static java.lang.foreign.ValueLayout.JAVA_LONG;
import static java.util.Objects.requireNonNull;

import com.itemis.fluffyj.memory.api.FluffyScalarPointer;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

public class PointerOfString implements FluffyScalarPointer<String> {

    private final Arena arena;
    private final MemorySegment backingSeg;

    /**
     * Allocate a new pointer.
     *
     * @param addressPointedTo - The address the new pointer will point to.
     * @param arena - Attach the new pointer to this arena.
     */
    public PointerOfString(final long addressPointedTo, final Arena arena) {
        this.arena = requireNonNull(arena, "arena");

        backingSeg = arena.allocate(JAVA_LONG, addressPointedTo);
    }

    @Override
    public boolean isAlive() {
        return arena.scope().isAlive();
    }

    @Override
    public long rawAddress() {
        return backingSeg.address();
    }

    @Override
    public MemorySegment address() {
        return MemorySegment.ofAddress(rawAddress());
    }

    @Override
    public long getRawValue() {
        return backingSeg.get(JAVA_LONG, 0);
    }

    @Override
    public MemorySegment getValue() {
        return MemorySegment.ofAddress(getRawValue());
    }

    @Override
    public String dereference() {
        return rawDereference().getUtf8String(0);
    }

    @Override
    public MemorySegment rawDereference() {
        return MemorySegment.ofAddress(getRawValue()).reinterpret(Long.MAX_VALUE, arena, null);
    }
}
