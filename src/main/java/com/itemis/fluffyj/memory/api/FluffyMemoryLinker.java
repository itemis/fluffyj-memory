package com.itemis.fluffyj.memory.api;

import java.lang.foreign.Addressable;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.SymbolLookup;
import java.lang.invoke.MethodHandle;

/**
 * A linker abstraction that is able to link a symbol of a library to a JVM {@link MethodHandle}
 * instance.
 */
@FunctionalInterface
public interface FluffyMemoryLinker {

    /**
     * A {@link FluffyMemoryLinker} that is able to link JVM and native code according to the ABI of
     * the underlying native platform.
     */
    FluffyMemoryLinker NATIVE_LINKER =
        (symbol, srcFuncType) -> Linker.nativeLinker().downcallHandle(symbol, srcFuncType);

    /**
     * Link a native function (symbol) to a JVM {@link MethodHandle}.
     *
     * @param symbol - Symbol to link to. Use a {@link SymbolLookup} to acquire one of these.
     * @param srcFuncDescr - Native function description understandable by the JVM. To acquire, use
     *        {@link FunctionDescriptor}.
     * @return A {@link MethodHandle} instance with which it is possible to call native code via JVM
     *         semantics.
     */
    MethodHandle link(Addressable symbol, FunctionDescriptor srcFuncDescr);
}
