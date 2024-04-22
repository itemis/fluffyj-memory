package com.itemis.fluffyj.memory;

import static com.itemis.fluffyj.tests.FluffyTestHelper.assertNullArgNotAccepted;
import static org.assertj.core.api.Assertions.assertThat;

import com.itemis.fluffyj.memory.internal.PointerOfThing;
import com.itemis.fluffyj.memory.tests.ArenafiedTest;

import org.junit.jupiter.api.Test;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.util.Random;

public class PointerOfThingTest extends ArenafiedTest {

    @Test
    void constructor_does_not_accept_null_arena() {
        assertNullArgNotAccepted(() -> new PointerOfThing(null), "arena");
    }

    @Test
    void is_alive_is_tied_to_arena_livecycle() {
        final var underTest = new PointerOfThing(arena);

        assertThat(underTest.isAlive()).isTrue();
        arena.close();
        assertThat(underTest.isAlive()).isFalse();
    }

    @Test
    void rawAddress_returns_a_value() {
        final var underTest = new PointerOfThing(arena);
        assertThat(underTest.rawAddress()).isInstanceOf(Long.class);
    }

    @Test
    void address_returns_a_value() {
        final var underTest = new PointerOfThing(arena);
        assertThat(underTest.address()).isInstanceOf(MemorySegment.class);
    }

    @Test
    void address_rawAddress_equality() {
        final var underTest = new PointerOfThing(arena);
        assertThat(underTest.address().address()).isEqualTo(underTest.rawAddress());
    }

    @Test
    void getRawValue_returns_a_value_that_is_not_pointers_address() {
        final var underTest = new PointerOfThing(arena);
        final var rawValue = underTest.getRawValue();
        assertThat(rawValue).isInstanceOf(Long.class);
        assertThat(rawValue).isNotSameAs(underTest.rawAddress());
    }

    @Test
    void getValue_returns_a_value_that_is_not_pointers_address() {
        final var underTest = new PointerOfThing(arena);
        final var value = underTest.getValue();
        assertThat(value).isInstanceOf(MemorySegment.class);
        assertThat(value).isNotSameAs(underTest.address());
    }

    @Test
    void getValue_of_a_freshly_instantiated_pointer_returns_zero() {
        final var underTest = new PointerOfThing(arena);
        final var value = underTest.getValue();

        assertThat(value.address()).isEqualTo(0);
    }

    @Test
    void rawDereference_returns_segment_pointed_to() {
        final var underTest = new PointerOfThing(arena);

        final var expectedVal = new Random().nextInt();
        final var nativeSeg = arena.allocateFrom(ValueLayout.JAVA_INT, expectedVal);
        final var underTestAsFfmSeg =
            underTest.address().reinterpret(ValueLayout.JAVA_LONG.byteSize(), arena, null);
        underTestAsFfmSeg.set(ValueLayout.JAVA_LONG, 0, nativeSeg.address());

        final var result = underTest.rawDereference();

        assertThat(result.get(ValueLayout.JAVA_INT, 0)).isEqualTo(expectedVal);
    }
}
