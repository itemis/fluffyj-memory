package com.itemis.fluffyj.memory;

import static jdk.incubator.foreign.MemoryLayouts.JAVA_LONG;
import static jdk.incubator.foreign.ResourceScope.globalScope;

import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.ResourceScope;

public class PointerOfLong {

    private final LongSegment me;
    private final ResourceScope scope;

    public PointerOfLong(MemoryAddress addressPointedTo, ResourceScope scope) {
        this.me = new LongSegment(addressPointedTo.toRawLongValue(), globalScope());
        this.scope = scope;
    }

    public MemoryAddress getValue() {
        return MemoryAddress.ofLong(me.getValue());
    }

    public MemoryAddress address() {
        return me.address();
    }

    public long dereference() {
        return getValue().asSegment(JAVA_LONG.byteSize(), scope).asByteBuffer().getLong();
    }

    public boolean isAlive() {
        return scope.isAlive();
    }
}
