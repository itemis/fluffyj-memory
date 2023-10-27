package com.itemis.fluffyj.memory.tests;

import static com.itemis.fluffyj.memory.FluffyMemory.pointer;
import static com.itemis.fluffyj.memory.FluffyMemory.segment;
import static com.itemis.fluffyj.memory.FluffyMemory.wrap;
import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;

import com.itemis.fluffyj.memory.api.FluffyPointer;
import com.itemis.fluffyj.memory.api.FluffySegment;
import com.itemis.fluffyj.memory.api.FluffyVectorPointer;
import com.itemis.fluffyj.memory.api.FluffyVectorSegment;

import org.junit.jupiter.api.Test;

import java.lang.foreign.Arena;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.util.Iterator;

public abstract class FluffyVectorDataManipulationTest<T> extends ArenafiedTest {

    private final Iterator<FluffyMemoryVectorTestValue<T>> testValueIter;
    private final MemoryLayout segmentLayout;
    private final FluffyMemoryVectorTestValue<T> firstTestValue;
    private final Class<? extends T[]> testValueType;

    protected FluffyVectorDataManipulationTest(final Iterator<FluffyMemoryVectorTestValue<T>> testValueIter,
            final MemoryLayout segmentLayout) {
        this.testValueIter = requireNonNull(testValueIter, "testValueIter");
        this.segmentLayout = requireNonNull(segmentLayout, "segmentLayout");
        this.firstTestValue = testValueIter.next();
        testValueType = firstTestValue.type();
    }

    @Test
    void allocate_segment_success() {
        final var result = allocateSeg();

        assertThat(result).isInstanceOf(FluffySegment.class);
    }

    @Test
    void allocate_with_initial_value_success() {
        final var result = allocateSeg();

        assertThat(result.getValue()).isEqualTo(firstTestValue.typedValue());
    }

    @Test
    void a_newly_allocated_segment_is_alive() {
        final var underTest = allocateSeg();

        assertThat(underTest.isAlive()).isTrue();
    }

    @Test
    void when_arena_is_closed_then_segment_is_not_alive_anymore() {
        final var underTest = allocateArenafiedSeg(arena);

        arena.close();

        assertThat(underTest.isAlive()).isFalse();
    }

    @Test
    void wrapped_segment_has_same_value() {
        final var nativeSeg = allocateNativeSeg(firstTestValue.rawValue());

        final var underTest = wrapNativeSeg(nativeSeg);

        assertThat(underTest.getValue()).isEqualTo(firstTestValue.typedValue());
    }

    @Test
    void if_value_of_wrapped_seg_changes_then_segs_value_changes_too() {
        final var nativeSeg = allocateNativeSeg(firstTestValue.rawValue());
        final var underTest = wrapNativeSeg(nativeSeg);

        final var expectedValue = testValueIter.next();
        assertThat(underTest.getValue()).isNotEqualTo(expectedValue.typedValue());

        nativeSeg.asByteBuffer().clear().put(expectedValue.rawValue());

        assertThat(underTest.getValue()).isEqualTo(expectedValue.typedValue());
    }

    @Test
    void wrapped_seg_has_same_address_as_native_seg() {
        final var nativeSeg = allocateNativeSeg(firstTestValue.rawValue());
        final var underTest = wrapNativeSeg(nativeSeg);

        assertThat(underTest.rawAddress()).isEqualTo(nativeSeg.address());
    }

    @Test
    void wrapped_seg_and_native_seg_share_same_arena() {
        final var nativeSeg = allocateNativeSeg(firstTestValue.rawValue(), arena);
        final var underTest = wrapNativeSeg(nativeSeg);

        assertThat(underTest.isAlive()).isTrue();
        arena.close();
        assertThat(underTest.isAlive()).isFalse();
    }

    @Test
    void allocate_pointer_success() {
        final var result = allocateNullPointer();

        assertThat(result).isInstanceOf(FluffyPointer.class);
    }

    @Test
    void null_pointer_points_to_null() {
        final var result = allocateNullPointer();

        assertThat(result.getRawValue()).isEqualTo(0L);
    }

    @Test
    void pointer_of_segment_points_to_segment() {
        final var nativeSeg = allocateSeg();
        final var underTest = allocatePointer(nativeSeg);

        assertThat(underTest.getRawValue()).isEqualTo(nativeSeg.rawAddress());
    }

    @Test
    void pointer_of_address_points_to_address() {
        final var expectedAddress = allocateSeg().rawAddress();
        final var underTest = allocatePointer(expectedAddress);

        assertThat(underTest.getRawValue()).isEqualTo(expectedAddress);
    }

    @Test
    void pointer_with_arena_is_not_alive_when_arena_is_closed() {
        final var addr = allocateSeg().rawAddress();
        final var underTest = allocateArenafiedPointer(addr, arena);

        arena.close();

        assertThat(underTest.isAlive()).isFalse();
    }

    @Test
    void pointer_from_address_to_type_can_be_dereferenced_as_type() {
        final var nativeSeg = allocateNativeSeg(firstTestValue.rawValue());
        final var underTest = allocatePointer(nativeSeg.address());

        assertThat(underTest.dereference()).isEqualTo(firstTestValue.typedValue());
    }

    @Test
    void rawDereference_returns_seg_of_data_pointed_to() {
        final var nativeSeg = allocateNativeSeg(firstTestValue.rawValue());
        final var underTest = allocatePointer(nativeSeg.address());

        final var dereferencedSeg = underTest.rawDereference();
        assertThat(dereferencedSeg.address()).isEqualTo(nativeSeg.address());
        final var buf = new byte[firstTestValue.length()];
        for (var i = 0; i < buf.length; i++) {
            buf[i] = dereferencedSeg.get(ValueLayout.JAVA_BYTE, i);
        }
        assertThat(buf).isEqualTo(firstTestValue.rawValue());
    }

    protected MemorySegment allocateNativeSeg(final byte[] rawContents) {
        return allocateNativeSeg(rawContents, Arena.ofAuto());
    }

    protected MemorySegment allocateNativeSeg(final byte[] rawContents, final Arena arena) {
        final var result = arena.allocate(segmentLayout);
        result.asByteBuffer().put(rawContents);
        return result;
    }

    protected final FluffyVectorSegment<? extends T> allocateSeg() {
        return segment().ofArray(firstTestValue.typedValue()).allocate();
    }

    protected final FluffyVectorSegment<? extends T> allocateArenafiedSeg(final Arena arena) {
        return segment().ofArray(firstTestValue.typedValue()).allocate(arena);
    }

    protected final FluffyVectorSegment<? extends T> wrapNativeSeg(final MemorySegment nativeSeg) {
        return wrap(nativeSeg).asArray(testValueType);
    }

    protected final FluffyVectorPointer<? extends T> allocatePointer(final long address) {
        return pointer().to(address).asArray(firstTestValue.length()).of(testValueType).allocate();
    }

    protected final FluffyVectorPointer<? extends T> allocateArenafiedPointer(final long address,
            final Arena arena) {
        return pointer().to(address).asArray(firstTestValue.length()).of(testValueType).allocate(arena);
    }

    protected final FluffyVectorPointer<? extends T> allocatePointer(final FluffyVectorSegment<? extends T> seg) {
        return pointer().toArray(seg).allocate();
    }

    protected final FluffyVectorPointer<? extends T> allocateNullPointer() {
        return allocatePointer(MemorySegment.NULL.address());
    }

    protected static abstract class FluffyMemoryVectorTestValueIterator<V>
            implements Iterator<FluffyMemoryVectorTestValue<V>> {
        @Override
        public boolean hasNext() {
            return true;
        }
    }
}
