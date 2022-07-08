package com.itemis.fluffyj.memory.error;

import static java.util.Objects.requireNonNull;

/**
 * Failing Fluffy Memory code usually throws this kind of exception.
 */
public class FluffyMemoryException extends RuntimeException {

    private static final long serialVersionUID = -8073012686837630937L;

    /**
     * Construct a new instance with no cause and the provided {@code message}.
     *
     * @param message
     */
    public FluffyMemoryException(String message) {
        super(requireNonNull(message, "message"));
    }

    /**
     * Construct a new instance with {@code cause} and {@code message}.
     *
     * @param message
     * @param cause
     */
    public FluffyMemoryException(String message, Throwable cause) {
        super(requireNonNull(message, "message"), requireNonNull(cause, "cause"));
    }
}
