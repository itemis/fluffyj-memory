package com.itemis.fluffyj.memory.internal.impl;

import static com.google.common.base.StandardSystemProperty.OS_NAME;
import static jdk.incubator.foreign.CLinker.C_LONG;
import static jdk.incubator.foreign.CLinker.C_LONG_LONG;
import static org.assertj.core.api.Assertions.assertThat;

import com.itemis.fluffyj.tests.FluffyTestSystemProperties;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public class OsDependentCDataTypeConverterTest {

    @RegisterExtension
    FluffyTestSystemProperties fluffyProps = new FluffyTestSystemProperties();

    private CDataTypeConverter underTest;

    @BeforeEach
    void setUp() {
        underTest = new CDataTypeConverter();
    }

    @Test
    void c_type_to_java_type_windows() {
        System.setProperty(OS_NAME.key(), "Windows 10");

        assertThat(underTest.getNativeType(long.class)).isEqualTo(C_LONG_LONG);
        assertThat(underTest.getNativeType(Long.class)).isEqualTo(C_LONG_LONG);
    }

    @Test
    void c_type_to_java_type_linux() {
        System.setProperty(OS_NAME.key(), "Linux");

        assertThat(underTest.getNativeType(long.class)).isEqualTo(C_LONG);
        assertThat(underTest.getNativeType(Long.class)).isEqualTo(C_LONG);
    }

    @Test
    void c_type_to_java_type_mac() {
        System.setProperty(OS_NAME.key(), "Mac OS X");

        assertThat(underTest.getNativeType(long.class)).isEqualTo(C_LONG);
        assertThat(underTest.getNativeType(Long.class)).isEqualTo(C_LONG);
    }
}
