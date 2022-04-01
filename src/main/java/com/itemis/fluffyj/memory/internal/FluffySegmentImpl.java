package com.itemis.fluffyj.memory.internal;

import static jdk.incubator.foreign.MemorySegment.allocateNative;

import com.itemis.fluffyj.memory.api.FluffySegment;

import java.nio.ByteBuffer;

import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemoryLayout;
import jdk.incubator.foreign.MemorySegment;
import jdk.incubator.foreign.ResourceScope;

abstract class FluffySegmentImpl<T> implements FluffySegment<T> {

    protected final MemorySegment backingSeg;
    protected final ResourceScope scope;

    public FluffySegmentImpl(MemorySegment backingSeg) {
        this.backingSeg = backingSeg;
        this.scope = backingSeg.scope();
    }

    public FluffySegmentImpl(byte[] initialValue, MemoryLayout layout, ResourceScope scope) {
        this(allocateNative(layout, scope));
        backingSeg.asByteBuffer().put(initialValue);
    }

    @Override
    public T getValue() {
        return getTypedValue(backingSeg.asByteBuffer().asReadOnlyBuffer());
    }

    @Override
    public boolean isAlive() {
        return scope.isAlive();
    }

    @Override
    public MemoryAddress address() {
        return backingSeg.address();
    }

    protected abstract T getTypedValue(ByteBuffer rawValue);
}
