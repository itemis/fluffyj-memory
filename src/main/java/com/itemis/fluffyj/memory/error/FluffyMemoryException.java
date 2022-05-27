package com.itemis.fluffyj.memory.error;

import static java.util.Objects.requireNonNull;

public class FluffyMemoryException extends RuntimeException {

    private static final long serialVersionUID = -8073012686837630937L;

    public FluffyMemoryException(String message) {
        super(requireNonNull(message, "message"));
    }

    public FluffyMemoryException(String message, Throwable cause) {
        super(requireNonNull(message, "message"), requireNonNull(cause, "cause"));
    }
}
