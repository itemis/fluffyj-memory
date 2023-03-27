package com.itemis.fluffyj.memory.internal.impl;

import static java.util.Objects.requireNonNull;

import com.itemis.fluffyj.memory.api.FluffyVectorPointer;

import java.lang.foreign.SegmentScope;

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
     * @param scope - The scope to attach this pointer to.
     */
    public FluffyVectorPointerImpl(long addressPointedTo, long byteSize, SegmentScope scope) {
        super(addressPointedTo, scope);
        this.byteSize = requireNonNull(byteSize);
    }
}
