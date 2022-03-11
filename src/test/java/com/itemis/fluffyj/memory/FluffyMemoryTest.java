package com.itemis.fluffyj.memory;

import static com.itemis.fluffyj.memory.FluffyMemory.pointer;
import static com.itemis.fluffyj.memory.FluffyMemory.segment;
import static jdk.incubator.foreign.MemoryAddress.NULL;
import static jdk.incubator.foreign.MemoryLayouts.JAVA_LONG;
import static jdk.incubator.foreign.ResourceScope.globalScope;
import static jdk.incubator.foreign.ResourceScope.newConfinedScope;
import static org.assertj.core.api.Assertions.assertThat;

import com.itemis.fluffyj.memory.tests.MemoryScopedTest;

import org.junit.jupiter.api.Test;

import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;
import jdk.incubator.foreign.ResourceScope;

class FluffyMemoryTest extends MemoryScopedTest {

    private static long LONG_VAL = 123L;

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

        var underTest = segment().wrap(nativeSeg).asLong();

        assertThat(underTest.getValue()).isEqualTo(LONG_VAL);
    }

    @Test
    void if_value_of_wrapped_seg_changes_long_segs_value_changes_too() {
        var nativeSeg = allocateNativeSeg(LONG_VAL);
        var underTest = segment().wrap(nativeSeg).asLong();

        long expectedValue = 456L;
        nativeSeg.asByteBuffer().putLong(expectedValue);

        assertThat(underTest.getValue()).isEqualTo(expectedValue);

    }

    @Test
    void wrapped_seg_has_same_address_as_native_seg() {
        var nativeSeg = allocateNativeSeg(LONG_VAL);
        var underTest = segment().wrap(nativeSeg).asLong();

        assertThat(underTest.address()).isEqualTo(nativeSeg.address());
    }

    @Test
    void wrapped_seg_and_native_seg_share_same_scope() {
        var nativeSeg = allocateNativeSeg(LONG_VAL);
        var underTest = segment().wrap(nativeSeg).asLong();

        assertThat(underTest.isAlive()).isTrue();
        nativeSeg.scope().close();
        assertThat(underTest.isAlive()).isFalse();
    }

    @Test
    void allocate_pointer_of_long_success() {
        var result = allocateNullPointerOfLong();

        assertThat(result).isInstanceOf(PointerOfLong.class);
    }

    @Test
    void freshly_allocated_pointer_of_long_points_to_null() {
        var result = allocateNullPointerOfLong();

        assertThat(result.getValue()).isEqualTo(NULL);
    }

    @Test
    void pointer_of_segment_points_to_segment() {
        var longSegment = allocateLongSeg();
        var underTest = allocatePointer(longSegment);

        assertThat(underTest.getValue()).isEqualTo(longSegment.address());
    }

    @Test
    void pointer_of_address_points_to_address() {
        MemoryAddress expectedAddress = allocateLongSeg().address();
        var underTest = allocatePointer(expectedAddress);

        assertThat(underTest.getValue()).isEqualTo(expectedAddress);
    }

    @Test
    void pointer_with_scope_is_not_alive_when_scope_is_closed() {
        PointerOfLong underTest = null;

        try (var scope = newConfinedScope()) {
            underTest = allocateNullPointerOfLong(scope);
        }

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

    private PointerOfLong allocateNullPointerOfLong() {
        return allocateNullPointerOfLong(globalScope());
    }

    private PointerOfLong allocateNullPointerOfLong(ResourceScope scope) {
        return pointer().ofLong().allocate(scope);
    }

    private PointerOfLong allocatePointer(LongSegment toHere) {
        return pointer().ofLong(toHere).allocate();
    }

    private PointerOfLong allocatePointer(MemoryAddress toHere) {
        return pointer().ofLong(toHere).allocate();
    }

}
