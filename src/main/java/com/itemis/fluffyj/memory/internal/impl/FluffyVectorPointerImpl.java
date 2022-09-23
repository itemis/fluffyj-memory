package com.itemis.fluffyj.memory.internal.impl;

import static java.util.Objects.requireNonNull;

import com.itemis.fluffyj.memory.api.FluffyVectorPointer;

import java.lang.foreign.MemoryAddress;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.MemorySession;
import java.nio.ByteBuffer;

/**
 * Default implementation of a vector pointer.
 *
 * @param <T> - The component type of the vector this pointer points to, i. e. for an array of Byte
 *        this would be Byte.
 */
public abstract class FluffyVectorPointerImpl<T> extends FluffyPointerImpl implements FluffyVectorPointer<T> {

    private final long byteSize;

    /**
     * @param addressPointedTo - The address this pointer will point to.
     * @param byteSize - Size of the array this pointer points to in bytes.
     * @param session - The session to attach this pointer to. If the session is closed, the pointer
     *        will not be alive anymore.
     */
    public FluffyVectorPointerImpl(MemoryAddress addressPointedTo, long byteSize, MemorySession session) {
        super(addressPointedTo, session);
        this.byteSize = requireNonNull(byteSize);
    }

    /**
     * @param rawDereferencedValue - A read only {@link ByteBuffer} that contains the bytes of the
     *        segment this pointer points to.
     * @return The correctly typed value of the segment this pointer points to.
     */
    protected abstract T[] typedDereference(ByteBuffer rawDereferencedValue);

    @Override
    public final T[] dereference() {
        return typedDereference(MemorySegment.ofAddress(getValue(), byteSize, session).asByteBuffer().asReadOnlyBuffer().order(FLUFFY_POINTER_BYTE_ORDER));
    }
}
