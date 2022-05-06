package com.itemis.fluffyj.memory.internal;

import static java.util.Objects.requireNonNull;
import static jdk.incubator.foreign.MemoryLayout.sequenceLayout;
import static jdk.incubator.foreign.MemoryLayouts.JAVA_BYTE;

import java.nio.ByteBuffer;

import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.ResourceScope;

public class PointerOfBlob extends FluffyPointerImpl<byte[]> {

    /**
     * Allocate a new pointer.
     *
     * @param addressPointedTo - The {@link MemoryAddress} the new pointer will point to.
     * @param scope - Attach the new pointer to this scope.
     */
    public PointerOfBlob(MemoryAddress addressPointedTo, int byteCount, ResourceScope scope) {
        super(requireNonNull(addressPointedTo, "addressPointedTo"), sequenceLayout(byteCount, JAVA_BYTE), requireNonNull(scope, "scope"));
    }

    @Override
    protected byte[] typedDereference(ByteBuffer rawDereferencedValue) {
        requireNonNull(rawDereferencedValue, "rawDereferencedValue");
        var length = rawDereferencedValue.capacity();
        var result = new byte[length];
        rawDereferencedValue.asReadOnlyBuffer().get(result);

        return result;
    }
}
