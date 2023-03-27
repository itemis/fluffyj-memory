package com.itemis.fluffyj.memory.internal;

import static java.lang.foreign.ValueLayout.JAVA_INT;
import static java.util.Objects.requireNonNull;

import com.itemis.fluffyj.memory.api.FluffyPointer;
import com.itemis.fluffyj.memory.internal.impl.FluffyScalarPointerImpl;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentScope;
import java.lang.foreign.ValueLayout;

/**
 * A {@link FluffyPointer} that points to a segment that holds an {@link Integer}.
 */
public class PointerOfInt extends FluffyScalarPointerImpl<Integer> {

    /**
     * Allocate a new pointer.
     *
     * @param addressPointedTo - The address the new pointer will point to.
     * @param scope - Attach the new pointer to this scope.
     */
    public PointerOfInt(long addressPointedTo, SegmentScope scope) {
        super(requireNonNull(addressPointedTo, "addressPointedTo"), JAVA_INT.byteSize(),
            requireNonNull(scope, "scope"));
    }

    @Override
    public Integer dereference() {
        return MemorySegment.ofAddress(getValue(), JAVA_INT.byteSize(), scope).get(ValueLayout.JAVA_INT, 0);
    }
}
