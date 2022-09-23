package com.itemis.fluffyj.memory.api;

import com.itemis.fluffyj.memory.error.FluffyMemoryException;

import java.lang.foreign.MemoryLayout;

/**
 * Knows how to convert between JVM and native types.
 */
public interface FluffyMemoryTypeConverter {

    /**
     * Return the native {@link MemoryLayout} matching {@code jvmType}.
     *
     * @param jvmType - JVM type to convert.
     * @return The native {@link MemoryLayout} matching the provided {@code jvmType}.
     * @throws FluffyMemoryException if no matching {@link MemoryLayout} can be found.
     */
    MemoryLayout getNativeType(Class<?> jvmType);

    /**
     * Like {@link #getNativeType(Class)} but takes and returns multiple types in order.
     *
     * @param jvmTypes - Convert all of these types.
     * @return A new array containing the native {@link MemoryLayout} instances for all provided
     *         {@code jvmTypes} in the same order as they have been passed to this method.
     * @throws FluffyMemoryException if no matching native type could be found for at least one of
     *         the provided {@code jvmTypes}.
     */
    MemoryLayout[] getNativeTypes(Class<?>... jvmTypes);

    /**
     * Return the JVM type matching the provided {@code nativeType}.
     *
     * @param nativeType - Convert this native type.
     * @return The JVM type matching the provided {@code nativeType}.
     * @throws FluffyMemoryException if no matching JVM type could be found.
     */
    Class<?> getJvmType(MemoryLayout nativeType);

    /**
     * Like {@link #getJvmTypes(MemoryLayout...)} but takes and returns multiple types in order.
     *
     * @param nativeTypes - Convert all of these types.
     * @return A new array containing the JVM types for all provided {@code nativeTypes} in the same
     *         order as they have been passed to this method.
     * @throws FluffyMemoryException if no matching JVM type could be found for at least one of the
     *         provided {@code nativeTypes}.
     */
    Class<?>[] getJvmTypes(MemoryLayout... nativeTypes);
}
