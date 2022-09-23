package com.itemis.fluffyj.memory.tests;

import static com.itemis.fluffyj.memory.FluffyMemory.pointer;
import static com.itemis.fluffyj.memory.FluffyMemory.segment;
import static com.itemis.fluffyj.memory.FluffyMemory.wrap;
import static java.lang.foreign.MemorySegment.allocateNative;
import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;

import com.itemis.fluffyj.memory.api.FluffyPointer;
import com.itemis.fluffyj.memory.api.FluffySegment;
import com.itemis.fluffyj.memory.api.FluffyVectorPointer;
import com.itemis.fluffyj.memory.api.FluffyVectorSegment;

import org.junit.jupiter.api.Test;

import java.lang.foreign.MemoryAddress;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.MemorySession;
import java.util.Iterator;

public abstract class FluffyVectorDataManipulationTest<T> extends MemorySessionEnabledTest {

    private final Iterator<FluffyMemoryVectorTestValue<T>> testValueIter;
    private final MemoryLayout segmentLayout;
    private final FluffyMemoryVectorTestValue<T> firstTestValue;
    private final Class<? extends T[]> testValueType;

    protected FluffyVectorDataManipulationTest(Iterator<FluffyMemoryVectorTestValue<T>> testValueIter,
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
    void when_session_is_closed_then_segment_is_not_alive_anymore() {
        var underTest = allocateSessionSeg(session);

        session.close();

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
        assertThat(underTest.getValue()).isNotEqualTo(expectedValue.typedValue());

        nativeSeg.asByteBuffer().clear().put(expectedValue.rawValue());

        assertThat(underTest.getValue()).isEqualTo(expectedValue.typedValue());
    }

    @Test
    void wrapped_seg_has_same_address_as_native_seg() {
        var nativeSeg = allocateNativeSeg(firstTestValue.rawValue());
        var underTest = wrapNativeSeg(nativeSeg);

        assertThat(underTest.address()).isEqualTo(nativeSeg.address());
    }

    @Test
    void wrapped_seg_and_native_seg_share_same_session() {
        var nativeSeg = allocateNativeSeg(firstTestValue.rawValue(), session);
        var underTest = wrapNativeSeg(nativeSeg);

        assertThat(underTest.isAlive()).isTrue();
        session.close();
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

        assertThat(result.getValue()).isEqualTo(MemoryAddress.NULL);
    }

    @Test
    void pointer_of_segment_points_to_segment() {
        var nativeSeg = allocateSeg();
        var underTest = allocatePointer(nativeSeg);

        assertThat(underTest.getValue()).isEqualTo(nativeSeg.address());
    }

    @Test
    void pointer_of_address_points_to_address() {
        var expectedAddress = allocateSeg().address();
        var underTest = allocatePointer(expectedAddress);

        assertThat(underTest.getValue()).isEqualTo(expectedAddress);
    }

    @Test
    void pointer_with_session_is_not_alive_when_session_is_closed() {
        var addr = allocateSeg().address();
        var underTest = allocateSessionPointer(addr, session);

        session.close();

        assertThat(underTest.isAlive()).isFalse();
    }

    @Test
    void pointer_from_address_to_type_can_be_dereferenced_as_type() {
        var nativeSeg = allocateNativeSeg(firstTestValue.rawValue());
        var underTest = allocatePointer(nativeSeg.address());

        assertThat(underTest.dereference()).isEqualTo(firstTestValue.typedValue());
    }

    protected MemorySegment allocateNativeSeg(byte[] rawContents) {
        return allocateNativeSeg(rawContents, MemorySession.global());
    }

    protected MemorySegment allocateNativeSeg(byte[] rawContents, MemorySession session) {
        var result = allocateNative(segmentLayout, session);
        result.asByteBuffer().put(rawContents);
        return result;
    }

    protected final FluffyVectorSegment<? extends T> allocateSeg() {
        return segment().ofArray(firstTestValue.typedValue()).allocate();
    }

    protected final FluffyVectorSegment<? extends T> allocateSessionSeg(MemorySession session) {
        return segment().ofArray(firstTestValue.typedValue()).allocate(session);
    }

    protected final FluffyVectorSegment<? extends T> wrapNativeSeg(MemorySegment nativeSeg) {
        return wrap(nativeSeg).asArray(testValueType);
    }

    protected final FluffyVectorPointer<? extends T> allocatePointer(MemoryAddress address) {
        return pointer().to(address).asArray(firstTestValue.length()).of(testValueType).allocate();
    }

    protected final FluffyVectorPointer<? extends T> allocateSessionPointer(MemoryAddress address,
            MemorySession session) {
        return pointer().to(address).asArray(firstTestValue.length()).of(testValueType).allocate(session);
    }

    protected final FluffyVectorPointer<? extends T> allocatePointer(FluffyVectorSegment<? extends T> seg) {
        return pointer().toArray(seg).allocate();
    }

    protected final FluffyVectorPointer<? extends T> allocateNullPointer() {
        return allocatePointer(MemoryAddress.NULL);
    }

    protected static abstract class FluffyMemoryVectorTestValueIterator<V>
            implements Iterator<FluffyMemoryVectorTestValue<V>> {
        @Override
        public boolean hasNext() {
            return true;
        }
    }
}
