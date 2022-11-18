package com.itemis.fluffyj.memory.internal;

import static java.util.Objects.requireNonNull;

import com.itemis.fluffyj.memory.internal.impl.FluffyVectorPointerImpl;

import java.lang.foreign.MemoryAddress;
import java.lang.foreign.MemorySession;
import java.lang.foreign.ValueLayout;

public class PointerOfBlob extends FluffyVectorPointerImpl<Byte> {

    /**
     * Allocate a new pointer.
     *
     * @param addressPointedTo - The {@link MemoryAddress} the new pointer will point to.
     * @param byteSize - Size of the vector this pointer points to in bytes.
     * @param session - Attach the new pointer to this session.
     */
    public PointerOfBlob(MemoryAddress addressPointedTo, long byteSize, MemorySession session) {
        super(requireNonNull(addressPointedTo, "addressPointedTo"), byteSize, requireNonNull(session, "session"));
    }

    @Override
    public Byte[] dereference() {
        var addr = getValue();
        var result = new Byte[(int) byteSize];
        for (var i = 0; i < result.length; i++) {
            result[i] = addr.get(ValueLayout.JAVA_BYTE, i);
        }

        return result;
    }
}
