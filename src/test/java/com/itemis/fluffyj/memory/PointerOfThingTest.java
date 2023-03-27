package com.itemis.fluffyj.memory;

import static com.itemis.fluffyj.tests.FluffyTestHelper.assertNullArgNotAccepted;
import static org.assertj.core.api.Assertions.assertThat;

import com.itemis.fluffyj.memory.internal.PointerOfThing;
import com.itemis.fluffyj.memory.tests.MemoryScopeEnabledTest;

import org.junit.jupiter.api.Test;

import java.lang.foreign.MemorySegment;

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
    void address_returns_a_value() {
        var underTest = new PointerOfThing(scope);
        assertThat(underTest.address()).isInstanceOf(Long.class);
    }

    @Test
    void addressAsSeg_returns_a_value() {
        var underTest = new PointerOfThing(scope);
        assertThat(underTest.addressAsSeg()).isInstanceOf(MemorySegment.class);
    }

    @Test
    void getValue_returns_a_value_that_is_not_pointers_address() {
        var underTest = new PointerOfThing(scope);
        var value = underTest.getValue();
        assertThat(value).isInstanceOf(Long.class);
        assertThat(value).isNotSameAs(underTest.address());
    }

    @Test
    void getValueAsSeg_returns_a_value_that_is_not_pointers_address() {
        var underTest = new PointerOfThing(scope);
        var value = underTest.getValueAsSeg();
        assertThat(value).isInstanceOf(MemorySegment.class);
        assertThat(value.address()).isNotSameAs(underTest.address());
    }
}
