package com.itemis.fluffyj.memory;

import static com.itemis.fluffyj.exceptions.ThrowablePrettyfier.pretty;
import static com.itemis.fluffyj.memory.FluffyMemory.segment;
import static jdk.incubator.foreign.MemoryLayouts.JAVA_LONG;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jdk.incubator.foreign.MemorySegment;
import jdk.incubator.foreign.ResourceScope;

class FluffyMemoryTest {

    private static long LONG_VAL = 123L;

    private ResourceScope scope;

    @BeforeEach
    void setUp() {
        scope = ResourceScope.newConfinedScope();
    }

    @AfterEach
    void tearDown() {
        if (scope != null && scope.isAlive()) {
            try {
                scope.close();
            } catch (Exception e) {
                System.err.println("WARN: Could not close resource scope: " + pretty(e));
            }
        }
    }

    @Test
    void allocate_long_segment_success() {
        var result = allocateLongSeg();

        assertThat(result).isInstanceOf(LongSegment.class);
    }

    @Test
    void allocate_long_with_initial_value_success() {
        var result = segment().ofLong(LONG_VAL).allocate(scope);

        assertThat(result.getValue()).isEqualTo(LONG_VAL);
    }

    @Test
    void a_newly_allocated_long_segment_is_alive() {
        var underTest = allocateLongSeg();

        assertThat(underTest.isAlive()).isTrue();
    }

    @Test
    void when_scope_is_closed_long_segment_is_not_alive_anymore() {
        var underTest = segment().ofLong().allocate(scope);

        scope.close();

        assertThat(underTest.isAlive()).isFalse();
    }

    @Test
    void segment_from_has_same_value() {
        var nativeSeg = allocateNativeSeg(LONG_VAL);

        var underTest = segment().ofLong().from(nativeSeg);

        assertThat(underTest.getValue()).isEqualTo(LONG_VAL);
    }

    @Test
    void if_value_of_wrapped_seg_changes_long_segs_value_changes_too() {
        var nativeSeg = allocateNativeSeg(LONG_VAL);
        var underTest = segment().ofLong().from(nativeSeg);

        long expectedValue = 456L;
        nativeSeg.asByteBuffer().putLong(expectedValue);

        assertThat(underTest.getValue()).isEqualTo(expectedValue);

    }

    @Test
    void wrapped_seg_has_same_address_as_native_seg() {
        var nativeSeg = allocateNativeSeg(LONG_VAL);
        var underTest = segment().ofLong().from(nativeSeg);

        assertThat(underTest.address()).isEqualTo(nativeSeg.address());
    }

    @Test
    void wrapped_seg_and_native_seg_share_same_scope() {
        var nativeSeg = allocateNativeSeg(LONG_VAL);
        var underTest = segment().ofLong().from(nativeSeg);

        assertThat(underTest.isAlive()).isTrue();
        nativeSeg.scope().close();
        assertThat(underTest.isAlive()).isFalse();
    }

    private MemorySegment allocateNativeSeg(long initialValue) {
        var result = MemorySegment.allocateNative(JAVA_LONG, scope);
        result.asByteBuffer().putLong(LONG_VAL);
        return result;
    }

    private LongSegment allocateLongSeg() {
        return segment().ofLong().allocate();
    }


}
