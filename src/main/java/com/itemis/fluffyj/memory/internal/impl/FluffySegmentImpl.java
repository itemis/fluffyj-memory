package com.itemis.fluffyj.memory.internal.impl;

import static jdk.incubator.foreign.MemorySegment.allocateNative;

import com.itemis.fluffyj.memory.api.FluffySegment;

import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemoryLayout;
import jdk.incubator.foreign.MemorySegment;
import jdk.incubator.foreign.ResourceScope;

/**
 * Default implementation of a segment.
 */
public abstract class FluffySegmentImpl implements FluffySegment {

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

    @Override
    public boolean isAlive() {
        return scope.isAlive();
    }

    @Override
    public MemoryAddress address() {
        return backingSeg.address();
    }
}
