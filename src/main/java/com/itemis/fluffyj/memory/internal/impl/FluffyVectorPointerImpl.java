package com.itemis.fluffyj.memory.internal.impl;

import static java.util.Objects.requireNonNull;

import com.itemis.fluffyj.memory.api.FluffyVectorPointer;

import java.lang.foreign.Arena;

/**
 * Default implementation of a vector pointer.
 *
 * @param <T> - The component type of the vector this pointer points to, i. e. for an array of Byte
 *        this would be Byte.
 */
public abstract class FluffyVectorPointerImpl<T> extends FluffyPointerImpl implements FluffyVectorPointer<T> {

    protected final long byteSize;

    /**
     * @param addressPointedTo - The address this pointer will point to.
     * @param byteSize - Size of the array this pointer points to in bytes.
     * @param arena - The arena to attach this pointer to.
     */
    public FluffyVectorPointerImpl(final long addressPointedTo, final long byteSize, final Arena arena) {
        super(addressPointedTo, arena);
        this.byteSize = requireNonNull(byteSize);
    }
}
