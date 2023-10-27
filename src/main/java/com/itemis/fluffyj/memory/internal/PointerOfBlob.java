package com.itemis.fluffyj.memory.internal;

import static java.util.Objects.requireNonNull;

import com.itemis.fluffyj.memory.internal.impl.FluffyVectorPointerImpl;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;

public class PointerOfBlob extends FluffyVectorPointerImpl<Byte> {

    /**
     * Allocate a new pointer.
     *
     * @param addressPointedTo - The address the new pointer will point to.
     * @param byteSize - Size of the vector this pointer points to in bytes.
     * @param arena - Attach the new pointer to this arena.
     */
    public PointerOfBlob(final long addressPointedTo, final long byteSize, final Arena arena) {
        super(requireNonNull(addressPointedTo, "addressPointedTo"), byteSize, requireNonNull(arena, "arena"));
    }

    @Override
    public Byte[] dereference() {
        final var valSeg = rawDereference();
        final var result = new Byte[(int) byteSize];
        for (var i = 0; i < result.length; i++) {
            result[i] = valSeg.get(ValueLayout.JAVA_BYTE, i);
        }

        return result;
    }

    @Override
    public MemorySegment rawDereference() {
        return MemorySegment.ofAddress(getRawValue()).reinterpret(byteSize, arena, null);
    }
}
