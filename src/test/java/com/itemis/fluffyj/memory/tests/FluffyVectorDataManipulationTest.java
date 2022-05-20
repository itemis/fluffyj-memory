package com.itemis.fluffyj.memory.tests;

import static com.itemis.fluffyj.memory.FluffyMemory.pointer;
import static com.itemis.fluffyj.memory.FluffyMemory.segment;
import static com.itemis.fluffyj.memory.FluffyMemory.wrap;
import static java.util.Objects.requireNonNull;
import static jdk.incubator.foreign.MemoryAddress.NULL;
import static jdk.incubator.foreign.ResourceScope.globalScope;
import static org.assertj.core.api.Assertions.assertThat;

import com.itemis.fluffyj.memory.api.FluffyPointer;
import com.itemis.fluffyj.memory.api.FluffySegment;
import com.itemis.fluffyj.memory.api.FluffyVectorPointer;
import com.itemis.fluffyj.memory.api.FluffyVectorSegment;

import org.junit.jupiter.api.Test;

import java.util.function.Supplier;

import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;
import jdk.incubator.foreign.ResourceScope;

public abstract class FluffyVectorDataManipulationTest<T> extends MemoryScopedTest {

    private final T[] testValue;
    private final Supplier<? extends T[]> testValueSup;
    private final Class<T[]> testValueType;

    // Cast seems reasonably safe, since we are directly using testValue's class.
    @SuppressWarnings("unchecked")
    protected FluffyVectorDataManipulationTest(Supplier<? extends T[]> testValueSup) {
        this.testValueSup = requireNonNull(testValueSup, "testValueSup");
        testValue = testValueSup.get();
        testValueType = (Class<T[]>) testValue.getClass();
    }

    @Test
    void allocate_segment_success() {
        var result = allocateSeg();

        assertThat(result).isInstanceOf(FluffySegment.class);
    }

    @Test
    void allocate_with_initial_value_success() {
        var result = allocateSeg();

        assertThat(result.getValue()).isEqualTo(testValue);
    }

    @Test
    void a_newly_allocated_segment_is_alive() {
        var underTest = allocateSeg();

        assertThat(underTest.isAlive()).isTrue();
    }

    @Test
    void when_scope_is_closed_then_segment_is_not_alive_anymore() {
        var underTest = allocateScopedSeg(scope);

        scope.close();

        assertThat(underTest.isAlive()).isFalse();
    }

    @Test
    void wrapped_segment_has_same_value() {
        var nativeSeg = allocateNativeSeg(testValue);

        var underTest = wrapNativeSeg(nativeSeg);

        assertThat(underTest.getValue()).isEqualTo(testValue);
    }

    @Test
    void if_value_of_wrapped_seg_changes_then_segs_value_changes_too() {
        var nativeSeg = allocateNativeSeg(testValue);
        var underTest = wrapNativeSeg(nativeSeg);

        T[] expectedValue = testValueSup.get();
        assertThat(underTest.getValue()).isNotEqualTo(expectedValue);

        changeNativeSegValue(nativeSeg, expectedValue);

        assertThat(underTest.getValue()).isEqualTo(expectedValue);
    }

    @Test
    void wrapped_seg_has_same_address_as_native_seg() {
        var nativeSeg = allocateNativeSeg(testValue);
        var underTest = wrapNativeSeg(nativeSeg);

        assertThat(underTest.address()).isEqualTo(nativeSeg.address());
    }

    @Test
    void wrapped_seg_and_native_seg_share_same_scope() {
        var nativeSeg = allocateNativeSeg(testValue, scope);
        var underTest = wrapNativeSeg(nativeSeg);

        assertThat(underTest.isAlive()).isTrue();
        scope.close();
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

        assertThat(result.getValue()).isEqualTo(NULL);
    }

    @Test
    void pointer_of_segment_points_to_segment() {
        var nativeSeg = allocateSeg();
        var underTest = allocatePointer(nativeSeg);

        assertThat(underTest.getValue()).isEqualTo(nativeSeg.address());
    }

    @Test
    void pointer_of_address_points_to_address() {
        MemoryAddress expectedAddress = allocateSeg().address();
        var underTest = allocatePointer(expectedAddress);

        assertThat(underTest.getValue()).isEqualTo(expectedAddress);
    }

    @Test
    void pointer_with_scope_is_not_alive_when_scope_is_closed() {
        MemoryAddress addr = allocateSeg().address();
        var underTest = allocateScopedPointer(addr, scope);

        scope.close();

        assertThat(underTest.isAlive()).isFalse();
    }

    @Test
    void pointer_from_address_to_type_can_be_dereferenced_as_type() {
        var nativeSeg = allocateNativeSeg(testValue);
        var underTest = allocatePointer(nativeSeg.address());

        assertThat(underTest.dereference()).isEqualTo(testValue);
    }

    protected MemorySegment allocateNativeSeg(T[] initialValue) {
        return allocateNativeSeg(initialValue, globalScope());
    }

    protected abstract MemorySegment allocateNativeSeg(T[] initialValue, ResourceScope scope);

    protected abstract void changeNativeSegValue(MemorySegment nativeSeg, T[] newValue);

    protected final FluffyVectorSegment<? extends T> allocateSeg() {
        return segment().ofArray(testValue).allocate();
    }

    protected final FluffyVectorSegment<? extends T> allocateScopedSeg(ResourceScope scope) {
        return segment().ofArray(testValue).allocate(scope);
    }

    protected final FluffyVectorSegment<? extends T> wrapNativeSeg(MemorySegment nativeSeg) {
        return wrap(nativeSeg).asArray(testValueType);
    }

    protected final FluffyVectorPointer<? extends T> allocatePointer(MemoryAddress address) {
        return pointer().to(address).asArray(testValue.length).of(testValueType).allocate();
    }

    protected final FluffyVectorPointer<? extends T> allocateScopedPointer(MemoryAddress address, ResourceScope scope) {
        return pointer().to(address).asArray(testValue.length).of(testValueType).allocate(scope);
    }

    protected final FluffyVectorPointer<? extends T> allocatePointer(FluffyVectorSegment<? extends T> seg) {
        return pointer().toArray(seg).allocate();
    }

    protected final FluffyVectorPointer<? extends T> allocateNullPointer() {
        return allocatePointer(NULL);
    }
}
