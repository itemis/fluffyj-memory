package com.itemis.fluffyj.memory.internal;

import static java.lang.foreign.ValueLayout.JAVA_INT;
import static java.util.Objects.requireNonNull;

import com.itemis.fluffyj.memory.api.FluffyPointer;
import com.itemis.fluffyj.memory.internal.impl.FluffyScalarPointerImpl;

import java.lang.foreign.MemoryAddress;
import java.lang.foreign.MemorySession;
import java.lang.foreign.ValueLayout;

/**
 * A {@link FluffyPointer} that points to a segment that holds an {@link Integer}.
 */
public class PointerOfInt extends FluffyScalarPointerImpl<Integer> {

    /**
     * Allocate a new pointer.
     *
     * @param addressPointedTo - The {@link MemoryAddress} the new pointer will point to.
     * @param session - Attach the new pointer to this session.
     */
    public PointerOfInt(MemoryAddress addressPointedTo, MemorySession session) {
        super(requireNonNull(addressPointedTo, "addressPointedTo"), JAVA_INT.byteSize(),
            requireNonNull(session, "session"));
    }

    @Override
    public Integer dereference() {
        return getValue().get(ValueLayout.JAVA_INT, 0);
    }
}
