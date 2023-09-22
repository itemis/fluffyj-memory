package com.itemis.fluffyj.memory.internal;

import static java.lang.foreign.ValueLayout.JAVA_INT;
import static java.util.Objects.requireNonNull;

import com.itemis.fluffyj.memory.api.FluffyPointer;
import com.itemis.fluffyj.memory.internal.impl.FluffyScalarPointerImpl;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;

/**
 * A {@link FluffyPointer} that points to a segment that holds an {@link Integer}.
 */
public class PointerOfInt extends FluffyScalarPointerImpl<Integer> {

    /**
     * Allocate a new pointer.
     *
     * @param addressPointedTo - The address the new pointer will point to.
     * @param arena - Attach the new pointer to this arena.
     */
    public PointerOfInt(final long addressPointedTo, final Arena arena) {
        super(requireNonNull(addressPointedTo, "addressPointedTo"), JAVA_INT.byteSize(),
            requireNonNull(arena, "arena"));
    }

    @Override
    public Integer dereference() {
        return rawDereference().get(ValueLayout.JAVA_INT, 0);
    }

    @Override
    public MemorySegment rawDereference() {
        return MemorySegment.ofAddress(getRawValue()).reinterpret(JAVA_INT.byteSize(), arena, null);
    }
}
