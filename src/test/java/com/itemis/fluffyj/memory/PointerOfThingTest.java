package com.itemis.fluffyj.memory;

import static com.itemis.fluffyj.tests.FluffyTestHelper.assertNullArgNotAccepted;
import static java.lang.foreign.ValueLayout.ADDRESS;
import static org.assertj.core.api.Assertions.assertThat;

import com.itemis.fluffyj.memory.internal.PointerOfThing;
import com.itemis.fluffyj.memory.tests.MemoryScopeEnabledTest;

import org.junit.jupiter.api.Test;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;

public class PointerOfThingTest extends MemoryScopeEnabledTest {

    @Test
    void constructor_does_not_accept_null_scope() {
        assertNullArgNotAccepted(() -> new PointerOfThing(null), "scope");
    }

    @Test
    void is_alive_is_tied_to_scope() {
        var underTest = new PointerOfThing(scope);

        assertThat(underTest.isAlive()).isTrue();
        arena.close();
        assertThat(underTest.isAlive()).isFalse();
    }

    @Test
    void rawAddress_returns_a_value() {
        var underTest = new PointerOfThing(scope);
        assertThat(underTest.rawAddress()).isInstanceOf(Long.class);
    }

    @Test
    void address_returns_a_value() {
        var underTest = new PointerOfThing(scope);
        assertThat(underTest.address()).isInstanceOf(MemorySegment.class);
    }

    @Test
    void address_rawAddress_equality() {
        var underTest = new PointerOfThing(scope);
        assertThat(underTest.address().address()).isEqualTo(underTest.rawAddress());
    }

    @Test
    void getRawValue_returns_a_value_that_is_not_pointers_address() {
        var underTest = new PointerOfThing(scope);
        var rawValue = underTest.getRawValue();
        assertThat(rawValue).isInstanceOf(Long.class);
        assertThat(rawValue).isNotSameAs(underTest.rawAddress());
    }

    @Test
    void getValue_returns_a_value_that_is_not_pointers_address() {
        var underTest = new PointerOfThing(scope);
        var value = underTest.getValue();
        assertThat(value).isInstanceOf(MemorySegment.class);
        assertThat(value).isNotSameAs(underTest.address());
    }

    @Test
    void getValue_of_a_freshly_instantiated_pointer_returns_zero() {
        var underTest = new PointerOfThing(scope);
        var value = underTest.getValue();

        assertThat(value.address()).isEqualTo(0);
    }

    @Test
    void rawDereference_returns_segment() {
        var underTest = new PointerOfThing(scope);

        var nativeSeg = MemorySegment.allocateNative(ValueLayout.JAVA_INT.byteSize(), scope);
        var nativePtrSeg = MemorySegment.ofAddress(underTest.rawAddress(), ADDRESS.byteSize(), scope);
        nativePtrSeg.set(ADDRESS, 0, nativeSeg);

        var result = underTest.rawDereference();

        assertThat(result.get(ADDRESS, 0).address()).isEqualTo(nativeSeg.address());
    }
}
