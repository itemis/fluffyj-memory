package com.itemis.fluffyj.memory;

import static jdk.incubator.foreign.ResourceScope.globalScope;

import com.itemis.fluffyj.memory.api.FluffySegment;
import com.itemis.fluffyj.memory.internal.LongSegment;

import jdk.incubator.foreign.ResourceScope;

/**
 * @param <T> - The type of data the allocated segment should hold.
 */
public class FluffyMemorySegmentAllocator<T> {
    private final T initialValue;

    public FluffyMemorySegmentAllocator(T initialValue) {
        this.initialValue = initialValue;
    }

    public FluffySegment<T> allocate() {
        return allocate(globalScope());
    }

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
