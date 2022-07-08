package com.itemis.fluffyj.memory.api;

import com.itemis.fluffyj.memory.error.FluffyMemoryException;

import jdk.incubator.foreign.MemoryLayout;

/**
 * Knows how to convert between Java and native types.
 */
public interface FluffyMemoryTypeConverter {

    /**
     * Return the native {@link MemoryLayout} matching {@code javaType}.
     *
     * @param javaType - Java type to convert.
     * @return The native {@link MemoryLayout} matching the provided {@code javaType}.
     * @throws FluffyMemoryException if no matching {@link MemoryLayout} can be found.
     */
    MemoryLayout getNativeType(Class<?> javaType);

    /**
     * Like {@link #getNativeType(Class)} but takes and returns multiple types in order.
     *
     * @param javaTypes - Convert all of these types.
     * @return A new array containing the native {@link MemoryLayout} instances for all provided
     *         {@code javaTypes} in the same order as they have been passed to this method.
     * @throws FluffyMemoryException if no matching native type could be found for at least one of
     *         the provided {@code javaTypes}.
     */
    MemoryLayout[] getNativeTypes(Class<?>... javaTypes);

    /**
     * Return the Java type matching the provided {@code nativeType}.
     *
     * @param nativeType - Convert this native type.
     * @return The Java type matching the provided {@code nativeType}.
     * @throws FluffyMemoryException if no matching Java type could be found.
     */
    Class<?> getJavaType(MemoryLayout nativeType);

    /**
     * Like {@link #getJavaTypes(MemoryLayout...)} but takes and returns multiple types in order.
     *
     * @param nativeTypes - Convert all of these types.
     * @return A new array containing the Java types for all provided {@code nativeTypes} in the
     *         same order as they have been passed to this method.
     * @throws FluffyMemoryException if no matching Java type could be found for at least one of the
     *         provided {@code nativeTypes}.
     */
    Class<?>[] getJavaTypes(MemoryLayout... nativeTypes);
}
