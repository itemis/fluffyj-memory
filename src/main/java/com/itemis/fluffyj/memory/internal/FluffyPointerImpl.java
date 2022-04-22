package com.itemis.fluffyj.memory.internal;

import static jdk.incubator.foreign.ResourceScope.globalScope;

import com.itemis.fluffyj.memory.api.FluffyPointer;
import com.itemis.fluffyj.memory.api.FluffySegment;

import java.nio.ByteBuffer;

import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemoryLayout;
import jdk.incubator.foreign.ResourceScope;

/**
 * Default implementation of a generic pointer.
 *
 * @param <T> - Type of data this pointer points to.
 */
public abstract class FluffyPointerImpl<T> implements FluffyPointer<T> {

    private final FluffySegment<Long> addressSeg;
    private final MemoryLayout dereferencedMemoryLayout;
    private final ResourceScope scope;

    /**
     * @param addressPointedTo - The address this pointer will point to.
     * @param dereferencedMemoryLayout - The {@link MemoryLayout} of the segment this pointer points
     *        to.
     * @param scope - The scope to attach this pointer to. If the scope is closed, the pointer will
     *        not be alive anymore.
     */
    public FluffyPointerImpl(MemoryAddress addressPointedTo, MemoryLayout dereferencedMemoryLayout, ResourceScope scope) {
        this.addressSeg = new LongSegment(addressPointedTo.toRawLongValue(), globalScope());
        this.dereferencedMemoryLayout = dereferencedMemoryLayout;
        this.scope = scope;
    }

    /**
     * @param rawDereferencedValue - A read only {@link ByteBuffer} that contains the bytes of the
     *        segment this pointer points to.
     * @return The correctly typed value of the segment this pointer points to.
     */
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
