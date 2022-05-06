package com.itemis.fluffyj.memory.internal;

import static com.itemis.fluffyj.tests.FluffyTestHelper.assertNullArgNotAccepted;
import static jdk.incubator.foreign.MemoryLayouts.JAVA_LONG;
import static org.assertj.core.api.Assertions.assertThat;

import com.itemis.fluffyj.memory.api.FluffySegment;
import com.itemis.fluffyj.memory.tests.MemoryScopedTest;

import org.junit.jupiter.api.Test;

import jdk.incubator.foreign.MemoryAddress;

class PointerOfBlobTest extends MemoryScopedTest {

    private static final byte[] BYTE_ARRAY = new byte[] {1, 2, 3};

    @Test
    void getValue_returns_a_value() {
        var underTest = buildPointer();

        assertThat(underTest.getValue()).isInstanceOf(MemoryAddress.class);
    }

    @Test
    void freshly_allocated_pointer_has_address() {
        var result = buildPointer();

        assertThat(result.address()).isInstanceOf(MemoryAddress.class);
    }

    @Test
    void two_pointers_do_have_different_addresses() {
        var firstPointer = buildPointer();
        var secondPointer = buildPointer();

        assertThat(firstPointer.address().toRawLongValue()).isNotEqualTo(secondPointer.address().toRawLongValue());
    }

    @Test
    void address_of_a_pointer_can_be_used_to_access_the_pointers_value() {
        var pointer = buildPointer();
        var nativeSeg = pointer.address().asSegment(JAVA_LONG.byteSize(), scope);
        assertThat(pointer.getValue().toRawLongValue()).isEqualTo(nativeSeg.asByteBuffer().getLong());
    }

    @Test
    void dereferentiation_yields_value_the_pointer_points_to() {
        var pointer = buildPointer();

        assertThat(pointer.dereference()).isEqualTo(BYTE_ARRAY);
    }

    @Test
    void a_new_pointer_is_alive() {
        var underTest = buildPointer();
        assertThat(underTest.isAlive()).isTrue();
    }

    @Test
    void when_closing_scope_pointer_is_not_alive_anymore() {
        var underTest = buildPointer();

        tearDownScope();
        assertThat(underTest.isAlive()).isFalse();
    }

    @Test
    void typedDereference_from_null_yields_npe() {
        var underTest = buildPointer();
        assertNullArgNotAccepted(() -> underTest.typedDereference(null), "rawDereferencedValue");
    }

    private PointerOfBlob buildPointer() {
        var blob = buildSegment();
        return buildPointer(blob);
    }

    private PointerOfBlob buildPointer(FluffySegment<byte[]> seg) {
        return new PointerOfBlob(seg.address(), BYTE_ARRAY.length, scope);
    }

    private BlobSegment buildSegment() {
        return new BlobSegment(BYTE_ARRAY, scope);
    }
}
