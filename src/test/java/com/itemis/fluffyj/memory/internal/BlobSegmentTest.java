package com.itemis.fluffyj.memory.internal;

import static org.assertj.core.api.Assertions.assertThat;

import com.itemis.fluffyj.memory.tests.MemoryScopedTest;

import org.junit.jupiter.api.Test;

class BlobSegmentTest extends MemoryScopedTest {

    @Test
    void contained_type_returns_type() {
        var initialValue = new byte[1];
        var underTest = new BlobSegment(initialValue, scope);

        assertThat(underTest.getContainedType()).isEqualTo(initialValue.getClass());
    }
}
