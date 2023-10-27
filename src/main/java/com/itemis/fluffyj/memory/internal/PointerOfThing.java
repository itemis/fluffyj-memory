package com.itemis.fluffyj.memory.internal;

import static java.util.Objects.requireNonNull;

import com.itemis.fluffyj.memory.internal.impl.FluffyPointerImpl;

import java.lang.foreign.Arena;

/**
 * An arbitrary pointer that just holds an address and cannot be dereferenced via Fluffy API. It is
 * thought to be used in cases where an API call requires the address of a pointer segment in order
 * to "return" the address of a newly created segment via this pointer.
 */
public class PointerOfThing extends FluffyPointerImpl {

    /**
     * @param arena - The arena to attach this pointer to. If the arena's scope has been closed, the
     *        pointer will not be alive anymore.
     */
    public PointerOfThing(final Arena arena) {
        super(0L, requireNonNull(arena, "arena"));
    }
}
