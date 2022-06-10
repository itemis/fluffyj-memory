package com.itemis.fluffyj.memory.internal.impl;

import static java.util.Objects.requireNonNull;

import com.itemis.fluffyj.memory.api.FluffyMemoryTypeConverter;
import com.itemis.fluffyj.memory.error.FluffyMemoryException;

import jdk.incubator.foreign.CLinker;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemoryLayout;

public class CDataTypeConverter implements FluffyMemoryTypeConverter {

    @Override
    public MemoryLayout getCType(Class<?> javaType) {
        MemoryLayout result;
        if (long.class.equals(javaType) || Long.class.equals(javaType)) {
            result = OsDependentLong.memoryLayout();
        } else if (int.class.equals(javaType) || Integer.class.equals(javaType)) {
            result = CLinker.C_INT;
        } else if (char.class.equals(javaType) || Character.class.equals(javaType)) {
            result = CLinker.C_CHAR;
        } else if (double.class.equals(javaType) || Double.class.equals(javaType)) {
            result = CLinker.C_DOUBLE;
        } else if (float.class.equals(javaType) || Float.class.equals(javaType)) {
            result = CLinker.C_FLOAT;
        } else if (short.class.equals(javaType) || Short.class.equals(javaType)) {
            result = CLinker.C_SHORT;
        } else {
            throw new FluffyMemoryException("Cannot provide C memory layout for type " + javaType.getCanonicalName());
        }
        return result;
    }

    @Override
    public Class<?> getJavaType(MemoryLayout cType) {
        Class<?> result;
        if (CLinker.C_LONG.equals(cType) || CLinker.C_LONG_LONG.equals(cType)) {
            result = long.class;
        } else if (CLinker.C_INT.equals(cType)) {
            result = int.class;
        } else if (CLinker.C_CHAR.equals(cType)) {
            result = char.class;
        } else if (CLinker.C_DOUBLE.equals(cType)) {
            result = double.class;
        } else if (CLinker.C_FLOAT.equals(cType)) {
            result = float.class;
        } else if (CLinker.C_SHORT.equals(cType)) {
            result = short.class;
        } else if (CLinker.C_POINTER.equals(cType)) {
            result = MemoryAddress.class;
        } else {
            throw new FluffyMemoryException("Cannot provide Java type for C memory layout " + cType.name());
        }

        return result;
    }

    @Override
    public Class<?>[] getJavaTypes(MemoryLayout... cTypes) {
        requireNonNull(cTypes, "cTypes");
        var result = new Class<?>[cTypes.length];
        for (var i = 0; i < cTypes.length; i++) {
            result[i] = getJavaType(cTypes[i]);
        }

        return result;
    }

}
