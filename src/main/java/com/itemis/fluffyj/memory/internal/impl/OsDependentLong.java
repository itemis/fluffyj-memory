package com.itemis.fluffyj.memory.internal.impl;

import static com.google.common.base.StandardSystemProperty.OS_NAME;
import static com.google.common.base.Strings.nullToEmpty;
import static jdk.incubator.foreign.CLinker.C_LONG;
import static jdk.incubator.foreign.CLinker.C_LONG_LONG;

import com.itemis.fluffyj.exceptions.InstantiationNotPermittedException;

import jdk.incubator.foreign.MemoryLayout;

/**
 * This is a static helper for java.lang.long to C's long conversion. On some operating systems -
 * most notably Windows - C's long is a bit different from Unix C's long.
 */
public final class OsDependentLong {

    private OsDependentLong() {
        throw new InstantiationNotPermittedException();
    }

    /**
     * @return The right {@link MemoryLayout} for a java.lang.long depending on the operating system
     *         this code runs on.
     */
    public static MemoryLayout memoryLayout() {
        return nullToEmpty(OS_NAME.value()).trim().toLowerCase().contains("windows") ? C_LONG_LONG : C_LONG;
    }
}
