package com.itemis.fluffyj.memory.internal.impl;

import static java.util.Objects.requireNonNull;
import static jdk.incubator.foreign.MemoryLayouts.ADDRESS;
import static jdk.incubator.foreign.MemorySegment.allocateNative;

import com.itemis.fluffyj.memory.api.FluffyPointer;

import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;
import jdk.incubator.foreign.ResourceScope;

/**
 * Default implementation of a pointer.
 */
public abstract class FluffyPointerImpl implements FluffyPointer {

    protected final MemorySegment addressSeg;
    protected final ResourceScope scope;

    /**
     * @param addressPointedTo - The address this pointer will point to.
     * @param scope - The scope to attach this pointer to. If the scope is closed, the pointer will
     *        not be alive anymore.
     */
    public FluffyPointerImpl(MemoryAddress addressPointedTo, ResourceScope scope) {
        this.addressSeg = allocateNative(ADDRESS, requireNonNull(scope, "scope"));
        this.addressSeg.asByteBuffer().putLong(requireNonNull(addressPointedTo, "addressPointedTo").toRawLongValue());
        this.scope = scope;
    }

    @Override
    public final boolean isAlive() {
        return scope.isAlive();
    }

    @Override
    public final MemoryAddress address() {
        return addressSeg.address();
    }

    @Override
    public final MemoryAddress getValue() {
        return MemoryAddress.ofLong(addressSeg.asByteBuffer().getLong());
    }
}
