package com.itemis.fluffyj.memory;

import static java.util.Objects.requireNonNull;

import com.itemis.fluffyj.memory.api.FluffyVectorSegment;
import com.itemis.fluffyj.memory.error.FluffyMemoryException;
import com.itemis.fluffyj.memory.internal.BlobSegment;

import java.lang.foreign.Arena;

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
    public FluffyMemoryVectorSegmentAllocator(final T[] initialValue) {
        requireNonNull(initialValue, "initialValue");
        this.initialValue = initialValue;
    }

    /**
     * @return A freshly allocated segment attached to the auto scope.
     */
    public FluffyVectorSegment<T> allocate() {
        return allocate(Arena.ofAuto());
    }

    /**
     * @return A freshly allocated segment attached to the provided {@code arena}.
     */
    // We cannot convince the compiler at this point anyway so we need to make sure about type
    // safety via tests
    @SuppressWarnings("unchecked")
    public FluffyVectorSegment<T> allocate(final Arena arena) {
        requireNonNull(arena, "arena");

        Object result = null;
        if (!initialValue.getClass().componentType().isAssignableFrom(Byte.class)) {
            throw new FluffyMemoryException(
                "Cannot allocate vector segment of unknown type: " + initialValue.getClass().getCanonicalName());
        }
        result = new BlobSegment((Byte[]) initialValue, arena);

        return (FluffyVectorSegment<T>) result;
    }
}
