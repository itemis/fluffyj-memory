package com.itemis.fluffyj.memory.internal;

import static com.itemis.fluffyj.tests.FluffyTestHelper.assertNullArgNotAccepted;
import static org.assertj.core.api.Assertions.assertThat;

import com.itemis.fluffyj.memory.api.FluffySegment;
import com.itemis.fluffyj.memory.tests.MemoryScopedTest;

import org.junit.jupiter.api.Test;

class BlobSegmentTest extends MemoryScopedTest {

    private static final byte[] BYTE_ARRAY = new byte[] {1, 2, 3};

    @Test
    void constructor_with_null_scope_yields_npe() {
        assertNullArgNotAccepted(() -> new BlobSegment(BYTE_ARRAY, null), "scope");
    }

    @Test
    void getValue_returns_a_value() {
        var underTest = buildDefault();

        assertThat(underTest.getValue()).isInstanceOf(byte[].class);
    }

    @Test
    void initialValue_isSet() {
        var underTest = buildDefault();

        assertThat(underTest.getValue()).isEqualTo(BYTE_ARRAY);
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

    private FluffySegment<byte[]> buildDefault() {
        return new BlobSegment(BYTE_ARRAY, scope);
    }
}
