package com.itemis.fluffyj.memory.internal.impl;

import static com.google.common.base.StandardSystemProperty.OS_NAME;
import static com.google.common.base.Strings.nullToEmpty;
import static jdk.incubator.foreign.CLinker.C_LONG;
import static jdk.incubator.foreign.CLinker.C_LONG_LONG;

import com.itemis.fluffyj.exceptions.InstantiationNotPermittedException;

import jdk.incubator.foreign.MemoryLayout;

public final class OsDependentLong {

    private OsDependentLong() {
        throw new InstantiationNotPermittedException();
    }

    public static MemoryLayout memoryLayout() {
        return nullToEmpty(OS_NAME.value()).trim().toLowerCase().contains("windows") ? C_LONG_LONG : C_LONG;
    }
}
