package com.itemis.fluffyj.memory;

import static java.util.Objects.requireNonNull;

import com.itemis.fluffyj.memory.api.FluffySegment;
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
     * @return A view of the native segment interpreted as {@link FluffySegment} of {@link Long}.
     *         The constructed {@link FluffySegment} will have the same scope as the native segment.
     */
    public FluffySegment<Long> asLong() {
        return new LongSegment(nativeSegment);
    }

    public FluffySegment<byte[]> asBlob() {
        return new BlobSegment(nativeSegment);
    }
}
