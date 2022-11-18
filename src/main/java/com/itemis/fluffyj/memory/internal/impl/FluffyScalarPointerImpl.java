package com.itemis.fluffyj.memory.internal.impl;

import static java.util.Objects.requireNonNull;

import com.itemis.fluffyj.memory.api.FluffyScalarPointer;

import java.lang.foreign.MemoryAddress;
import java.lang.foreign.MemorySession;

/**
 * Default implementation of a scalar pointer.
 *
 * @param <T> - Type of data this pointer points to.
 */
public abstract class FluffyScalarPointerImpl<T> extends FluffyPointerImpl implements FluffyScalarPointer<T> {

    final long byteSize;

    /**
     * @param addressPointedTo - The address this pointer will point to.
     * @param byteSize - Size of the array this pointer points to in bytes.
     * @param session - The session to attach this pointer to. If the session is closed, the pointer
     *        will not be alive anymore.
     */
    public FluffyScalarPointerImpl(MemoryAddress addressPointedTo, long byteSize, MemorySession session) {
        super(addressPointedTo, session);
        this.byteSize = requireNonNull(byteSize);
    }
}
