package com.itemis.fluffyj.memory.internal.impl;

import static java.util.Objects.requireNonNull;

import com.itemis.fluffyj.memory.api.FluffyMemoryTypeConverter;
import com.itemis.fluffyj.memory.error.FluffyMemoryException;

import jdk.incubator.foreign.CLinker;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemoryLayout;

public class CDataTypeConverter implements FluffyMemoryTypeConverter {

    @Override
    public MemoryLayout[] getNativeTypes(Class<?>... javaTypes) {
        requireNonNull(javaTypes, "javaTypes");

        var result = new MemoryLayout[javaTypes.length];
        for (var i = 0; i < result.length; i++) {
            result[i] = getNativeType(javaTypes[i]);
        }

        return result;
    }

    @Override
    public MemoryLayout getNativeType(Class<?> javaType) {
        MemoryLayout result;
        if (long.class.equals(javaType) || Long.class.equals(javaType)) {
            result = OsDependentLong.memoryLayout();
        } else if (int.class.equals(javaType) || Integer.class.equals(javaType)) {
            result = CLinker.C_INT;
        } else if (char.class.equals(javaType) || Character.class.equals(javaType)) {
            result = CLinker.C_SHORT;
        } else if (double.class.equals(javaType) || Double.class.equals(javaType)) {
            result = CLinker.C_DOUBLE;
        } else if (float.class.equals(javaType) || Float.class.equals(javaType)) {
            result = CLinker.C_FLOAT;
        } else if (short.class.equals(javaType) || Short.class.equals(javaType)) {
            result = CLinker.C_SHORT;
        } else if (byte.class.equals(javaType) || Byte.class.equals(javaType)) {
            result = CLinker.C_CHAR;
        } else if (MemoryAddress.class.isAssignableFrom(javaType)) {
            result = CLinker.C_POINTER;
        } else {
            throw new FluffyMemoryException("Cannot provide C memory layout for type " + javaType.getCanonicalName());
        }
        return result;
    }

    @Override
    public Class<?> getJavaType(MemoryLayout nativeType) {
        Class<?> result;
        if (CLinker.C_LONG.equals(nativeType) || CLinker.C_LONG_LONG.equals(nativeType)) {
            result = long.class;
        } else if (CLinker.C_INT.equals(nativeType)) {
            result = int.class;
        } else if (CLinker.C_CHAR.equals(nativeType)) {
            result = byte.class;
        } else if (CLinker.C_DOUBLE.equals(nativeType)) {
            result = double.class;
        } else if (CLinker.C_FLOAT.equals(nativeType)) {
            result = float.class;
        } else if (CLinker.C_SHORT.equals(nativeType)) {
            result = short.class;
        } else if (CLinker.C_POINTER.equals(nativeType)) {
            result = MemoryAddress.class;
        } else {
            throw new FluffyMemoryException("Cannot provide Java type for C memory layout " + nativeType.name());
        }

        return result;
    }

    @Override
    public Class<?>[] getJavaTypes(MemoryLayout... nativeTypes) {
        requireNonNull(nativeTypes, "cTypes");
        var result = new Class<?>[nativeTypes.length];
        for (var i = 0; i < nativeTypes.length; i++) {
            result[i] = getJavaType(nativeTypes[i]);
        }

        return result;
    }
}
