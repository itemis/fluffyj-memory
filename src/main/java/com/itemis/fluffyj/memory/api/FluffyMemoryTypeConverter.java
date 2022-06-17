package com.itemis.fluffyj.memory.api;

import jdk.incubator.foreign.MemoryLayout;

public interface FluffyMemoryTypeConverter {

    MemoryLayout getNativeType(Class<?> javaType);

    MemoryLayout[] getNativeTypes(Class<?>... javaTypes);

    Class<?> getJavaType(MemoryLayout nativeType);

    Class<?>[] getJavaTypes(MemoryLayout... nativeTypes);
}
