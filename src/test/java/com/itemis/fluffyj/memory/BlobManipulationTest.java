package com.itemis.fluffyj.memory;

import static com.google.common.primitives.Bytes.asList;
import static com.itemis.fluffyj.memory.FluffyMemory.pointer;
import static com.itemis.fluffyj.memory.FluffyMemory.segment;
import static com.itemis.fluffyj.memory.FluffyMemory.wrap;
import static com.tngtech.archunit.thirdparty.com.google.common.collect.Lists.reverse;
import static java.util.Arrays.copyOf;
import static jdk.incubator.foreign.MemoryAddress.NULL;
import static jdk.incubator.foreign.MemoryLayout.sequenceLayout;
import static jdk.incubator.foreign.MemoryLayouts.JAVA_BYTE;
import static jdk.incubator.foreign.MemorySegment.allocateNative;
import static org.assertj.core.api.Assertions.assertThat;

import com.itemis.fluffyj.memory.api.FluffyPointer;
import com.itemis.fluffyj.memory.api.FluffySegment;
import com.itemis.fluffyj.memory.internal.BlobSegment;
import com.itemis.fluffyj.memory.internal.PointerOfBlob;
import com.itemis.fluffyj.memory.tests.MemoryScopedTest;

import org.junit.jupiter.api.Test;

import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;
import jdk.incubator.foreign.ResourceScope;

class BlobManipulationTest extends MemoryScopedTest {

    private static byte[] BYTE_ARRAY = new byte[] {1, 2, 3};

    @Test
    void allocate_blob_segment_success() {
        var result = allocateSeg();

        assertThat(result).isInstanceOf(BlobSegment.class);
    }

    @Test
    void allocate_blob_with_initial_value_success() {
        var result = allocateSeg();

        assertThat(result.getValue()).isEqualTo(BYTE_ARRAY);
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
        var nativeSeg = allocateNativeSeg(BYTE_ARRAY);

        var underTest = wrap(nativeSeg).asBlob();

        assertThat(underTest.getValue()).isEqualTo(BYTE_ARRAY);
    }

    @Test
    void if_value_of_wrapped_seg_changes_then_segs_value_changes_too() {
        var nativeSeg = allocateNativeSeg(BYTE_ARRAY);
        var underTest = wrapNativeSeg(nativeSeg);

        var byteList = reverse(asList(copyOf(BYTE_ARRAY, BYTE_ARRAY.length)));
        var expectedValue = new byte[byteList.size()];
        for (int i = 0; i < expectedValue.length; i++) {
            expectedValue[i] = byteList.get(i);
        }
        nativeSeg.asByteBuffer().put(expectedValue);

        assertThat(underTest.getValue()).isEqualTo(expectedValue);
    }

    @Test
    void wrapped_seg_has_same_address_as_native_seg() {
        var nativeSeg = allocateNativeSeg(BYTE_ARRAY);
        var underTest = wrapNativeSeg(nativeSeg);

        assertThat(underTest.address()).isEqualTo(nativeSeg.address());
    }

    @Test
    void wrapped_seg_and_native_seg_share_same_scope() {
        var nativeSeg = allocateNativeSeg(BYTE_ARRAY);
        var underTest = wrapNativeSeg(nativeSeg);

        assertThat(underTest.isAlive()).isTrue();
        nativeSeg.scope().close();
        assertThat(underTest.isAlive()).isFalse();
    }

    @Test
    void allocate_pointer_success() {
        var seg = allocateNativeSeg(BYTE_ARRAY);
        var underTest = allocatePointer(seg.address());

        assertThat(underTest).isInstanceOf(PointerOfBlob.class);
    }

    @Test
    void null_pointer_points_to_null() {
        var result = allocateNullPointer();

        assertThat(result.getValue()).isEqualTo(NULL);
    }

    @Test
    void pointer_of_segment_points_to_segment() {
        var seg = allocateSeg();
        var underTest = allocatePointer(seg);

        assertThat(underTest.getValue()).isEqualTo(seg.address());
    }

    @Test
    void pointer_of_address_points_to_address() {
        var expectedAddress = allocateSeg().address();
        var underTest = allocatePointer(expectedAddress);

        assertThat(underTest.getValue()).isEqualTo(expectedAddress);
    }

    @Test
    void pointer_with_scope_is_not_alive_when_scope_is_closed() {
        var underTest = allocateScopedPointer(allocateSeg().address(), scope);

        scope.close();

        assertThat(underTest.isAlive()).isFalse();
    }

    @Test
    void pointer_from_address_to_blob_can_be_dereferenced_as_blob() {
        var nativeSeg = allocateNativeSeg(BYTE_ARRAY);
        var underTest = pointer().to(nativeSeg.address()).asBlob(BYTE_ARRAY.length).allocate(scope);

        assertThat(underTest.dereference()).isEqualTo(BYTE_ARRAY);
    }

    private FluffySegment<byte[]> allocateSeg() {
        return segment().of(BYTE_ARRAY).allocate();
    }

    private FluffySegment<byte[]> allocateScopedSeg(ResourceScope scope) {
        return segment().of(BYTE_ARRAY).allocate(scope);
    }

    private MemorySegment allocateNativeSeg(byte[] initialValue) {
        var result = allocateNative(sequenceLayout(initialValue.length, JAVA_BYTE), scope);
        result.asByteBuffer().put(initialValue);
        return result;
    }

    private FluffySegment<byte[]> wrapNativeSeg(MemorySegment nativeSeg) {
        return wrap(nativeSeg).asBlob();
    }

    private FluffyPointer<byte[]> allocatePointer(MemoryAddress address) {
        return pointer().to(address).asBlob(BYTE_ARRAY.length).allocate();
    }

    private FluffyPointer<byte[]> allocateScopedPointer(MemoryAddress address, ResourceScope scope) {
        return pointer().to(address).asBlob(BYTE_ARRAY.length).allocate(scope);
    }

    private FluffyPointer<byte[]> allocatePointer(FluffySegment<byte[]> seg) {
        return pointer().toArray(seg).allocate();
    }

    private FluffyPointer<byte[]> allocateNullPointer() {
        return allocatePointer(NULL);
    }
}
