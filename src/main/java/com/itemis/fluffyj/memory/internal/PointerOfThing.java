package com.itemis.fluffyj.memory.internal;

import com.itemis.fluffyj.memory.internal.impl.FluffyPointerImpl;

import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.ResourceScope;

/**
 * An arbitrary pointer that just holds an address and cannot be dereferenced via Fluffy API. It is
 * thought to be used in cases where an API call requires the address of a pointer segment in order
 * to "return" the address of a newly created segment via this pointer.
 */
public class PointerOfThing extends FluffyPointerImpl {

    /**
     * @param scope - The scope to attach this pointer to. If the scope is closed, the pointer will
     *        not be alive anymore.
     */
    public PointerOfThing(ResourceScope scope) {
        super(MemoryAddress.NULL, scope);
    }
}
