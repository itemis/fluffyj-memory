package com.itemis.fluffyj.memory.tests;

import static com.itemis.fluffyj.memory.FluffyMemory.pointer;
import static com.itemis.fluffyj.memory.FluffyMemory.segment;
import static com.itemis.fluffyj.memory.FluffyMemory.wrap;
import static java.lang.foreign.MemorySegment.allocateNative;
import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;

import com.itemis.fluffyj.memory.api.FluffyPointer;
import com.itemis.fluffyj.memory.api.FluffyScalarPointer;
import com.itemis.fluffyj.memory.api.FluffyScalarSegment;
import com.itemis.fluffyj.memory.api.FluffySegment;

import org.junit.jupiter.api.Test;

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentScope;
import java.lang.foreign.ValueLayout;
import java.util.Iterator;

public abstract class FluffyScalarDataManipulationTest<T> extends MemoryScopeEnabledTest {

    private final Iterator<FluffyMemoryScalarTestValue<T>> testValueIter;
    private final MemoryLayout segmentLayout;
    private final FluffyMemoryScalarTestValue<T> firstTestValue;
    private final Class<? extends T> testValueType;

    protected FluffyScalarDataManipulationTest(Iterator<FluffyMemoryScalarTestValue<T>> testValueIter,
            MemoryLayout segmentLayout) {
        this.testValueIter = requireNonNull(testValueIter, "testValueIter");
        this.segmentLayout = requireNonNull(segmentLayout, "segmentLayout");
        this.firstTestValue = testValueIter.next();
        testValueType = firstTestValue.type();
    }

    @Test
    void allocate_segment_success() {
        var result = allocateSeg();

        assertThat(result).isInstanceOf(FluffySegment.class);
    }

    @Test
    void allocate_with_initial_value_success() {
        var result = allocateSeg();

        assertThat(result.getValue()).isEqualTo(firstTestValue.typedValue());
    }

    @Test
    void a_newly_allocated_segment_is_alive() {
        var underTest = allocateSeg();

        assertThat(underTest.isAlive()).isTrue();
    }

    @Test
    void when_scope_is_closed_then_segment_is_not_alive_anymore() {
        var underTest = allocateScopedSeg(scope);

        arena.close();

        assertThat(underTest.isAlive()).isFalse();
    }

    @Test
    void wrapped_segment_has_same_value() {
        var nativeSeg = allocateNativeSeg(firstTestValue.rawValue());

        var underTest = wrapNativeSeg(nativeSeg);

        assertThat(underTest.getValue()).isEqualTo(firstTestValue.typedValue());
    }

    @Test
    void if_value_of_wrapped_seg_changes_then_segs_value_changes_too() {
        var nativeSeg = allocateNativeSeg(firstTestValue.rawValue());
        var underTest = wrapNativeSeg(nativeSeg);

        var expectedValue = testValueIter.next();
        nativeSeg.asByteBuffer().clear().put(expectedValue.rawValue());

        assertThat(underTest.getValue()).isEqualTo(expectedValue.typedValue());

    }

    @Test
    void wrapped_seg_has_same_address_as_native_seg() {
        var nativeSeg = allocateNativeSeg(firstTestValue.rawValue());
        var underTest = wrapNativeSeg(nativeSeg);

        assertThat(underTest.rawAddress()).isEqualTo(nativeSeg.address());
    }

    @Test
    void wrapped_seg_and_native_seg_share_same_scope() {
        var nativeSeg = allocateNativeSeg(firstTestValue.rawValue());
        var underTest = wrapNativeSeg(nativeSeg);

        assertThat(underTest.isAlive()).isTrue();
        arena.close();
        assertThat(underTest.isAlive()).isFalse();
    }

    @Test
    void allocate_pointer_success() {
        var result = allocateNullPointer();

        assertThat(result).isInstanceOf(FluffyPointer.class);
    }

    @Test
    void null_pointer_points_to_null() {
        var result = allocateNullPointer();

        assertThat(result.getRawValue()).isEqualTo(MemorySegment.NULL.address());
    }

    @Test
    void pointer_of_segment_points_to_segment() {
        var longSegment = allocateSeg();
        var underTest = allocatePointer(longSegment);

        assertThat(underTest.getRawValue()).isEqualTo(longSegment.rawAddress());
    }

    @Test
    void pointer_of_address_points_to_address() {
        var expectedAddress = allocateSeg().rawAddress();
        var underTest = allocatePointer(expectedAddress);

        assertThat(underTest.getRawValue()).isEqualTo(expectedAddress);
    }

    @Test
    void pointer_with_scope_is_not_alive_when_scope_is_closed() {
        var addr = allocateSeg().rawAddress();
        var underTest = allocatescopePointer(addr, scope);

        arena.close();

        assertThat(underTest.isAlive()).isFalse();
    }

    @Test
    void pointer_from_address_to_type_can_be_dereferenced_as_type() {
        var nativeSeg = allocateNativeSeg(firstTestValue.rawValue());
        var underTest = allocatePointer(nativeSeg.address());

        assertThat(underTest.dereference()).isEqualTo(firstTestValue.typedValue());
    }

    @Test
    void can_create_empty_typed_pointer() {
        var result = pointer().of(testValueType).allocate();

        assertThat(result).isInstanceOf(FluffyScalarPointer.class);
    }

    @Test
    void can_create_empty_typed_pointer_tied_to_a_scope() {
        var result = pointer().of(testValueType).allocate(scope);

        assertThat(result.isAlive()).isTrue();
        arena.close();
        assertThat(result.isAlive()).isFalse();
    }

    @Test
    void rawDereference_returns_seg_of_data_pointed_to() {
        var nativeSeg = allocateNativeSeg(firstTestValue.rawValue());
        var underTest = allocatePointer(nativeSeg.address());

        var dereferencedSeg = underTest.rawDereference();
        assertThat(dereferencedSeg.address()).isEqualTo(nativeSeg.address());
        byte[] buf = new byte[firstTestValue.rawValue().length];
        for (int i = 0; i < buf.length; i++) {
            buf[i] = dereferencedSeg.get(ValueLayout.JAVA_BYTE, i);
        }
        assertThat(buf).isEqualTo(firstTestValue.rawValue());
    }

    protected final MemorySegment allocateNativeSeg(byte[] rawContents) {
        var result = allocateNative(segmentLayout, scope);
        result.asByteBuffer().put(rawContents);
        return result;
    }

    protected final FluffyScalarSegment<? extends T> allocateSeg() {
        return segment().of(firstTestValue.typedValue()).allocate();
    }

    protected final FluffyScalarSegment<? extends T> allocateScopedSeg(SegmentScope scope) {
        return segment().of(firstTestValue.typedValue()).allocate(scope);
    }

    protected final FluffyScalarSegment<? extends T> wrapNativeSeg(MemorySegment nativeSeg) {
        return wrap(nativeSeg).as(testValueType);
    }

    protected final FluffyScalarPointer<? extends T> allocatePointer(long address) {
        return pointer().to(address).as(testValueType).allocate();
    }

    protected final FluffyScalarPointer<? extends T> allocatescopePointer(long address,
            SegmentScope scope) {
        return pointer().to(address).as(testValueType).allocate(scope);
    }

    protected final FluffyScalarPointer<? extends T> allocatePointer(FluffyScalarSegment<? extends T> seg) {
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
