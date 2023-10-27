package com.itemis.fluffyj.memory;

import static java.lang.foreign.MemorySegment.NULL;
import static java.util.Objects.requireNonNull;

import com.itemis.fluffyj.memory.api.FluffyPointer;
import com.itemis.fluffyj.memory.api.FluffyScalarSegment;
import com.itemis.fluffyj.memory.api.FluffyVectorSegment;
import com.itemis.fluffyj.memory.internal.FluffyMemoryFuncPointerBuilderStages.CFuncStage;
import com.itemis.fluffyj.memory.internal.FluffyMemoryFuncPointerBuilderStages.FuncStage;
import com.itemis.fluffyj.memory.internal.PointerOfThing;
import com.itemis.fluffyj.memory.internal.impl.CDataTypeConverter;

import java.lang.foreign.Arena;

/**
 * Intermediate pointer creation helper.
 */
public final class FluffyMemoryPointerBuilder {

    /**
     * @param <T> - Type of data the pointer should point to.
     * @param toHere - The resulting pointer will point to this segment's address.
     * @return A {@link FluffyMemoryScalarPointerAllocator} instance that is able to allocate
     *         pointers to data of type {@code T}.
     */
    public <T> FluffyMemoryScalarPointerAllocator<T> to(final FluffyScalarSegment<? extends T> toHere) {
        requireNonNull(toHere, "toHere");
        return new FluffyMemoryScalarPointerAllocator<>(toHere);
    }

    /**
     * @param <T> - Type of data the pointer should point to.
     * @param toHere - The resulting pointer will point to this segment's address.
     * @return A {@link FluffyMemoryScalarPointerAllocator} instance that is able to allocate
     *         pointers to data of type {@code T}.
     */
    public <T> FluffyMemoryVectorPointerAllocator<T> toArray(final FluffyVectorSegment<? extends T> toHere) {
        requireNonNull(toHere, "toHere");
        return new FluffyMemoryVectorPointerAllocator<>(toHere);
    }

    /**
     * @param address - The resulting pointer will point to this address.
     * @return A {@link FluffyMemoryScalarPointerAllocator} instance that is able to allocate
     *         pointers to data of type {@code T}.
     */
    public FluffyMemoryTypedPointerBuilder to(final long address) {
        requireNonNull(address, "address");
        return new FluffyMemoryTypedPointerBuilder(address);
    }

    /**
     * Allocate a pointer to arbitrary data. The resulting pointer cannot be dereferenced via its
     * API and is thought to be used in cases where an API writes an address into an "empty"
     * pointer.
     *
     * @return A new instance of {@link FluffyPointer} tied to the auto arena.
     */
    public FluffyPointer allocate() {
        return allocate(Arena.ofAuto());
    }

    /**
     * Like {@link #allocate()} but ties the newly created pointer's lifecycle to the provided
     * {@code arena}.
     *
     * @param arena - The arena to tie the pointer to.
     * @return A new instance of {@link FluffyPointer}.
     */
    public FluffyPointer allocate(final Arena arena) {
        return new PointerOfThing(requireNonNull(arena, "arena"));
    }

    public <T> FluffyMemoryScalarPointerAllocator<T> of(final Class<? extends T> type) {
        return new FluffyMemoryTypedPointerBuilder(NULL.address()).as(type);
    }

    /**
     * Construct a C style pointer to a JVM method. Will use JVM to/from C type conversion rules.
     *
     * @param funcName - Name of the JVM method to point to.
     * @return A builder that helps with creating the function pointer.
     */
    public CFuncStage toCFunc(final String funcName) {
        return new FluffyMemoryFuncPointerBuilder(requireNonNull(funcName, "funcName"), new CDataTypeConverter());
    }

    /**
     * Construct a native pointer to a JVM method. Argument and return type conversion rules must be
     * set manually.
     *
     * @param funcName - Name of the JVM method to point to.
     * @return A builder that helps with creating the function pointer.
     */
    public FuncStage toFunc(final String funcName) {
        return new FluffyMemoryFuncPointerBuilder(requireNonNull(funcName, "funcName"));
    }

    /**
     * A builder that helps with creating native pointers to scalar and array types.
     */
    public static final class FluffyMemoryTypedPointerBuilder {
        private final long address;

        /**
         * Create a new instance.
         *
         * @param address - Created pointer will point to this address.
         */
        public FluffyMemoryTypedPointerBuilder(final long address) {
            requireNonNull(address, "address");
            this.address = address;
        }

        /**
         * Create a native pointer to a scalar value (i. e. non array).
         *
         * @param <T> - Type of scalar data to point to.
         * @param type - Type of scalar data to point to.
         * @return A new builder instance that helps with creating this kind of pointer.
         */
        public <T> FluffyMemoryScalarPointerAllocator<T> as(final Class<? extends T> type) {
            return new FluffyMemoryScalarPointerAllocator<>(address, type);
        }

        /**
         * @param byteSize - The size of the array the pointer shall point to in bytes.
         */
        public FluffyMemoryTypedArrayPointerBuilder asArray(final int byteSize) {
            return new FluffyMemoryTypedArrayPointerBuilder(address, byteSize);
        }
    }

    /**
     * A builder that helps with creating native pointers to array types.
     */
    public static final class FluffyMemoryTypedArrayPointerBuilder {
        private final long byteSize;
        private final long address;

        /**
         * Create a new instance.
         *
         * @param address - Created pointer will point to this address.
         * @param byteSize - Size of the array to point to in bytes.
         */
        public FluffyMemoryTypedArrayPointerBuilder(final long address, final long byteSize) {
            this.address = requireNonNull(address, "address");
            this.byteSize = byteSize;
        }

        /**
         * Create a native pointer to an array value.
         *
         * @param <T> - Component type of the array to point to.
         * @return A new builder instance that helps with creating this kind of pointer.
         */
        public <T> FluffyMemoryVectorPointerAllocator<T> of(final Class<? extends T[]> arrayType) {
            return new FluffyMemoryVectorPointerAllocator<>(address, byteSize, arrayType);
        }
    }
}
