package com.itemis.fluffyj.memory.internal.impl;

import static com.google.common.base.StandardSystemProperty.OS_NAME;
import static com.itemis.fluffyj.tests.FluffyTestHelper.assertIsStaticHelper;
import static jdk.incubator.foreign.CLinker.C_LONG;
import static jdk.incubator.foreign.CLinker.C_LONG_LONG;
import static org.assertj.core.api.Assertions.assertThat;

import com.itemis.fluffyj.tests.FluffyTestSystemProperties;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public class OsDependentLongTest {

    @RegisterExtension
    FluffyTestSystemProperties fluffyProps = new FluffyTestSystemProperties();

    @Test
    void is_static_helper() {
        assertIsStaticHelper(OsDependentLong.class);
    }

    @Test
    void c_memory_layout_is_long_long_on_windows() {
        System.setProperty(OS_NAME.key(), "Windows 10");

        assertThat(OsDependentLong.memoryLayout()).isSameAs(C_LONG_LONG);
    }

    @Test
    void c_memory_layout_is_long_on_linux() {
        System.setProperty(OS_NAME.key(), "Linux");

        assertThat(OsDependentLong.memoryLayout()).isSameAs(C_LONG);
    }

    @Test
    void c_memory_layout_is_long_on_mac() {
        System.setProperty(OS_NAME.key(), "Mac OS X");

        assertThat(OsDependentLong.memoryLayout()).isSameAs(C_LONG);
    }

    @Test
    void c_memory_layout_works_with_null_os_name() {
        System.clearProperty(OS_NAME.key());

        assertThat(OsDependentLong.memoryLayout()).isNotNull();
    }
}
