package com.itemis.fluffyj.memory;

import static java.util.Objects.requireNonNull;
import static jdk.incubator.foreign.ResourceScope.globalScope;

import com.itemis.fluffyj.memory.api.FluffyVectorSegment;
import com.itemis.fluffyj.memory.error.FluffyMemoryException;
import com.itemis.fluffyj.memory.internal.BlobSegment;

import jdk.incubator.foreign.ResourceScope;

/**
 * Helps with allocating areas of off heap memory that contain vectorized data, i. e. arrays.
 *
 * @param <T> - The component type of array data the allocated segment should hold.
 */
public final class FluffyMemoryVectorSegmentAllocator<T> {
    private final T[] initialValue;

    /**
     * @param initialValue - The allocated segment will hold this value.
     */
    public FluffyMemoryVectorSegmentAllocator(T[] initialValue) {
        requireNonNull(initialValue, "initialValue");
        this.initialValue = initialValue;
    }

    /**
     * @return A freshly allocated segment attached to the global scope.
     */
    public FluffyVectorSegment<? extends T> allocate() {
        return allocate(globalScope());
    }

    /**
     * @return A freshly allocated segment attached to {@code scope}.
     */
    // We cannot convince the compiler at this point anyway so we need to make sure about type
    // safety via tests
    @SuppressWarnings("unchecked")
    public FluffyVectorSegment<? extends T> allocate(ResourceScope scope) {
        requireNonNull(scope, "scope");

        Object result = null;
        if (initialValue.getClass().componentType().isAssignableFrom(Byte.class)) {
            result = new BlobSegment((Byte[]) initialValue, scope);
        } else {
            throw new FluffyMemoryException("Cannot allocate vector segment of unknown type: " + initialValue.getClass().getCanonicalName());
        }

        return (FluffyVectorSegment<? extends T>) result;
    }
}
