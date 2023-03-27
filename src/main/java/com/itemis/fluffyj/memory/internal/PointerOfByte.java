package com.itemis.fluffyj.memory.internal;

import static java.lang.foreign.ValueLayout.JAVA_INT;
import static java.util.Objects.requireNonNull;

import com.itemis.fluffyj.memory.api.FluffyPointer;
import com.itemis.fluffyj.memory.internal.impl.FluffyScalarPointerImpl;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentScope;
import java.lang.foreign.ValueLayout;

/**
 * A {@link FluffyPointer} that points to a segment that holds a {@link Byte}.
 */
public class PointerOfByte extends FluffyScalarPointerImpl<Byte> {

    /**
     * Allocate a new pointer.
     *
     * @param addressPointedTo - The address the new pointer will point to.
     * @param scope - Attach the new pointer to this scope.
     */
    public PointerOfByte(long addressPointedTo, SegmentScope scope) {
        super(requireNonNull(addressPointedTo, "addressPointedTo"), JAVA_INT.byteSize(),
            requireNonNull(scope, "scope"));
    }

    @Override
    public Byte dereference() {
        return MemorySegment.ofAddress(getValue(), 1, scope).get(ValueLayout.JAVA_BYTE, 0);
    }
}
