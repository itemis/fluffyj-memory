package com.itemis.fluffyj.memory.internal;

import static java.util.Objects.requireNonNull;

import com.itemis.fluffyj.memory.api.FluffyPointer;
import com.itemis.fluffyj.memory.internal.impl.FluffyScalarPointerImpl;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;

/**
 * A {@link FluffyPointer} that points to a segment that holds a {@link Byte}.
 */
public class PointerOfByte extends FluffyScalarPointerImpl<Byte> {

    /**
     * Allocate a new pointer.
     *
     * @param addressPointedTo - The address the new pointer will point to.
     * @param arena - Attach the new pointer to this arena.
     */
    public PointerOfByte(final long addressPointedTo, final Arena arena) {
        super(requireNonNull(addressPointedTo, "addressPointedTo"), ValueLayout.JAVA_BYTE.byteSize(),
            requireNonNull(arena, "arena"));
    }

    @Override
    public Byte dereference() {
        return rawDereference().get(ValueLayout.JAVA_BYTE, 0);
    }

    @Override
    public MemorySegment rawDereference() {
        return MemorySegment.ofAddress(getRawValue()).reinterpret(1, arena, null);
    }
}
