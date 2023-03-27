package com.itemis.fluffyj.memory.internal.impl;

import static com.itemis.fluffyj.tests.FluffyTestHelper.assertNullArgNotAccepted;
import static java.lang.foreign.MemoryLayout.sequenceLayout;
import static java.lang.foreign.ValueLayout.ADDRESS;
import static java.lang.foreign.ValueLayout.JAVA_BYTE;
import static java.lang.foreign.ValueLayout.JAVA_CHAR;
import static java.lang.foreign.ValueLayout.JAVA_DOUBLE;
import static java.lang.foreign.ValueLayout.JAVA_FLOAT;
import static java.lang.foreign.ValueLayout.JAVA_INT;
import static java.lang.foreign.ValueLayout.JAVA_LONG;
import static java.lang.foreign.ValueLayout.JAVA_SHORT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.itemis.fluffyj.memory.error.FluffyMemoryException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.util.stream.Stream;

public class CDataTypeConverterTest {

    private static MemoryLayout UNKNOWN_MEMORY_LAYOUT = sequenceLayout(3, JAVA_INT);

    private CDataTypeConverter underTest;

    @BeforeEach
    void setUp() {
        underTest = new CDataTypeConverter();
    }

    @ParameterizedTest
    @MethodSource("expectedTypeMappings")
    void native_type_to_jvm_type_and_back(Class<?> jvmInputType, MemoryLayout expectedNativeType,
            Class<?> expectedJvmType) {
        var actualNativeType = underTest.getNativeType(jvmInputType);
        assertThat(actualNativeType).isEqualTo(expectedNativeType);
        assertThat(underTest.getJvmType(actualNativeType)).isEqualTo(expectedJvmType);
    }

    @Test
    void JVM_type_of_pointer_is_memory_segment() {
        assertThat(underTest.getJvmType(ADDRESS)).isEqualTo(MemorySegment.class);
    }

    @Test
    void memory_layout_of_unknown_type_yields_exception() {
        var unknownType = Object.class;
        assertThatThrownBy(() -> underTest.getNativeType(unknownType))
            .isInstanceOf(FluffyMemoryException.class)
            .hasMessage("Cannot provide native memory layout for JVM type " + unknownType.getCanonicalName());
    }

    @Test
    void JVM_type_of_unknown_memory_layout_yields_exception() {
        var unknownMemoryLayout = sequenceLayout(3, JAVA_INT);

        assertThatThrownBy(() -> underTest.getJvmType(unknownMemoryLayout))
            .isInstanceOf(FluffyMemoryException.class)
            .hasMessage("Cannot provide JVM type for native memory layout " + unknownMemoryLayout.name());
    }

    @ParameterizedTest
    @MethodSource("expectedTypeMappings")
    void jvmTypes_returns_correct_types(Class<?> unused, MemoryLayout input, Class<?> expectedOutput) {
        assertThat(underTest.getJvmTypes(input)).isEqualTo(new Class<?>[] {expectedOutput});
    }

    @Test
    void jvmTypes_does_not_accept_null_input() {
        assertNullArgNotAccepted(() -> underTest.getJvmTypes((MemoryLayout[]) null), "nativeTypes");
    }

    @Test
    void jvmTypes_returns_empty_array_on_empty_input() {
        assertThat(underTest.getJvmTypes()).isEmpty();
    }

    @Test
    void jvmTypes_throws_exception_on_unknown_memory_layout() {
        assertThatThrownBy(() -> underTest.getJvmTypes(UNKNOWN_MEMORY_LAYOUT))
            .isInstanceOf(FluffyMemoryException.class)
            .hasMessage("Cannot provide JVM type for native memory layout " + UNKNOWN_MEMORY_LAYOUT.name());
    }

    @Test
    void jvmTypes_returns_types_in_expected_order() {
        assertThat(underTest.getJvmTypes(JAVA_INT, JAVA_CHAR, JAVA_BYTE, JAVA_DOUBLE)).containsExactly(
            int.class,
            char.class,
            byte.class, double.class);
    }

    @Test
    void jvmTypes_throws_exception_on_unknown_memory_layout_multiple_inputs_case() {
        assertThatThrownBy(() -> underTest.getJvmTypes(JAVA_INT, UNKNOWN_MEMORY_LAYOUT))
            .isInstanceOf(FluffyMemoryException.class)
            .hasMessage("Cannot provide JVM type for native memory layout " + UNKNOWN_MEMORY_LAYOUT.name());
    }

    @Test
    void jvmType_of_native_char_is_char() {
        assertThat(underTest.getJvmType(JAVA_CHAR)).isEqualTo(char.class);
    }

    @Test
    void nativeTypes_with_no_args_returns_empty_array() {
        assertThat(underTest.getNativeTypes()).isEmpty();
    }

    @Test
    void nativeTypes_with_null_yields_npe() {
        assertNullArgNotAccepted(() -> underTest.getNativeTypes((Class<?>[]) null), "jvmTypes");
    }

    @ParameterizedTest
    @MethodSource("expectedTypeMappings")
    void nativeTypes_with_one_type_yields_correct_type(Class<?> input, MemoryLayout expectedOutput, Class<?> unused) {
        assertThat(underTest.getNativeTypes(input)).hasOnlyOneElementSatisfying(elm -> elm.equals(expectedOutput));
    }

    @Test
    void nativeTypes_returns_converted_types_in_correct_order() {
        assertThat(underTest.getNativeTypes(long.class, int.class, double.class))
            .containsExactly(JAVA_LONG, JAVA_INT, JAVA_DOUBLE);
    }

    @Test
    void nativeTypes_throws_exception_on_unknown_type() {
        var unknownJvmType = String.class;
        assertThatThrownBy(() -> underTest.getNativeTypes(long.class, unknownJvmType, double.class))
            .isInstanceOf(FluffyMemoryException.class)
            .hasMessage("Cannot provide native memory layout for JVM type " + unknownJvmType.getCanonicalName());
    }

    private static Stream<Arguments> expectedTypeMappings() {
        return Stream.of(
            Arguments.of(long.class, JAVA_LONG, long.class),
            Arguments.of(Long.class, JAVA_LONG, long.class),
            Arguments.of(int.class, JAVA_INT, int.class),
            Arguments.of(Integer.class, JAVA_INT, int.class),
            Arguments.of(char.class, JAVA_CHAR, char.class),
            Arguments.of(Character.class, JAVA_CHAR, char.class),
            Arguments.of(double.class, JAVA_DOUBLE, double.class),
            Arguments.of(Double.class, JAVA_DOUBLE, double.class),
            Arguments.of(float.class, JAVA_FLOAT, float.class),
            Arguments.of(Float.class, JAVA_FLOAT, float.class),
            Arguments.of(short.class, JAVA_SHORT, short.class),
            Arguments.of(Short.class, JAVA_SHORT, short.class),
            Arguments.of(byte.class, JAVA_BYTE, byte.class),
            Arguments.of(Byte.class, JAVA_BYTE, byte.class),
            Arguments.of(MemorySegment.class, ADDRESS, MemorySegment.class));
    }
}
