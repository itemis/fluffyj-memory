package com.itemis.fluffyj.memory;

import static jdk.incubator.foreign.ResourceScope.globalScope;

import com.itemis.fluffyj.memory.api.FluffySegment;
import com.itemis.fluffyj.memory.internal.LongSegment;

import jdk.incubator.foreign.ResourceScope;

/**
 * Helps with allocating areas of off heap memory. An allocated area of off heap memory is called a
 * segment.
 *
 * @param <T> - The type of data the segment should hold.
 */
public class FluffyMemorySegmentAllocator<T> {
    private final T initialValue;

    /**
     * @param initialValue - The allocated segment will hold this value.
     */
    public FluffyMemorySegmentAllocator(T initialValue) {
        this.initialValue = initialValue;
    }

    /**
     * @return A freshly allocated segment attached to the global scope.
     */
    public FluffySegment<T> allocate() {
        return allocate(globalScope());
    }

    /**
     * @return A freshly allocated segment attached to {@code scope}.
     */
    // We cannot convince the compiler at this point anyway so we need to make sure about type
    // safety via tests
    @SuppressWarnings("unchecked")
    public FluffySegment<T> allocate(ResourceScope scope) {
        FluffySegment<T> result = null;
        if (this.initialValue instanceof Long) {
            result = (FluffySegment<T>) new LongSegment((Long) initialValue, scope);
        }
        return result;
    }
}
