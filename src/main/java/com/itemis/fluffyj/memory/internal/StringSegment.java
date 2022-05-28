package com.itemis.fluffyj.memory.internal;

import static java.util.Objects.requireNonNull;
import static jdk.incubator.foreign.CLinker.toCString;
import static jdk.incubator.foreign.CLinker.toJavaString;

import com.itemis.fluffyj.memory.api.FluffyScalarSegment;

import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;
import jdk.incubator.foreign.ResourceScope;

public class StringSegment implements FluffyScalarSegment<String> {

    private final MemorySegment backingSeg;

    /**
     * Allocate a new segment.
     *
     * @param initialValue - The new segment will hold this value.
     * @param scope - The new segment will be attached to this scope, i. e. if the scope is closed,
     *        the new segment will not be alive anymore.
     */
    public StringSegment(String initialValue, ResourceScope scope) {
        this.backingSeg = toCString(requireNonNull(initialValue, "initialValue"), requireNonNull(scope, "scope"));
    }

    /**
     * Wrap the provided {@code backingSeg}. The constructed segment will be attached to the same
     * scope as the {@code backingSeg}.
     *
     * @param backingSeg - The raw segment to wrap.
     */
    public StringSegment(MemorySegment backingSeg) {
        this.backingSeg = requireNonNull(backingSeg, "backingSeg");
    }

    @Override
    public Class<? extends String> getContainedType() {
        return String.class;
    }

    @Override
    public MemoryAddress address() {
        return backingSeg.address();
    }

    @Override
    public boolean isAlive() {
        return backingSeg.scope().isAlive();
    }

    @Override
    public String getValue() {
        return toJavaString(address());
    }
}
