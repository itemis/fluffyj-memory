package com.itemis.fluffyj.memory.api;

import jdk.incubator.foreign.MemoryLayout;

public interface FluffyMemoryTypeConverter {
    MemoryLayout getCType(Class<?> javaType);

    Class<?> getJavaType(MemoryLayout cType);

    Class<?>[] getJavaTypes(MemoryLayout... args);
}
