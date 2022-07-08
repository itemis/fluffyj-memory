package com.itemis.fluffyj.memory.api;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;

import jdk.incubator.foreign.Addressable;
import jdk.incubator.foreign.CLinker;
import jdk.incubator.foreign.FunctionDescriptor;
import jdk.incubator.foreign.SymbolLookup;

/**
 * A linker abstraction that is able to link a symbol of a library to a Java {@link MethodHandle}
 * instance.
 */
public interface FluffyMemoryLinker {

    /**
     * A {@link FluffyMemoryLinker} that is able to link Java and C code.
     */
    public static final FluffyMemoryLinker C_LINKER =
        (symbol, srcFuncType, targetMethodType) -> CLinker.getInstance().downcallHandle(symbol, targetMethodType, srcFuncType);

    /**
     * Link a native function (symbol) to a Java {@link MethodHandle}.
     *
     * @param symbol - Symbol to link to. Use a {@link SymbolLookup} to acquire one of these.
     * @param srcFuncDescr - Describes the native function in a way that Java understands. Use
     *        {@link FunctionDescriptor#of(jdk.incubator.foreign.MemoryLayout, jdk.incubator.foreign.MemoryLayout...)
     * to acquire one of these.
     * @param targetMethodType - Describes the resulting Java method in a way that the native
     *        function understands. Use {@link MethodType#methodType(Class, Class, Class...)} to
     *        acquire one of these.
     * @return
     */
    MethodHandle link(Addressable symbol, FunctionDescriptor srcFuncDescr, MethodType targetMethodType);
}
