package com.itemis.fluffyj.memory.internal.impl;

import static java.lang.foreign.MemorySegment.ofAddress;
import static java.lang.foreign.ValueLayout.JAVA_LONG;

import com.itemis.fluffyj.memory.api.FluffyPointer;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentAllocator;
import java.lang.foreign.SegmentScope;

/**
 * Default implementation of a pointer.
 */
public abstract class FluffyPointerImpl implements FluffyPointer {

    protected final MemorySegment addressSeg;
    protected final SegmentScope scope;

    /**
     * @param addressPointedTo - The address this pointer will point to.
     * @param scope - The scope to attach this pointer to.
     */
    public FluffyPointerImpl(long addressPointedTo, SegmentScope scope) {
        this.addressSeg = SegmentAllocator.nativeAllocator(scope).allocate(JAVA_LONG, addressPointedTo);
        this.scope = scope;
    }

    @Override
    public final boolean isAlive() {
        return scope.isAlive();
    }

    @Override
    public final long address() {
        return addressSeg.address();
    }

    @Override
    public MemorySegment addressAsSeg() {
        return ofAddress(address(), 0, scope);
    }

    @Override
    public final long getValue() {
        return addressSeg.get(JAVA_LONG, 0);
    }

    @Override
    public MemorySegment getValueAsSeg() {
        return addressSeg;
    }
}
