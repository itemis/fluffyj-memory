package com.itemis.fluffyj.memory.internal;

import static java.util.Objects.requireNonNull;
import static jdk.incubator.foreign.MemoryLayouts.JAVA_LONG;

import com.itemis.fluffyj.memory.api.FluffyPointer;
import com.itemis.fluffyj.memory.internal.impl.FluffyScalarPointerImpl;

import java.nio.ByteBuffer;

import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.ResourceScope;

/**
 * A {@link FluffyPointer} that points to a segment that holds a {@link Long}.
 */
public class PointerOfLong extends FluffyScalarPointerImpl<Long> {

    /**
     * Allocate a new pointer.
     *
     * @param addressPointedTo - The {@link MemoryAddress} the new pointer will point to.
     * @param scope - Attach the new pointer to this scope.
     */
    public PointerOfLong(MemoryAddress addressPointedTo, ResourceScope scope) {
        super(requireNonNull(addressPointedTo, "addressPointedTo"), JAVA_LONG.byteSize(), requireNonNull(scope, "scope"));
    }

    @Override
    protected Long typedDereference(ByteBuffer rawDereferencedValue) {
        requireNonNull(rawDereferencedValue, "rawDereferencedValue");
        return rawDereferencedValue.order(FLUFFY_POINTER_BYTE_ORDER).getLong();
    }
}
