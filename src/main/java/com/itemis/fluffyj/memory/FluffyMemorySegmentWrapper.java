package com.itemis.fluffyj.memory;

import static java.util.Objects.requireNonNull;

import com.itemis.fluffyj.memory.api.FluffyScalarSegment;
import com.itemis.fluffyj.memory.api.FluffySegment;
import com.itemis.fluffyj.memory.api.FluffyVectorSegment;
import com.itemis.fluffyj.memory.internal.BlobSegment;
import com.itemis.fluffyj.memory.internal.LongSegment;

import jdk.incubator.foreign.MemorySegment;

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
    public FluffyMemorySegmentWrapper(MemorySegment nativeSegment) {
        requireNonNull(nativeSegment, "nativeSegment");
        this.nativeSegment = nativeSegment;
    }

    /**
     * @return A view of the native segment interpreted as {@link FluffyScalarSegment} of
     *         {@code type}. The constructed {@link FluffyScalarSegment} will have the same scope as
     *         the native segment.
     */
    // The cast is indeed unsafe. We need to make sure with good test coverage.
    @SuppressWarnings("unchecked")
    public <T> FluffyScalarSegment<? extends T> as(Class<? extends T> type) {
        return (FluffyScalarSegment<? extends T>) new LongSegment(nativeSegment);
    }

    /**
     * @return A view of the native segment interpreted as {@link FluffyVectorSegment} of
     *         {@code type}. The constructed {@link FluffyVectorSegment} will have the same scope as
     *         the native segment.
     */
    // The cast is indeed unsafe. We need to make sure with good test coverage.
    @SuppressWarnings("unchecked")
    public <T> FluffyVectorSegment<? extends T> asArray(Class<? extends T[]> type) {
        return (FluffyVectorSegment<? extends T>) new BlobSegment(nativeSegment);
    }
}