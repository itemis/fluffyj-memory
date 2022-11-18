package com.itemis.fluffyj.memory.internal;

import static java.lang.foreign.ValueLayout.JAVA_INT;
import static java.util.Objects.requireNonNull;

import com.itemis.fluffyj.memory.api.FluffyPointer;
import com.itemis.fluffyj.memory.internal.impl.FluffyScalarPointerImpl;

import java.lang.foreign.MemoryAddress;
import java.lang.foreign.MemorySession;
import java.lang.foreign.ValueLayout;

/**
 * A {@link FluffyPointer} that points to a segment that holds a {@link Byte}.
 */
public class PointerOfByte extends FluffyScalarPointerImpl<Byte> {

    /**
     * Allocate a new pointer.
     *
     * @param addressPointedTo - The {@link MemoryAddress} the new pointer will point to.
     * @param session - Attach the new pointer to this session.
     */
    public PointerOfByte(MemoryAddress addressPointedTo, MemorySession session) {
        super(requireNonNull(addressPointedTo, "addressPointedTo"), JAVA_INT.byteSize(),
            requireNonNull(session, "session"));
    }

    @Override
    public Byte dereference() {
        return getValue().get(ValueLayout.JAVA_BYTE, 0);
    }
}
