package com.itemis.fluffyj.memory.tests;

import static com.itemis.fluffyj.memory.FluffyMemory.dereference;
import static com.itemis.fluffyj.memory.FluffyMemory.pointer;
import static com.itemis.fluffyj.memory.FluffyMemory.segment;
import static com.itemis.fluffyj.memory.FluffyMemory.wrap;
import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;

import com.itemis.fluffyj.memory.FluffyMemory;
import com.itemis.fluffyj.memory.api.FluffyPointer;
import com.itemis.fluffyj.memory.api.FluffyScalarPointer;
import com.itemis.fluffyj.memory.api.FluffyScalarSegment;
import com.itemis.fluffyj.memory.api.FluffySegment;

import org.junit.jupiter.api.Test;

import java.lang.foreign.Arena;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.util.Iterator;

public abstract class FluffyScalarDataManipulationTest<T> extends ArenafiedTest {

    private final Iterator<FluffyMemoryScalarTestValue<T>> testValueIter;
    private final MemoryLayout segmentLayout;
    private final FluffyMemoryScalarTestValue<T> firstTestValue;
    private final Class<? extends T> testValueType;

    protected FluffyScalarDataManipulationTest(final Iterator<FluffyMemoryScalarTestValue<T>> testValueIter,
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
        final var nativeSeg = allocateNativeSeg(firstTestValue.rawValue());
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

        assertThat(result.getRawValue()).isEqualTo(MemorySegment.NULL.address());
    }

    @Test
    void pointer_of_segment_points_to_segment() {
        final var longSegment = allocateSeg();
        final var underTest = allocatePointer(longSegment);

        assertThat(underTest.getRawValue()).isEqualTo(longSegment.rawAddress());
    }

    @Test
    void pointer_of_address_points_to_address() {
        final var expectedAddress = allocateSeg().rawAddress();
        final var underTest = allocatePointer(expectedAddress);

        assertThat(underTest.getRawValue()).isEqualTo(expectedAddress);
    }

    @Test
    void pointer_with_arena_is_not_alive_anymore_when_arena_is_closed() {
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
    void can_create_empty_typed_pointer() {
        final var result = pointer().of(testValueType).allocate();

        assertThat(result).isInstanceOf(FluffyScalarPointer.class);
    }

    @Test
    void can_create_empty_typed_pointer_tied_to_a_arena() {
        final var result = pointer().of(testValueType).allocate(arena);

        assertThat(result.isAlive()).isTrue();
        arena.close();
        assertThat(result.isAlive()).isFalse();
    }

    @Test
    void rawDereference_returns_seg_of_data_pointed_to() {
        final var nativeSeg = allocateNativeSeg(firstTestValue.rawValue());
        final var underTest = allocatePointer(nativeSeg.address());

        final var dereferencedSeg = underTest.rawDereference();
        assertThat(dereferencedSeg.address()).isEqualTo(nativeSeg.address());
        final var buf = new byte[firstTestValue.rawValue().length];
        for (var i = 0; i < buf.length; i++) {
            buf[i] = dereferencedSeg.get(ValueLayout.JAVA_BYTE, i);
        }
        assertThat(buf).isEqualTo(firstTestValue.rawValue());
    }

    @Test
    void wrapped_pointer_works() {
        final var nativeSeg = allocateNativeSeg(firstTestValue.rawValue());
        final var result =
            wrap(MemorySegment.ofAddress(nativeSeg.address())).asPointerOf(testValueType).allocate(arena);
        assertThat(result).isNotNull();
        assertThat(result.getRawValue()).isEqualTo(nativeSeg.address());
    }

    @Test
    void native_dereference_shortcut_works() {
        final var nativeSeg = allocateNativeSeg(firstTestValue.rawValue());
        final var ptrToNativeSeg = FluffyMemory.pointer().to(nativeSeg.address()).as(testValueType).allocate(arena);
        final var result = dereference(ptrToNativeSeg.getValue()).as(testValueType);
        assertThat(result).isEqualTo(firstTestValue.typedValue());
    }

    @Test
    void address_dereference_shortcut_works() {
        final var nativeSeg = allocateNativeSeg(firstTestValue.rawValue());
        final var nativePtr = FluffyMemory.pointer().to(nativeSeg.address()).as(testValueType).allocate(arena);
        final var result = dereference(nativePtr.getRawValue()).as(testValueType);
        assertThat(result).isEqualTo(firstTestValue.typedValue());
    }

    protected final MemorySegment allocateNativeSeg(final byte[] rawContents) {
        final var result = arena.allocate(segmentLayout);
        result.asByteBuffer().put(rawContents);
        return result;
    }

    protected final FluffyScalarSegment<? extends T> allocateSeg() {
        return segment().of(firstTestValue.typedValue()).allocate();
    }

    protected final FluffyScalarSegment<? extends T> allocateArenafiedSeg(final Arena arena) {
        return segment().of(firstTestValue.typedValue()).allocate(arena);
    }

    protected final FluffyScalarSegment<? extends T> wrapNativeSeg(final MemorySegment nativeSeg) {
        return wrap(nativeSeg).as(testValueType);
    }

    protected final FluffyScalarPointer<? extends T> allocatePointer(final long address) {
        return pointer().to(address).as(testValueType).allocate();
    }

    protected final FluffyScalarPointer<? extends T> allocateArenafiedPointer(final long address,
            final Arena arena) {
        return pointer().to(address).as(testValueType).allocate(arena);
    }

    protected final FluffyScalarPointer<? extends T> allocatePointer(final FluffyScalarSegment<? extends T> seg) {
        return pointer().to(seg).allocate();
    }

    protected final FluffyScalarPointer<? extends T> allocateNullPointer() {
        return allocatePointer(MemorySegment.NULL.address());
    }

    protected static abstract class FluffyMemoryScalarTestValueIterator<V>
            implements Iterator<FluffyMemoryScalarTestValue<V>> {
        @Override
        public boolean hasNext() {
            return true;
        }
    }
}
