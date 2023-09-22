package com.itemis.fluffyj.memory;

import static java.util.Objects.requireNonNull;

import com.itemis.fluffyj.memory.api.FluffyScalarSegment;
import com.itemis.fluffyj.memory.error.FluffyMemoryException;
import com.itemis.fluffyj.memory.internal.ByteSegment;
import com.itemis.fluffyj.memory.internal.IntSegment;
import com.itemis.fluffyj.memory.internal.LongSegment;
import com.itemis.fluffyj.memory.internal.StringSegment;

import java.lang.foreign.Arena;

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
    public FluffyMemoryScalarSegmentAllocator(final T initialValue) {
        requireNonNull(initialValue, "initialValue");
        this.initialValue = initialValue;
    }

    /**
     * @return A freshly allocated segment attached to the auto arena.
     */
    public FluffyScalarSegment<T> allocate() {
        return allocate(Arena.ofAuto());
    }

    /**
     * @return A freshly allocated segment attached to the provided {@code arena}.
     */
    // We cannot convince the compiler at this point anyway so we need to make sure about type
    // safety via tests
    @SuppressWarnings("unchecked")
    public FluffyScalarSegment<T> allocate(final Arena arena) {
        requireNonNull(arena, "arena");

        Object result = null;
        if (initialValue instanceof Long) {
            result = new LongSegment((Long) initialValue, arena);
        } else if (initialValue instanceof Integer) {
            result = new IntSegment((Integer) initialValue, arena);
        } else if (initialValue instanceof String) {
            result = new StringSegment((String) initialValue, arena);
        } else if (initialValue instanceof Byte) {
            result = new ByteSegment((Byte) initialValue, arena);
        } else {
            throw new FluffyMemoryException(
                "Cannot allocate scalar segment of unknown type: " + initialValue.getClass().getCanonicalName());
        }
        return (FluffyScalarSegment<T>) result;
    }
}
