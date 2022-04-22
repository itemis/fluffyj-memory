package com.itemis.fluffyj.memory.internal;

import static com.itemis.fluffyj.memory.internal.LongSegment.DEFAULT_VALUE;
import static com.itemis.fluffyj.tests.FluffyTestHelper.assertNullArgNotAccepted;
import static org.assertj.core.api.Assertions.assertThat;

import com.itemis.fluffyj.memory.api.FluffySegment;
import com.itemis.fluffyj.memory.tests.MemoryScopedTest;

import org.junit.jupiter.api.Test;

class LongSegmentTest extends MemoryScopedTest {

    @Test
    void constructor_with_null_scope_yields_npe() {
        assertNullArgNotAccepted(() -> new LongSegment(DEFAULT_VALUE, null), "scope");
    }

    @Test
    void getValue_returns_a_value() {
        FluffySegment<Long> underTest = buildDefault();

        assertThat(underTest.getValue()).isInstanceOf(Long.class);
    }

    @Test
    void defaultValue_isSet() {
        FluffySegment<Long> underTest = buildDefault();

        assertThat(underTest.getValue()).isEqualTo(DEFAULT_VALUE);
    }

    @Test
    void constructor_sets_initial_value_correctly() {
        long expectedValue = 123L;
        FluffySegment<Long> underTest = new LongSegment(expectedValue, scope);

        assertThat(underTest.getValue()).isEqualTo(expectedValue);
    }

    @Test
    void a_new_segment_is_alive() {
        FluffySegment<Long> underTest = buildDefault();

        assertThat(underTest.isAlive()).isTrue();
    }

    private FluffySegment<Long> buildDefault() {
        return new LongSegment(LongSegment.DEFAULT_VALUE, scope);
    }
}
