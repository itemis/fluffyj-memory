package com.itemis.fluffyj.memory.api;

/**
 * A pointer to a segment that holds a scalar (i. e. non array) value. Could also be the first value
 * of an array.
 *
 * @param <T> - The type of data this pointer points to.
 */
public interface FluffyScalarPointer<T> extends FluffyPointer {
    /**
     * @return The value that this pointer points to interpreted as type {@code T}.
     */
    T dereference();
}
