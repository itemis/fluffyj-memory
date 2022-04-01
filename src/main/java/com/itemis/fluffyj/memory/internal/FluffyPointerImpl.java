package com.itemis.fluffyj.memory.internal;

import static jdk.incubator.foreign.ResourceScope.globalScope;

import com.itemis.fluffyj.memory.api.FluffyPointer;
import com.itemis.fluffyj.memory.api.FluffySegment;

import java.nio.ByteBuffer;

import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemoryLayout;
import jdk.incubator.foreign.ResourceScope;

/**
 * @param <T> - Type of data this pointer points to.
 */
public abstract class FluffyPointerImpl<T> implements FluffyPointer<T> {

    private final FluffySegment<Long> addressSeg;
    private final MemoryLayout dereferencedMemoryLayout;
    private final ResourceScope scope;

    public FluffyPointerImpl(MemoryAddress addressPointedTo, MemoryLayout dereferencedMemoryLayout, ResourceScope scope) {
        this.addressSeg = new LongSegment(addressPointedTo.toRawLongValue(), globalScope());
        this.dereferencedMemoryLayout = dereferencedMemoryLayout;
        this.scope = scope;
    }

    protected abstract T typedDereference(ByteBuffer rawDereferencedValue);

    @Override
    public T dereference() {
        return typedDereference(getValue().asSegment(dereferencedMemoryLayout.byteSize(), scope).asByteBuffer().asReadOnlyBuffer());
    }

    @Override
    public boolean isAlive() {
        return scope.isAlive();
    }

    @Override
    public MemoryAddress address() {
        return addressSeg.address();
    }

    @Override
    public MemoryAddress getValue() {
        return MemoryAddress.ofLong(addressSeg.getValue());
    }
}
