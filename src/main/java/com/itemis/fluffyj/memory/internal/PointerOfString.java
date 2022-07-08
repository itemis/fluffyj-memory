package com.itemis.fluffyj.memory.internal;

import static java.util.Objects.requireNonNull;
import static jdk.incubator.foreign.CLinker.toJavaString;
import static jdk.incubator.foreign.MemoryAddress.ofLong;
import static jdk.incubator.foreign.MemoryLayouts.ADDRESS;
import static jdk.incubator.foreign.MemorySegment.allocateNative;

import com.itemis.fluffyj.memory.api.FluffyScalarPointer;

import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;
import jdk.incubator.foreign.ResourceScope;

public class PointerOfString implements FluffyScalarPointer<String> {

    private final ResourceScope scope;
    private final MemorySegment backingSeg;

    /**
     * Allocate a new pointer.
     *
     * @param addressPointedTo - The {@link MemoryAddress} the new pointer will point to.
     * @param scope - Attach the new pointer to this scope.
     */
    public PointerOfString(MemoryAddress addressPointedTo, ResourceScope scope) {
        this.scope = requireNonNull(scope, "scope");

        backingSeg = allocateNative(ADDRESS, scope);
        backingSeg.asByteBuffer().putLong(requireNonNull(addressPointedTo, "addressPointedTo").toRawLongValue());
    }

    @Override
    public boolean isAlive() {
        return scope.isAlive();
    }

    @Override
    public MemoryAddress address() {
        return backingSeg.address();
    }

    @Override
    public MemoryAddress getValue() {
        return ofLong(backingSeg.asByteBuffer().getLong());
    }

    @Override
    public String dereference() {
        return toJavaString(getValue());
    }
}
