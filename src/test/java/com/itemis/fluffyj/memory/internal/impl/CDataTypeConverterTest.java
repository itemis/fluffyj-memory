package com.itemis.fluffyj.memory.internal.impl;

import static com.itemis.fluffyj.tests.FluffyTestHelper.assertNullArgNotAccepted;
import static jdk.incubator.foreign.CLinker.C_CHAR;
import static jdk.incubator.foreign.CLinker.C_DOUBLE;
import static jdk.incubator.foreign.CLinker.C_FLOAT;
import static jdk.incubator.foreign.CLinker.C_INT;
import static jdk.incubator.foreign.CLinker.C_POINTER;
import static jdk.incubator.foreign.CLinker.C_SHORT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.itemis.fluffyj.memory.error.FluffyMemoryException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemoryLayout;
import jdk.incubator.foreign.MemoryLayouts;

public class CDataTypeConverterTest {

    private CDataTypeConverter underTest;

    @BeforeEach
    void setUp() {
        underTest = new CDataTypeConverter();
    }

    @ParameterizedTest
    @MethodSource("expectedTypeMappings")
    void native_type_to_java_type_and_back(Class<?> javaInputType, MemoryLayout expectedCType, Class<?> expectedJavaType) {
        var actualCType = underTest.getNativeType(javaInputType);
        assertThat(actualCType).isEqualTo(expectedCType);
        assertThat(underTest.getJavaType(actualCType)).isEqualTo(expectedJavaType);
    }

    @Test
    void java_type_of_pointer_is_memory_address() {
        assertThat(underTest.getJavaType(C_POINTER)).isEqualTo(MemoryAddress.class);
    }

    @Test
    void memory_layout_of_unknown_type_yields_exception() {
        var unknownType = Object.class;
        assertThatThrownBy(() -> underTest.getNativeType(unknownType))
            .isInstanceOf(FluffyMemoryException.class)
            .hasMessage("Cannot provide C memory layout for type " + unknownType.getCanonicalName());
    }

    @Test
    void java_type_of_unknown_memory_layout_yields_exception() {
        var unknownMemoryLayout = MemoryLayouts.PAD_16;
        assertThatThrownBy(() -> underTest.getJavaType(unknownMemoryLayout))
            .isInstanceOf(FluffyMemoryException.class)
            .hasMessage("Cannot provide Java type for C memory layout " + unknownMemoryLayout.name());
    }

    @ParameterizedTest
    @MethodSource("expectedTypeMappings")
    void javaTypes_returns_correct_types(Class<?> unused, MemoryLayout input, Class<?> expectedOutput) {
        assertThat(underTest.getJavaTypes(input)).isEqualTo(new Class<?>[] {expectedOutput});
    }

    @Test
    void javaTypes_does_not_accept_null_input() {
        assertNullArgNotAccepted(() -> underTest.getJavaTypes((MemoryLayout[]) null), "cTypes");
    }

    @Test
    void javaTypes_returns_empty_array_on_empty_input() {
        assertThat(underTest.getJavaTypes()).isEmpty();
    }

    @Test
    void javaTypes_throws_exception_on_unknown_memory_layout() {
        var unknownMemoryLayout = MemoryLayouts.PAD_16;
        assertThatThrownBy(() -> underTest.getJavaTypes(unknownMemoryLayout))
            .isInstanceOf(FluffyMemoryException.class)
            .hasMessage("Cannot provide Java type for C memory layout " + unknownMemoryLayout.name());
    }

    @Test
    void javaTypes_returns_types_in_expected_order() {
        assertThat(underTest.getJavaTypes(C_INT, C_CHAR, C_DOUBLE)).containsExactly(int.class, byte.class, double.class);
    }

    @Test
    void javaTypes_throws_exception_on_unknown_memory_layout_multiple_inputs_case() {
        var unknownMemoryLayout = MemoryLayouts.PAD_16;
        assertThatThrownBy(() -> underTest.getJavaTypes(C_INT, unknownMemoryLayout))
            .isInstanceOf(FluffyMemoryException.class)
            .hasMessage("Cannot provide Java type for C memory layout " + unknownMemoryLayout.name());
    }

    // C's char is usually 1 byte in size. Java's char/Character is 2 bytes which usually is a
    // C_SHORT
    @Test
    void javaType_of_c_char_is_byte() {
        assertThat(underTest.getJavaType(C_CHAR)).isEqualTo(byte.class);
    }

    @Test
    void nativeTypes_with_no_args_returns_empty_array() {
        assertThat(underTest.getNativeTypes()).isEmpty();
    }

    @Test
    void nativeTypes_with_null_yields_npe() {
        assertNullArgNotAccepted(() -> underTest.getNativeTypes((Class<?>[]) null), "javaTypes");
    }

    @ParameterizedTest
    @MethodSource("expectedTypeMappings")
    void nativeTypes_with_one_type_yields_correct_type(Class<?> input, MemoryLayout expectedOutput, Class<?> unused) {
        assertThat(underTest.getNativeTypes(input)).hasOnlyOneElementSatisfying(elm -> elm.equals(expectedOutput));
    }

    @Test
    void nativeTypes_returns_converted_types_in_correct_order() {
        assertThat(underTest.getNativeTypes(long.class, int.class, double.class)).containsExactly(OsDependentLong.memoryLayout(), C_INT, C_DOUBLE);
    }

    @Test
    void nativeTypes_throws_exception_on_unknown_type() {
        var unknownJavaType = String.class;
        assertThatThrownBy(() -> underTest.getNativeTypes(long.class, unknownJavaType, double.class))
            .isInstanceOf(FluffyMemoryException.class)
            .hasMessage("Cannot provide C memory layout for type " + unknownJavaType.getCanonicalName());
    }

    private static Stream<Arguments> expectedTypeMappings() {
        return Stream.of(
            Arguments.of(long.class, OsDependentLong.memoryLayout(), long.class),
            Arguments.of(Long.class, OsDependentLong.memoryLayout(), long.class),
            Arguments.of(int.class, C_INT, int.class),
            Arguments.of(Integer.class, C_INT, int.class),
            Arguments.of(char.class, C_SHORT, short.class),
            Arguments.of(Character.class, C_SHORT, short.class),
            Arguments.of(double.class, C_DOUBLE, double.class),
            Arguments.of(Double.class, C_DOUBLE, double.class),
            Arguments.of(float.class, C_FLOAT, float.class),
            Arguments.of(Float.class, C_FLOAT, float.class),
            Arguments.of(short.class, C_SHORT, short.class),
            Arguments.of(Short.class, C_SHORT, short.class),
            Arguments.of(byte.class, C_CHAR, byte.class),
            Arguments.of(Byte.class, C_CHAR, byte.class),
            Arguments.of(MemoryAddress.class, C_POINTER, MemoryAddress.class));
    }
}
