package com.itemis.fluffyj.memory;

import static com.itemis.fluffyj.tests.FluffyTestHelper.assertNullArgNotAccepted;
import static org.assertj.core.api.Assertions.assertThat;

import com.itemis.fluffyj.memory.internal.PointerOfThing;
import com.itemis.fluffyj.memory.tests.MemorySessionEnabledTest;

import org.junit.jupiter.api.Test;

import java.lang.foreign.MemoryAddress;

public class PointerOfThingTest extends MemorySessionEnabledTest {

    @Test
    void constructor_does_not_accept_null_session() {
        assertNullArgNotAccepted(() -> new PointerOfThing(null), "session");
    }

    @Test
    void is_alive_is_tied_to_session() {
        var underTest = new PointerOfThing(session);

        assertThat(underTest.isAlive()).isTrue();
        session.close();
        assertThat(underTest.isAlive()).isFalse();
    }

    @Test
    void address_returns_a_value() {
        var underTest = new PointerOfThing(session);
        assertThat(underTest.address()).isInstanceOf(MemoryAddress.class);
    }

    @Test
    void getValue_returns_a_value_that_is_not_pointers_address() {
        var underTest = new PointerOfThing(session);
        var value = underTest.getValue();
        assertThat(value).isInstanceOf(MemoryAddress.class);
        assertThat(value).isNotSameAs(underTest.address());
    }
}
