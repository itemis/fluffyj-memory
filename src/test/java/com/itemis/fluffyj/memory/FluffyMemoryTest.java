package com.itemis.fluffyj.memory;

import static com.itemis.fluffyj.memory.FluffyMemory.pointer;
import static com.itemis.fluffyj.memory.FluffyMemory.segment;
import static com.itemis.fluffyj.memory.FluffyMemory.wrap;
import static com.itemis.fluffyj.tests.FluffyTestHelper.assertIsStaticHelper;
import static com.itemis.fluffyj.tests.FluffyTestHelper.assertNullArgNotAccepted;
import static jdk.incubator.foreign.MemoryAddress.NULL;
import static jdk.incubator.foreign.MemoryLayouts.JAVA_LONG;
import static jdk.incubator.foreign.ResourceScope.globalScope;
import static jdk.incubator.foreign.ResourceScope.newConfinedScope;
import static org.assertj.core.api.Assertions.assertThat;

import com.itemis.fluffyj.memory.api.FluffyPointer;
import com.itemis.fluffyj.memory.api.FluffySegment;
import com.itemis.fluffyj.memory.internal.LongSegment;
import com.itemis.fluffyj.memory.internal.PointerOfLong;
import com.itemis.fluffyj.memory.tests.MemoryScopedTest;

import org.junit.jupiter.api.Test;

import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemoryLayouts;
import jdk.incubator.foreign.MemorySegment;
import jdk.incubator.foreign.ResourceScope;

class FluffyMemoryTest extends MemoryScopedTest {

    private static long LONG_VAL = 123L;

    @Test
    void is_static_helper() {
        assertIsStaticHelper(FluffyMemory.class);
    }

    @Test
    void allocate_long_segment_success() {
        var result = allocateLongSeg();

        assertThat(result).isInstanceOf(LongSegment.class);
    }

    @Test
    void allocate_long_with_initial_value_success() {
        FluffySegment<Long> result = segment().of(LONG_VAL).allocate(scope);

        assertThat(result.getValue()).isEqualTo(LONG_VAL);
    }

    @Test
    void a_newly_allocated_long_segment_is_alive() {
        FluffySegment<Long> underTest = allocateLongSeg();

        assertThat(underTest.isAlive()).isTrue();
    }

    @Test
    void when_scope_is_closed_long_segment_is_not_alive_anymore() {
        FluffySegment<Long> underTest = segment().ofLong().allocate(scope);

        scope.close();

        assertThat(underTest.isAlive()).isFalse();
    }

    @Test
    void segment_from_has_same_value() {
        var nativeSeg = allocateNativeSeg(LONG_VAL);

        FluffySegment<Long> underTest = wrap(nativeSeg).asLong();

        assertThat(underTest.getValue()).isEqualTo(LONG_VAL);
    }

    @Test
    void if_value_of_wrapped_seg_changes_long_segs_value_changes_too() {
        var nativeSeg = allocateNativeSeg(LONG_VAL);
        FluffySegment<Long> underTest = wrap(nativeSeg).asLong();

        long expectedValue = 456L;
        nativeSeg.asByteBuffer().putLong(expectedValue);

        assertThat(underTest.getValue()).isEqualTo(expectedValue);

    }

    @Test
    void wrapped_seg_has_same_address_as_native_seg() {
        var nativeSeg = allocateNativeSeg(LONG_VAL);
        FluffySegment<Long> underTest = wrap(nativeSeg).asLong();

        assertThat(underTest.address()).isEqualTo(nativeSeg.address());
    }

    @Test
    void wrapped_seg_and_native_seg_share_same_scope() {
        var nativeSeg = allocateNativeSeg(LONG_VAL);
        FluffySegment<Long> underTest = wrap(nativeSeg).asLong();

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
        FluffyPointer<Long> result = allocateNullPointerOfLong();

        assertThat(result.getValue()).isEqualTo(NULL);
    }

    @Test
    void pointer_of_segment_points_to_segment() {
        FluffySegment<Long> longSegment = allocateLongSeg();
        FluffyPointer<Long> underTest = allocatePointer(longSegment);

        assertThat(underTest.getValue()).isEqualTo(longSegment.address());
    }

    @Test
    void pointer_of_address_points_to_address() {
        MemoryAddress expectedAddress = allocateLongSeg().address();
        FluffyPointer<?> underTest = allocatePointer(expectedAddress);

        assertThat(underTest.getValue()).isEqualTo(expectedAddress);
    }

    @Test
    void pointer_with_scope_is_not_alive_when_scope_is_closed() {
        FluffyPointer<Long> underTest = null;

        try (var scope = newConfinedScope()) {
            underTest = allocateNullPointerOfLong(scope);
        }

        assertThat(underTest.isAlive()).isFalse();
    }

    @Test
    void pointer_from_address_to_long_can_be_dereferenced_as_long() {
        var nativeSeg = MemorySegment.allocateNative(MemoryLayouts.JAVA_LONG, scope);
        nativeSeg.asByteBuffer().putLong(LONG_VAL);
        var underTest = pointer().to(nativeSeg.address()).asLong().allocate(scope);

        assertThat(underTest.dereference()).isEqualTo(LONG_VAL);
    }

    @Test
    void wrap_null_yields_npe() {
        assertNullArgNotAccepted(() -> wrap(null), "nativeSeg");
    }

    private MemorySegment allocateNativeSeg(long initialValue) {
        var result = MemorySegment.allocateNative(JAVA_LONG, scope);
        result.asByteBuffer().putLong(LONG_VAL);
        return result;
    }

    private FluffySegment<Long> allocateLongSeg() {
        return segment().ofLong().allocate();
    }

    private FluffyPointer<Long> allocateNullPointerOfLong() {
        return allocateNullPointerOfLong(globalScope());
    }

    private FluffyPointer<Long> allocateNullPointerOfLong(ResourceScope scope) {
        return pointer().to(NULL).asLong().allocate(scope);
    }

    private FluffyPointer<Long> allocatePointer(FluffySegment<Long> toHere) {
        return pointer().to(toHere).allocate();
    }

    private FluffyPointer<?> allocatePointer(MemoryAddress toHere) {
        return pointer().to(toHere).asLong().allocate();
    }

}
