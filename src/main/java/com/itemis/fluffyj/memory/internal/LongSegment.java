package com.itemis.fluffyj.memory.internal;

import static jdk.incubator.foreign.MemoryLayouts.JAVA_LONG;

import java.nio.ByteBuffer;

import jdk.incubator.foreign.MemoryLayout;
import jdk.incubator.foreign.MemorySegment;
import jdk.incubator.foreign.ResourceScope;

public class LongSegment extends FluffySegmentImpl<Long> {

    private static final MemoryLayout MY_LAYOUT = JAVA_LONG;
    /**
     * Instances of this class hold this value as default if no other has been set upon
     * construction.
     */
    public static final long DEFAULT_VALUE = -1;

    public LongSegment(long initialValue, ResourceScope scope) {
        super(ByteBuffer.allocate((int) MY_LAYOUT.byteSize()).putLong(initialValue).array(), MY_LAYOUT, scope);
    }

    public LongSegment(MemorySegment backingSeg) {
        super(backingSeg);
    }

    @Override
    protected Long getTypedValue(ByteBuffer rawValue) {
        return rawValue.getLong();
    }
}
