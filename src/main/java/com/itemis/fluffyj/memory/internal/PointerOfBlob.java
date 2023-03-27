package com.itemis.fluffyj.memory.internal;

import static java.util.Objects.requireNonNull;

import com.itemis.fluffyj.memory.internal.impl.FluffyVectorPointerImpl;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentScope;
import java.lang.foreign.ValueLayout;

public class PointerOfBlob extends FluffyVectorPointerImpl<Byte> {

    /**
     * Allocate a new pointer.
     *
     * @param addressPointedTo - The address the new pointer will point to.
     * @param byteSize - Size of the vector this pointer points to in bytes.
     * @param scope - Attach the new pointer to this scope.
     */
    public PointerOfBlob(long addressPointedTo, long byteSize, SegmentScope scope) {
        super(requireNonNull(addressPointedTo, "addressPointedTo"), byteSize, requireNonNull(scope, "scope"));
    }

    @Override
    public Byte[] dereference() {
        var addr = getRawValue();
        var valSeg = MemorySegment.ofAddress(addr, byteSize, scope);
        var result = new Byte[(int) byteSize];
        for (var i = 0; i < result.length; i++) {
            result[i] = valSeg.get(ValueLayout.JAVA_BYTE, i);
        }

        return result;
    }
}
