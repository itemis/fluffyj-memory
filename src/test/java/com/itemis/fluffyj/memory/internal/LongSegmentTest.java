package com.itemis.fluffyj.memory.internal;

import static com.itemis.fluffyj.memory.internal.LongSegment.DEFAULT_VALUE;
import static com.itemis.fluffyj.tests.FluffyTestHelper.assertNullArgNotAccepted;
import static org.assertj.core.api.Assertions.assertThat;

import com.itemis.fluffyj.memory.api.FluffyScalarSegment;
import com.itemis.fluffyj.memory.tests.MemoryScopedTest;

import org.junit.jupiter.api.Test;

class LongSegmentTest extends MemoryScopedTest {

    @Test
    void constructor_with_null_scope_yields_npe() {
        assertNullArgNotAccepted(() -> new LongSegment(DEFAULT_VALUE, null), "scope");
    }

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
    void a_new_segment_is_alive() {
        var underTest = buildDefault();

        assertThat(underTest.isAlive()).isTrue();
    }

    @Test
    void contained_type_returns_type() {
        Long initialValue = 123L;
        var underTest = new LongSegment(initialValue, scope);

        assertThat(underTest.getContainedType()).isEqualTo(initialValue.getClass());
    }

    private FluffyScalarSegment<Long> buildDefault() {
        return new LongSegment(LongSegment.DEFAULT_VALUE, scope);
    }
}
