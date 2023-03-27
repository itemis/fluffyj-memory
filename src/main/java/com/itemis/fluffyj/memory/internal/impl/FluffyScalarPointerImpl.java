package com.itemis.fluffyj.memory.internal.impl;

import static java.util.Objects.requireNonNull;

import com.itemis.fluffyj.memory.api.FluffyScalarPointer;

import java.lang.foreign.SegmentScope;

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
     * @param scope - The scope to attach this pointer to.
     */
    public FluffyScalarPointerImpl(long addressPointedTo, long byteSize, SegmentScope scope) {
        super(addressPointedTo, scope);
        this.byteSize = requireNonNull(byteSize);
    }
}
