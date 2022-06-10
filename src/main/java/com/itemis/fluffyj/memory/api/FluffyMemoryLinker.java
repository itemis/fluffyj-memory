package com.itemis.fluffyj.memory.api;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;

import jdk.incubator.foreign.Addressable;
import jdk.incubator.foreign.CLinker;
import jdk.incubator.foreign.FunctionDescriptor;

public interface FluffyMemoryLinker {

    public static final FluffyMemoryLinker C_LINKER =
        (symbol, srcFuncType, targetMethodType) -> CLinker.getInstance().downcallHandle(symbol, targetMethodType, srcFuncType);

    MethodHandle link(Addressable symbol, FunctionDescriptor srcFuncDescr, MethodType targetMethodType);
}
