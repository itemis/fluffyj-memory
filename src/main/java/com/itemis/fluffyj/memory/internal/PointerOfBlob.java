package com.itemis.fluffyj.memory.internal;

import static java.util.Objects.requireNonNull;

import com.itemis.fluffyj.memory.internal.impl.FluffyVectorPointerImpl;

import java.nio.ByteBuffer;

import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.ResourceScope;

public class PointerOfBlob extends FluffyVectorPointerImpl<Byte> {

    /**
     * Allocate a new pointer.
     *
     * @param addressPointedTo - The {@link MemoryAddress} the new pointer will point to.
     * @param byteSize - Size of the vector this pointer points to in bytes.
     * @param scope - Attach the new pointer to this scope.
     */
    public PointerOfBlob(MemoryAddress addressPointedTo, long byteSize, ResourceScope scope) {
        super(requireNonNull(addressPointedTo, "addressPointedTo"), byteSize, requireNonNull(scope, "scope"));
    }

    @Override
    protected Byte[] typedDereference(ByteBuffer rawDereferencedValue) {
        requireNonNull(rawDereferencedValue, "rawDereferencedValue");
        var length = rawDereferencedValue.capacity();
        var result = new Byte[length];
        for (int i = 0; i < length; i++) {
            result[i] = rawDereferencedValue.asReadOnlyBuffer().get(i);
        }

        return result;
    }
}
