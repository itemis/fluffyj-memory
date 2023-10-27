package com.itemis.fluffyj.memory.internal.impl;

import static java.util.Objects.requireNonNull;

import com.itemis.fluffyj.memory.api.FluffyScalarPointer;

import java.lang.foreign.Arena;

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
     * @param arena - The arena to attach this pointer to.
     */
    public FluffyScalarPointerImpl(final long addressPointedTo, final long byteSize, final Arena arena) {
        super(addressPointedTo, arena);
        this.byteSize = requireNonNull(byteSize);
    }
}
