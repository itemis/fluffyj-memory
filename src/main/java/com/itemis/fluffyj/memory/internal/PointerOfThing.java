package com.itemis.fluffyj.memory.internal;

import static java.util.Objects.requireNonNull;
import static jdk.incubator.foreign.MemoryLayouts.ADDRESS;
import static jdk.incubator.foreign.MemorySegment.allocateNative;

import com.itemis.fluffyj.memory.api.FluffyPointer;

import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;
import jdk.incubator.foreign.ResourceScope;

/**
 * An arbitrary pointer that just holds an address and cannot be dereferenced via Fluffy API. It is
 * thought to be used in cases where an API call requires the address of a pointer segment in order
 * to "return" the address of a newly created segment via this pointer.
 */
public class PointerOfThing implements FluffyPointer {

    private final MemorySegment addressSeg;
    private final ResourceScope scope;

    /**
     * @param addressPointedTo - The address this pointer will point to.
     * @param scope - The scope to attach this pointer to. If the scope is closed, the pointer will
     *        not be alive anymore.
     */
    public PointerOfThing(ResourceScope scope) {
        this.addressSeg = allocateNative(ADDRESS, requireNonNull(scope, "scope"));
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
