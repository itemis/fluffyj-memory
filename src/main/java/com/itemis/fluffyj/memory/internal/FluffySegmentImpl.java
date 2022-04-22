package com.itemis.fluffyj.memory.internal;

import static jdk.incubator.foreign.MemorySegment.allocateNative;

import com.itemis.fluffyj.memory.api.FluffySegment;

import java.nio.ByteBuffer;

import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemoryLayout;
import jdk.incubator.foreign.MemorySegment;
import jdk.incubator.foreign.ResourceScope;

/**
 * Default implementation of a generic segment.
 *
 * @param <T> - Type of data this segment holds.
 */
abstract class FluffySegmentImpl<T> implements FluffySegment<T> {

    protected final MemorySegment backingSeg;
    protected final ResourceScope scope;

    /**
     * Wrap the provided {@link MemorySegment}.
     *
     * @param backingSeg - The raw segment to wrap.
     */
    public FluffySegmentImpl(MemorySegment backingSeg) {
        this.backingSeg = backingSeg;
        this.scope = backingSeg.scope();
    }

    /**
     * Allocate new segment.
     *
     * @param initialValue - The new segment will hold this value.
     * @param layout - The new segment will have this {@link MemoryLayout}.
     * @param scope - The new segment will be attached to this scope, i. e. if this scope is closed,
     *        the segment will not be alive anymore.
     */
    public FluffySegmentImpl(byte[] initialValue, MemoryLayout layout, ResourceScope scope) {
        this(allocateNative(layout, scope));
        backingSeg.asByteBuffer().put(initialValue);
    }

    /**
     * @param rawValue - A read only {@link ByteBuffer} that contains the bytes this segment holds.
     * @return The typed interpretation if this segment's bytes.
     */
    protected abstract T getTypedValue(ByteBuffer rawValue);

    @Override
    public T getValue() {
        return getTypedValue(backingSeg.asByteBuffer().asReadOnlyBuffer());
    }

    @Override
    public boolean isAlive() {
        return scope.isAlive();
    }

    @Override
    public MemoryAddress address() {
        return backingSeg.address();
    }
}
