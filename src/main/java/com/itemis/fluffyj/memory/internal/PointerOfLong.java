package com.itemis.fluffyj.memory.internal;

import static java.lang.foreign.ValueLayout.JAVA_LONG;
import static java.util.Objects.requireNonNull;

import com.itemis.fluffyj.memory.api.FluffyPointer;
import com.itemis.fluffyj.memory.internal.impl.FluffyScalarPointerImpl;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentScope;
import java.lang.foreign.ValueLayout;

/**
 * A {@link FluffyPointer} that points to a segment that holds a {@link Long}.
 */
public class PointerOfLong extends FluffyScalarPointerImpl<Long> {

    /**
     * Allocate a new pointer.
     *
     * @param addressPointedTo - The {@link MemorySegment} the new pointer will point to.
     * @param scope - Attach the new pointer to this scope.
     */
    public PointerOfLong(long addressPointedTo, SegmentScope scope) {
        super(requireNonNull(addressPointedTo, "addressPointedTo"), JAVA_LONG.byteSize(),
            requireNonNull(scope, "scope"));
    }

    @Override
    public Long dereference() {
        return MemorySegment.ofAddress(getRawValue(), JAVA_LONG.byteSize(), scope).get(ValueLayout.JAVA_LONG, 0);
    }
}
