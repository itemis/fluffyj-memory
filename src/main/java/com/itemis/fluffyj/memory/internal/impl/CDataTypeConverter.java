package com.itemis.fluffyj.memory.internal.impl;

import static java.util.Objects.requireNonNull;

import com.itemis.fluffyj.memory.api.FluffyMemoryTypeConverter;
import com.itemis.fluffyj.memory.error.FluffyMemoryException;

import java.lang.foreign.MemoryAddress;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.ValueLayout;

public class CDataTypeConverter implements FluffyMemoryTypeConverter {

    @Override
    public MemoryLayout[] getNativeTypes(Class<?>... jvmTypes) {
        requireNonNull(jvmTypes, "jvmTypes");

        var result = new MemoryLayout[jvmTypes.length];
        for (var i = 0; i < result.length; i++) {
            result[i] = getNativeType(jvmTypes[i]);
        }

        return result;
    }

    @Override
    public MemoryLayout getNativeType(Class<?> jvmType) {
        MemoryLayout result;
        if (long.class.equals(jvmType) || Long.class.equals(jvmType)) {
            result = ValueLayout.JAVA_LONG;
        } else if (int.class.equals(jvmType) || Integer.class.equals(jvmType)) {
            result = ValueLayout.JAVA_INT;
        } else if (char.class.equals(jvmType) || Character.class.equals(jvmType)) {
            result = ValueLayout.JAVA_CHAR;
        } else if (double.class.equals(jvmType) || Double.class.equals(jvmType)) {
            result = ValueLayout.JAVA_DOUBLE;
        } else if (float.class.equals(jvmType) || Float.class.equals(jvmType)) {
            result = ValueLayout.JAVA_FLOAT;
        } else if (short.class.equals(jvmType) || Short.class.equals(jvmType)) {
            result = ValueLayout.JAVA_SHORT;
        } else if (byte.class.equals(jvmType) || Byte.class.equals(jvmType)) {
            result = ValueLayout.JAVA_BYTE;
        } else if (MemoryAddress.class.isAssignableFrom(jvmType)) {
            result = ValueLayout.ADDRESS;
        } else {
            throw new FluffyMemoryException(
                "Cannot provide native memory layout for JVM type " + jvmType.getCanonicalName());
        }
        return result;
    }

    @Override
    public Class<?> getJvmType(MemoryLayout nativeType) {
        Class<?> result;
        if (ValueLayout.JAVA_LONG.equals(nativeType)) {
            result = long.class;
        } else if (ValueLayout.JAVA_INT.equals(nativeType)) {
            result = int.class;
        } else if (ValueLayout.JAVA_CHAR.equals(nativeType)) {
            result = char.class;
        } else if (ValueLayout.JAVA_BYTE.equals(nativeType)) {
            result = byte.class;
        } else if (ValueLayout.JAVA_DOUBLE.equals(nativeType)) {
            result = double.class;
        } else if (ValueLayout.JAVA_FLOAT.equals(nativeType)) {
            result = float.class;
        } else if (ValueLayout.JAVA_SHORT.equals(nativeType)) {
            result = short.class;
        } else if (ValueLayout.ADDRESS.equals(nativeType)) {
            result = MemoryAddress.class;
        } else {
            throw new FluffyMemoryException("Cannot provide JVM type for native memory layout " + nativeType.name());
        }

        return result;
    }

    @Override
    public Class<?>[] getJvmTypes(MemoryLayout... nativeTypes) {
        requireNonNull(nativeTypes, "nativeTypes");
        var result = new Class<?>[nativeTypes.length];
        for (var i = 0; i < nativeTypes.length; i++) {
            result[i] = getJvmType(nativeTypes[i]);
        }

        return result;
    }
}
