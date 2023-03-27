package com.itemis.fluffyj.memory.internal;

import static java.lang.foreign.MemorySegment.allocateNative;
import static java.lang.foreign.MemorySegment.ofAddress;
import static java.lang.foreign.ValueLayout.ADDRESS;
import static java.lang.foreign.ValueLayout.JAVA_LONG;
import static java.util.Objects.requireNonNull;

import com.itemis.fluffyj.memory.api.FluffyScalarPointer;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentScope;

public class PointerOfString implements FluffyScalarPointer<String> {

    private final SegmentScope scope;
    private final MemorySegment backingSeg;

    /**
     * Allocate a new pointer.
     *
     * @param addressPointedTo - The address the new pointer will point to.
     * @param scope - Attach the new pointer to this scope.
     */
    public PointerOfString(long addressPointedTo, SegmentScope scope) {
        this.scope = requireNonNull(scope, "scope");

        backingSeg = allocateNative(JAVA_LONG, scope);
        backingSeg.set(JAVA_LONG, 0, addressPointedTo);
    }

    @Override
    public boolean isAlive() {
        return scope.isAlive();
    }

    @Override
    public long rawAddress() {
        return backingSeg.address();
    }

    @Override
    public MemorySegment address() {
        return ofAddress(rawAddress(), 0, scope);
    }

    @Override
    public long getRawValue() {
        return backingSeg.get(JAVA_LONG, 0);
    }

    @Override
    public MemorySegment getValue() {
        return ofAddress(getRawValue(), 0, scope);
    }

    @Override
    public String dereference() {
        return backingSeg.get(ADDRESS.asUnbounded(), 0).getUtf8String(0);
    }
}
