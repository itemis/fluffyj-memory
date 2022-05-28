package com.itemis.fluffyj.memory;

import static java.util.Objects.requireNonNull;
import static jdk.incubator.foreign.ResourceScope.globalScope;

import com.itemis.fluffyj.memory.api.FluffyScalarSegment;
import com.itemis.fluffyj.memory.error.FluffyMemoryException;
import com.itemis.fluffyj.memory.internal.IntSegment;
import com.itemis.fluffyj.memory.internal.LongSegment;
import com.itemis.fluffyj.memory.internal.StringSegment;

import jdk.incubator.foreign.ResourceScope;

/**
 * Helps with allocating areas of off heap memory. An allocated area of off heap memory is called a
 * segment. This allocator may allocate segments that hold scalar data, i. e. non arrays.
 *
 * @param <T> - The type of data the segment should hold.
 */
public final class FluffyMemoryScalarSegmentAllocator<T> {
    private final T initialValue;

    /**
     * @param initialValue - The allocated segment will hold this value.
     */
    public FluffyMemoryScalarSegmentAllocator(T initialValue) {
        requireNonNull(initialValue, "initialValue");
        this.initialValue = initialValue;
    }

    /**
     * @return A freshly allocated segment attached to the global scope.
     */
    public FluffyScalarSegment<? extends T> allocate() {
        return allocate(globalScope());
    }

    /**
     * @return A freshly allocated segment attached to {@code scope}.
     */
    // We cannot convince the compiler at this point anyway so we need to make sure about type
    // safety via tests
    @SuppressWarnings("unchecked")
    public FluffyScalarSegment<? extends T> allocate(ResourceScope scope) {
        requireNonNull(scope, "scope");

        Object result = null;
        if (initialValue instanceof Long) {
            result = new LongSegment((Long) initialValue, scope);
        } else if (initialValue instanceof Integer) {
            result = new IntSegment((Integer) initialValue, scope);
        } else if (initialValue instanceof String) {
            result = new StringSegment((String) initialValue, scope);
        } else {
            throw new FluffyMemoryException("Cannot allocate scalar segment of unknown type: " + initialValue.getClass().getCanonicalName());
        }
        return (FluffyScalarSegment<? extends T>) result;
    }
}
