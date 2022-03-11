package com.itemis.fluffyj.memory;

import static jdk.incubator.foreign.MemoryLayouts.JAVA_LONG;
import static jdk.incubator.foreign.MemorySegment.allocateNative;

import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;
import jdk.incubator.foreign.ResourceScope;

public class LongSegment {

    /**
     * Instances of this class hold this value as default if no other has been set upon
     * construction.
     */
    public static final long DEFAULT_VALUE = -1;

    private final MemorySegment backingSeg;
    private final ResourceScope scope;

    public LongSegment(long initialValue, ResourceScope scope) {
        this(allocateNative(JAVA_LONG, scope));
        backingSeg.asByteBuffer().putLong(initialValue);
    }

    public LongSegment(MemorySegment backingSeg) {
        this.backingSeg = backingSeg;
        this.scope = backingSeg.scope();
    }

    public long getValue() {
        return backingSeg.asByteBuffer().getLong();
    }

    public boolean isAlive() {
        return scope.isAlive();
    }

    public MemoryAddress address() {
        return backingSeg.address();
    }
}
