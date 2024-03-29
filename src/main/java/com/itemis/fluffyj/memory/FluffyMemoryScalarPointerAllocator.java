package com.itemis.fluffyj.memory;

import static java.util.Objects.requireNonNull;

import com.itemis.fluffyj.memory.api.FluffyPointer;
import com.itemis.fluffyj.memory.api.FluffyScalarPointer;
import com.itemis.fluffyj.memory.api.FluffyScalarSegment;
import com.itemis.fluffyj.memory.error.FluffyMemoryException;
import com.itemis.fluffyj.memory.internal.PointerOfByte;
import com.itemis.fluffyj.memory.internal.PointerOfInt;
import com.itemis.fluffyj.memory.internal.PointerOfLong;
import com.itemis.fluffyj.memory.internal.PointerOfString;

import java.lang.foreign.Arena;

/**
 * Helps with allocating pointers to off heap memory areas.
 *
 * @param <T> - The type of data the allocated pointer points to.
 */
public final class FluffyMemoryScalarPointerAllocator<T> {
    private final long initialValue;
    private final Class<? extends T> type;

    /**
     * Prepare to allocate a pointer to {@code toHere}.
     *
     * @param toHere - The constructed pointer will point to the address of this segment.
     */
    public FluffyMemoryScalarPointerAllocator(final FluffyScalarSegment<? extends T> toHere) {
        requireNonNull(toHere, "toHere");
        initialValue = toHere.rawAddress();
        type = toHere.getContainedType();
    }

    /**
     * Prepare to allocate a pointer to the provided {@code address}.
     *
     * @param address - The constructed pointer will point to this address.
     * @param typeOfData - Type of data the segment the provided address points to.
     */
    public FluffyMemoryScalarPointerAllocator(final long address, final Class<? extends T> typeOfData) {
        initialValue = requireNonNull(address, "address");
        type = requireNonNull(typeOfData, "typeOfData");
    }

    /**
     * Allocate the pointer. Its arena will be the auto arena.
     *
     * @return A new {@link FluffyPointer} instance.
     */
    public FluffyScalarPointer<T> allocate() {
        return allocate(Arena.ofAuto());
    }

    /**
     * Allocate the pointer and attach it to the provided {@code arena}.
     *
     * @param arena - {@link Arena} of the pointer.
     * @return A new {@link FluffyPointer} instance.
     */
    // The cast is indeed unsafe but it won't produce any ClassCastExceptions since the value the
    // pointer points to will be interpreted as T which may be wrong but does not cause any error.
    @SuppressWarnings("unchecked")
    public FluffyScalarPointer<T> allocate(final Arena arena) {
        requireNonNull(arena, "arena");

        Object result = null;
        if (type.isAssignableFrom(Long.class) || type.isAssignableFrom(long.class)) {
            result = new PointerOfLong(initialValue, arena);
        } else if (type.isAssignableFrom(Integer.class) || type.isAssignableFrom(int.class)) {
            result = new PointerOfInt(initialValue, arena);
        } else if (type.isAssignableFrom(String.class)) {
            result = new PointerOfString(initialValue, arena);
        } else if (type.isAssignableFrom(Byte.class) || type.isAssignableFrom(byte.class)) {
            result = new PointerOfByte(initialValue, arena);
        } else {
            throw new FluffyMemoryException(
                "Cannot allocate scalar pointer of unknown type: " + type.getCanonicalName());
        }

        return (FluffyScalarPointer<T>) result;
    }
}
