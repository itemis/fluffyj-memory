package com.itemis.fluffyj.memory.internal;

import static java.lang.foreign.ValueLayout.JAVA_LONG;
import static java.util.Objects.requireNonNull;

import com.itemis.fluffyj.memory.api.FluffyPointer;
import com.itemis.fluffyj.memory.internal.impl.FluffyScalarPointerImpl;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;

/**
 * A {@link FluffyPointer} that points to a segment that holds a {@link Long}.
 */
public class PointerOfLong extends FluffyScalarPointerImpl<Long> {

    /**
     * Allocate a new pointer.
     *
     * @param addressPointedTo - The {@link MemorySegment} the new pointer will point to.
     * @param arena - Attach the new pointer to this arena.
     */
    public PointerOfLong(final long addressPointedTo, final Arena arena) {
        super(requireNonNull(addressPointedTo, "addressPointedTo"), JAVA_LONG.byteSize(),
            requireNonNull(arena, "arena"));
    }

    @Override
    public Long dereference() {
        return rawDereference().get(ValueLayout.JAVA_LONG, 0);
    }

    @Override
    public MemorySegment rawDereference() {
        return MemorySegment.ofAddress(getRawValue()).reinterpret(JAVA_LONG.byteSize(), arena, null);
    }
}
