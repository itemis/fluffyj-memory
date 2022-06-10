package com.itemis.fluffyj.memory;

import static com.itemis.fluffyj.exceptions.ThrowablePrettyfier.pretty;
import static java.util.Objects.requireNonNull;

import com.itemis.fluffyj.memory.api.FluffyMemoryTypeConverter;
import com.itemis.fluffyj.memory.error.FluffyMemoryException;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;

import jdk.incubator.foreign.CLinker;
import jdk.incubator.foreign.FunctionDescriptor;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemoryLayout;
import jdk.incubator.foreign.SymbolLookup;

/**
 * Instances of this class encapsulate a method call to a native method in a known non Java-based
 * library.
 *
 * @param <T> - Return type of the method to call. Use {@link Void} if method does not return a
 *        value.
 */
public final class NativeMethodHandle<T> {

    private final MethodHandle method;

    private NativeMethodHandle(MethodHandle method) {
        this.method = method;
    }

    // Well the cast ought to be safe enough. It is only possible to make harm here in case the
    // implementation of the builder itself is wrong. We try to mitigate this with good test
    // coverage.
    @SuppressWarnings("unchecked")
    public T call(Object... args) {
        try {
            return (T) method.invokeWithArguments(args);
        } catch (Throwable e) {
            throw new FluffyMemoryException("Calling native code failed: " + pretty(e), e);
        }
    }

    public static TypeConverterStage ofLib(SymbolLookup stdlib) {
        return new NativeMethodHandleBuilder<>(stdlib);
    }

    public static interface TypeConverterStage {
        ReturnTypeStage withTypeConverter(FluffyMemoryTypeConverter conv);
    }

    public static interface ReturnTypeStage {
        <T> FuncStage<T> returnType(Class<? super T> returnType);

        <T> FuncStage<T> noReturnType();
    }

    public static interface FuncStage<T> {
        ArgsStage<T> func(String name);
    }

    public static interface ArgsStage<T> {
        CreateStage<T> args(MemoryLayout... args);

        /**
         * Convenience method. Like {@link #args(MemoryLayout...)} but emphasizes the fact that the
         * native method does not take any arguments.
         *
         * @return {@link CreateStage} instance.
         */
        CreateStage<T> noArgs();
    }

    public static interface CreateStage<T> {
        NativeMethodHandle<T> create(CLinker linker);
    }

    private static final class NativeMethodHandleBuilder<T> implements TypeConverterStage, ReturnTypeStage, FuncStage<T>, ArgsStage<T>, CreateStage<T> {

        private SymbolLookup lib;
        private FluffyMemoryTypeConverter conv;
        private MemoryAddress funcRef;
        private Class<?> javaReturnType;
        private MemoryLayout cReturnType;
        private FunctionDescriptor funcDescr;
        private MethodType methodType;

        NativeMethodHandleBuilder(SymbolLookup lib) {
            this.lib = requireNonNull(lib, "lib");
        }

        @Override
        public ReturnTypeStage withTypeConverter(FluffyMemoryTypeConverter conv) {
            this.conv = requireNonNull(conv, "conv");
            return this;
        }

        // Well the cast ought to be safe enough. It is only possible to make harm here in case the
        // implementation itself is wrong. We try to mitigate this with good test coverage.
        @SuppressWarnings("unchecked")
        @Override
        public <K> FuncStage<K> returnType(Class<? super K> returnType) {
            javaReturnType = requireNonNull(returnType, "returnType");
            cReturnType = conv.getCType(returnType);
            return (FuncStage<K>) this;
        }

        // Well the cast ought to be safe enough. It is only possible to make harm here in case the
        // implementation itself is wrong. We try to mitigate this with good test coverage.
        @SuppressWarnings("unchecked")
        @Override
        public <K> FuncStage<K> noReturnType() {
            return (FuncStage<K>) returnType(Void.class);
        }

        @Override
        public ArgsStage<T> func(String funcName) {
            requireNonNull(funcName, "funcName");
            funcRef = loadSymbol(lib, funcName);
            return this;
        }

        @Override
        public CreateStage<T> args(MemoryLayout... args) {
            funcDescr = FunctionDescriptor.of(cReturnType, requireNonNull(args, "args"));
            methodType = MethodType.methodType(javaReturnType, conv.getJavaTypes(args));
            return (CreateStage<T>) this;
        }

        @Override
        public CreateStage<T> noArgs() {
            return args();
        }

        @Override
        public NativeMethodHandle<T> create(CLinker linker) {
            var methodHandle = linker.downcallHandle(funcRef, methodType, funcDescr);
            return new NativeMethodHandle<>(methodHandle);
        }

        private static MemoryAddress loadSymbol(SymbolLookup lookup, String symbolName) {
            return lookup.lookup(symbolName)
                .orElseThrow(() -> new FluffyMemoryException("Could not find symbol '" + symbolName + "' in library '" + lookup.toString() + "'."));
        }
    }
}
