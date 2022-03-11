package com.itemis.fluffyj.memory;

import static org.assertj.core.api.Assertions.assertThat;

import com.itemis.fluffyj.memory.tests.MemoryScopedTest;

import org.junit.jupiter.api.Test;

import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemoryLayouts;

class PointerOfLongTest extends MemoryScopedTest {

    @Test
    void getValue_returns_a_value() {
        var underTest = buildDefault();

        assertThat(underTest.getValue()).isInstanceOf(MemoryAddress.class);
    }

    @Test
    void freshly_allocated_pointer_has_address() {
        var result = buildDefault();

        assertThat(result.address()).isInstanceOf(MemoryAddress.class);
    }

    @Test
    void two_pointers_do_have_different_addresses() {
        var firstPointer = buildDefault();
        var secondPointer = buildDefault();

        assertThat(firstPointer.address().toRawLongValue()).isNotEqualTo(secondPointer.address().toRawLongValue());
    }

    @Test
    void address_of_a_pointer_can_be_used_to_access_the_pointers_value() {
        var longSegment = buildSegment(LongSegment.DEFAULT_VALUE);

        var pointer = new PointerOfLong(longSegment.address(), scope);
        var nativeSeg = pointer.address().asSegment(MemoryLayouts.JAVA_LONG.byteSize(), scope);
        assertThat(pointer.getValue().toRawLongValue()).isEqualTo(nativeSeg.asByteBuffer().getLong());
    }

    @Test
    void dereferentiation_yields_value_the_pointer_points_to() {
        var expectedValue = 123L;
        var longSegment = buildSegment(expectedValue);
        var pointer = new PointerOfLong(longSegment.address(), scope);

        assertThat(pointer.dereference()).isEqualTo(expectedValue);
    }

    @Test
    void a_new_pointer_is_alive() {
        var underTest = buildDefault();
        assertThat(underTest.isAlive()).isTrue();
    }

    @Test
    void when_closing_scope_pointer_is_not_alive_anymore() {
        PointerOfLong underTest = null;

        underTest = buildDefault();
        tearDownScope();
        assertThat(underTest.isAlive()).isFalse();
    }

    private LongSegment buildSegment(long initialVal) {
        return new LongSegment(initialVal, scope);
    }

    private PointerOfLong buildDefault() {
        return new PointerOfLong(MemoryAddress.NULL, scope);
    }
}
