package com.itemis.fluffyj.memory;

import static java.util.Objects.requireNonNull;

import com.itemis.fluffyj.memory.FluffyMemoryPointerBuilder.FluffyMemoryTypedPointerBuilder;
import com.itemis.fluffyj.memory.api.FluffyScalarSegment;
import com.itemis.fluffyj.memory.api.FluffySegment;
import com.itemis.fluffyj.memory.api.FluffyVectorSegment;
import com.itemis.fluffyj.memory.error.FluffyMemoryException;
import com.itemis.fluffyj.memory.internal.BlobSegment;
import com.itemis.fluffyj.memory.internal.ByteSegment;
import com.itemis.fluffyj.memory.internal.IntSegment;
import com.itemis.fluffyj.memory.internal.LongSegment;
import com.itemis.fluffyj.memory.internal.StringSegment;

import java.lang.foreign.MemorySegment;

/**
 * Helps with wrapping "untyped" off heap memory areas ("segments") into {@link FluffySegment}s.
 */
public final class FluffyMemorySegmentWrapper {

    private final MemorySegment nativeSegment;

    /**
     * Initialize the wrapper.
     *
     * @param nativeSegment - This segment will be wrapped.
     */
    public FluffyMemorySegmentWrapper(final MemorySegment nativeSegment) {
        requireNonNull(nativeSegment, "nativeSegment");
        this.nativeSegment = nativeSegment;
    }

    /**
     * <T> - Type of data the segment shall point to.
     *
     * @return A view of the native segment interpreted as {@link FluffyScalarSegment} of
     *         {@code type}. The constructed {@link FluffyScalarSegment} will belong to the same
     *         arena as the native segment.
     */
    // The cast is indeed unsafe. We need to make sure with good test coverage.
    @SuppressWarnings("unchecked")
    public <T> FluffyScalarSegment<T> as(final Class<? extends T> type) {
        requireNonNull(type, "type");

        Object result = null;
        if (type.isAssignableFrom(Long.class) || type.isAssignableFrom(long.class)) {
            result = new LongSegment(nativeSegment);
        } else if (type.isAssignableFrom(Integer.class) || type.isAssignableFrom(int.class)) {
            result = new IntSegment(nativeSegment);
        } else if (type.isAssignableFrom(String.class)) {
            result = new StringSegment(nativeSegment);
        } else if (type.isAssignableFrom(Byte.class) || type.isAssignableFrom(byte.class)) {
            result = new ByteSegment(nativeSegment);
        } else {
            throw new FluffyMemoryException("Cannot wrap scalar segment of unknown type: " + type.getCanonicalName());
        }
        return (FluffyScalarSegment<T>) result;
    }

    public <T> FluffyMemoryScalarPointerAllocator<T> asPointerOf(final Class<? extends T> type) {
        return new FluffyMemoryTypedPointerBuilder(nativeSegment.address()).as(type);
    }

    /**
     * @return A view of the native segment interpreted as {@link FluffyVectorSegment} of
     *         {@code type}. The constructed {@link FluffyVectorSegment} will belong to the same
     *         arena as the native segment.
     */
    // The cast is indeed unsafe. We need to make sure with good test coverage.
    @SuppressWarnings("unchecked")
    public <T> FluffyVectorSegment<T> asArray(final Class<? extends T[]> type) {
        requireNonNull(type, "type");

        Object result = null;
        if (!type.isAssignableFrom(Byte[].class)) {
            throw new FluffyMemoryException("Cannot wrap vector segment of unknown type: " + type.getCanonicalName());
        }
        result = new BlobSegment(nativeSegment);
        return (FluffyVectorSegment<T>) result;
    }
}
