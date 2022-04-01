package com.itemis.fluffyj.memory.internal;

import static jdk.incubator.foreign.MemoryLayouts.JAVA_LONG;

import java.nio.ByteBuffer;

import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.ResourceScope;

public class PointerOfLong extends FluffyPointerImpl<Long> {

    public PointerOfLong(MemoryAddress addressPointedTo, ResourceScope scope) {
        super(addressPointedTo, JAVA_LONG, scope);
    }

    @Override
    protected Long typedDereference(ByteBuffer rawDereferencedValue) {
        return rawDereferencedValue.getLong();
    }
}
