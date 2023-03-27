package com.itemis.fluffyj.memory;

import static java.util.Objects.requireNonNull;

import com.itemis.fluffyj.memory.api.FluffyPointer;
import com.itemis.fluffyj.memory.api.FluffyVectorPointer;
import com.itemis.fluffyj.memory.api.FluffyVectorSegment;
import com.itemis.fluffyj.memory.error.FluffyMemoryException;
import com.itemis.fluffyj.memory.internal.PointerOfBlob;

import java.lang.foreign.SegmentScope;

/**
 * Helps with allocating pointers to off heap memory areas that hold arrays.
 *
 * @param <T> - The type of data the allocated pointer points to.
 */
public final class FluffyMemoryVectorPointerAllocator<T> {
    private final long initialValue;
    private final long byteSize;
    private final Class<? extends T[]> type;

    /**
     * Prepare to allocate a pointer to {@code toHere}.
     *
     * @param toHere - The constructed pointer will point to the address of this segment.
     */
    public FluffyMemoryVectorPointerAllocator(FluffyVectorSegment<? extends T> toHere) {
        requireNonNull(toHere, "toHere");
        initialValue = toHere.rawAddress();
        byteSize = toHere.byteSize();
        type = toHere.getContainedType();
    }

    /**
     * Prepare to allocate a pointer to the provided {@code address}.
     *
     * @param byteSize - The size of the array the pointer shall point to in bytes.
     * @param arrayType - Type of the array the provided address points to.
     */
    public FluffyMemoryVectorPointerAllocator(long address, long byteSize, Class<? extends T[]> arrayType) {
        this.initialValue = requireNonNull(address, "address");
        this.byteSize = byteSize;
        this.type = requireNonNull(arrayType, "typeOfData");
    }

    /**
     * Allocate the pointer. Its scope will be the global scope.
     *
     * @return A new {@link FluffyPointer} instance.
     */
    public FluffyVectorPointer<T> allocate() {
        return allocate(SegmentScope.global());
    }

    /**
     * Allocate the pointer and attach it to the provided {@code scope}.
     *
     * @param scope - {@link SegmentScope} of the pointer.
     * @return A new {@link FluffyPointer} instance.
     */
    // The cast is indeed unsafe but it won't produce any ClassCastExceptions since the value the
    // pointer points to will be interpreted as T anyway which may be false but does not cause any
    // error at the time the cast is done.
    @SuppressWarnings({"unchecked"})
    public FluffyVectorPointer<T> allocate(SegmentScope scope) {
        requireNonNull(scope, "scope");

        Object result = null;
        if (type.isAssignableFrom(Byte[].class)) {
            result = new PointerOfBlob(initialValue, byteSize, scope);
        } else {
            throw new FluffyMemoryException(
                "Cannot allocate vector pointer of unknown type: " + type.getCanonicalName());
        }

        return (FluffyVectorPointer<T>) result;
    }
}