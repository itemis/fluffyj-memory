package com.itemis.fluffyj.memory.internal;

import static java.util.Objects.requireNonNull;

import com.itemis.fluffyj.memory.api.FluffyScalarSegment;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentAllocator;
import java.lang.foreign.SegmentScope;

public class StringSegment implements FluffyScalarSegment<String> {

    private final SegmentScope scope;
    private final MemorySegment backingSeg;

    /**
     * Allocate a new segment.
     *
     * @param initialValue - The new segment will hold this value.
     * @param scope - The new segment will be attached to this scope.
     */
    public StringSegment(String initialValue, SegmentScope scope) {
        this.scope = requireNonNull(scope, "scope");
        this.backingSeg = SegmentAllocator.nativeAllocator(scope)
            .allocateUtf8String(requireNonNull(initialValue, "initialValue"));
    }

    /**
     * Wrap the provided {@code backingSeg}. The constructed segment will be attached to the same
     * scope as the {@code backingSeg}.
     *
     * @param backingSeg - The raw segment to wrap.
     */
    public StringSegment(MemorySegment backingSeg) {
        this.scope = backingSeg.scope();
        this.backingSeg = requireNonNull(backingSeg, "backingSeg");
    }

    @Override
    public Class<? extends String> getContainedType() {
        return String.class;
    }

    @Override
    public long address() {
        return backingSeg.address();
    }

    @Override
    public MemorySegment addressAsSeg() {
        return MemorySegment.ofAddress(address(), 0, scope);
    }

    @Override
    public boolean isAlive() {
        return backingSeg.scope().isAlive();
    }

    @Override
    public String getValue() {
        return backingSeg.getUtf8String(0);
    }
}
