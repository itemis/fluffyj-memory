package com.itemis.fluffyj.memory.tests;

import static java.util.Arrays.copyOf;
import static java.util.Objects.requireNonNull;

import java.util.Objects;

public final class FluffyMemoryScalarTestValue<T> {

    private final T typedValue;
    private final byte[] rawValue;

    public FluffyMemoryScalarTestValue(T typedValue, byte[] rawValue) {
        this.typedValue = requireNonNull(typedValue, "typedValue");
        this.rawValue = Objects.requireNonNull(rawValue, "rawValue");
    }

    /**
     * @return the typedValue
     */
    public T typedValue() {
        return typedValue;
    }

    /**
     * @return Copy of the rawValue
     */
    public byte[] rawValue() {
        return copyOf(rawValue, rawValue.length);
    }

    // Cast seems reasonably safe, because we are casting the runtime class of the typedValue which
    // has compile time type T.
    @SuppressWarnings("unchecked")
    public Class<? extends T> type() {
        return (Class<? extends T>) typedValue.getClass();
    }
}
