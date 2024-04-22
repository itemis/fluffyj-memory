package com.itemis.fluffyj.memory.internal;

import static java.lang.foreign.MemorySegment.ofAddress;
import static java.util.Objects.requireNonNull;

import com.itemis.fluffyj.memory.api.FluffyScalarSegment;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.MemorySegment.Scope;

public class StringSegment implements FluffyScalarSegment<String> {

    private final Scope scope;
    private final MemorySegment backingSeg;

    /**
     * Allocate a new segment.
     *
     * @param initialValue - The new segment will hold this value.
     * @param arena - The new segment will be attached to this arena.
     */
    public StringSegment(final String initialValue, final Arena arena) {
        this.scope = requireNonNull(arena, "arena").scope();
        this.backingSeg = arena.allocateFrom(requireNonNull(initialValue, "initialValue"));
    }

    /**
     * Wrap the provided {@code backingSeg}. The constructed segment will be attached to the same
     * arena as the {@code backingSeg}.
     *
     * @param backingSeg - The raw segment to wrap.
     */
    public StringSegment(final MemorySegment backingSeg) {
        this.backingSeg = requireNonNull(backingSeg, "backingSeg");
        this.scope = backingSeg.scope();
    }

    @Override
    public Class<? extends String> getContainedType() {
        return String.class;
    }

    @Override
    public MemorySegment address() {
        return ofAddress(rawAddress());
    }

    @Override
    public long rawAddress() {
        return backingSeg.address();
    }

    @Override
    public boolean isAlive() {
        return scope.isAlive();
    }

    @Override
    public String getValue() {
        return backingSeg.getString(0);
    }
}
