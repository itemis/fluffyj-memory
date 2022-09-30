package com.itemis.fluffyj.memory;

import static java.lang.foreign.MemorySession.global;
import static java.util.Objects.requireNonNull;

import com.itemis.fluffyj.memory.api.FluffyVectorSegment;
import com.itemis.fluffyj.memory.error.FluffyMemoryException;
import com.itemis.fluffyj.memory.internal.BlobSegment;

import java.lang.foreign.MemorySession;

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
     * @return A freshly allocated segment attached to the global session.
     */
    public FluffyVectorSegment<T> allocate() {
        return allocate(global());
    }

    /**
     * @return A freshly allocated segment attached to {@code session}.
     */
    // We cannot convince the compiler at this point anyway so we need to make sure about type
    // safety via tests
    @SuppressWarnings("unchecked")
    public FluffyVectorSegment<T> allocate(MemorySession session) {
        requireNonNull(session, "session");

        Object result = null;
        if (initialValue.getClass().componentType().isAssignableFrom(Byte.class)) {
            result = new BlobSegment((Byte[]) initialValue, session);
        } else {
            throw new FluffyMemoryException(
                "Cannot allocate vector segment of unknown type: " + initialValue.getClass().getCanonicalName());
        }

        return (FluffyVectorSegment<T>) result;
    }
}
