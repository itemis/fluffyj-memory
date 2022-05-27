package com.itemis.fluffyj.memory.error;

import static com.itemis.fluffyj.tests.FluffyTestHelper.assertNullArgNotAccepted;
import static org.assertj.core.api.Assertions.assertThat;

import com.itemis.fluffyj.tests.exceptions.ExpectedExceptions;

import org.junit.jupiter.api.Test;

import java.io.IOException;

public class FluffyMemoryExceptionTest {

    @Test
    public void string_constructor_does_not_accept_null() {
        assertNullArgNotAccepted(() -> new FluffyMemoryException((String) null), "message");
    }

    @Test
    public void string_constructor_works() {
        var testMsg = "testMsg";
        var underTest = new FluffyMemoryException(testMsg);

        assertThat(underTest.getMessage()).isEqualTo(testMsg);
    }

    @Test
    public void string_cause_constructor_does_not_accept_null_msg() {
        assertNullArgNotAccepted(() -> new FluffyMemoryException((String) null, new IOException()), "message");
    }

    @Test
    public void string_cause_constructor_does_not_accept_null_cause() {
        assertNullArgNotAccepted(() -> new FluffyMemoryException("message", null), "cause");
    }

    @Test
    public void string_cause_constructor_works() {
        var testMsg = "testMsg";
        var expectedException = ExpectedExceptions.EXPECTED_CHECKED_EXCEPTION;

        var underTest = new FluffyMemoryException(testMsg, expectedException);

        assertThat(underTest.getMessage()).isEqualTo(testMsg);
        assertThat(underTest.getCause()).isSameAs(expectedException);
    }
}
