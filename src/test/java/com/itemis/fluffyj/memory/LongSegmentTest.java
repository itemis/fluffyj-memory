package com.itemis.fluffyj.memory;

import static com.itemis.fluffyj.memory.LongSegment.DEFAULT_VALUE;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class LongSegmentTest {

    @Test
    void getValue_returns_a_value() {
        var underTest = buildDefault();

        assertThat(underTest.getValue()).isInstanceOf(Long.class);
    }

    @Test
    void defaultValue_isSet() {
        var underTest = buildDefault();

        assertThat(underTest.getValue()).isEqualTo(DEFAULT_VALUE);
    }

    @Test
    void constructor_sets_initial_value_correctly() {
        long expectedValue = 123L;
        var underTest = new LongSegment(expectedValue);

        assertThat(underTest.getValue()).isEqualTo(expectedValue);
    }

    @Test
    void a_new_segment_is_alive() {
        var underTest = buildDefault();

        assertThat(underTest.isAlive()).isTrue();
    }

    private LongSegment buildDefault() {
        return new LongSegment();
    }
}
