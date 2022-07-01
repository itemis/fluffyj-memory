package com.itemis.fluffyj.memory;

import static com.itemis.fluffyj.exceptions.ThrowablePrettyfier.pretty;
import static com.itemis.fluffyj.memory.api.FluffyMemoryLinker.C_LINKER;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.empty;
import static jdk.incubator.foreign.CLinker.systemLookup;

import com.google.common.collect.ImmutableSet;
import com.itemis.fluffyj.memory.api.FluffyMemoryLinker;
import com.itemis.fluffyj.memory.api.FluffyMemoryTypeConverter;
import com.itemis.fluffyj.memory.error.FluffyMemoryException;
import com.itemis.fluffyj.memory.internal.impl.CDataTypeConverter;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.util.Optional;
import java.util.Set;

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

    private static final Set<Class<?>> VOID_RETURN_TYPES = ImmutableSet.of(Void.class, void.class);

    private final MethodHandle method;

    private NativeMethodHandle(MethodHandle method) {
        this.method = method;
    }

    /**
     * Execute the native method this handle stands for.
     *
     * @param args - All required arguments of the native method as Java instances. For pointers use
     *        instances of {@link MemoryAddress}.
     * @return The return value of the method or {@code null} if the method is not supposed to
     *         return anything.
     */
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

    /**
     * Construct a handle to one of C stdlib's functions. This is a shortcut that sets up an
     * appropriate library lookup, linker and type converter.
     *
     * @return Next stage of the builder.
     */
    public static ReturnTypeStage fromCStdLib() {
        return fromLib(systemLookup())
            .withLinker(C_LINKER)
            .withTypeConverter(new CDataTypeConverter());
    }

    /**
     * Construct a handle to one of an arbitrary C based lib's functions. This is a shortcut that
     * sets up an appropriate linker and type converter.
     *
     * @param lib - {@link SymbolLookup} for a C based library.
     * @return Next stage of the builder.
     */
    public static ReturnTypeStage fromCLib(SymbolLookup lib) {
        return fromLib(lib)
            .withLinker(C_LINKER)
            .withTypeConverter(new CDataTypeConverter());
    }

    /**
     * Construct a handle to a function of an arbitrary library that does also not need to be C
     * based.
     *
     * @param stdlib - {@link SymbolLookup} for the library to load the function from.
     * @return Next stage of the builder.
     */
    public static LinkerStage fromLib(SymbolLookup stdlib) {
        return new NativeMethodHandleBuilder<>(stdlib);
    }

    /**
     * Builder stage that takes care of setting up an appropriate linker.
     */
    public static interface LinkerStage {
        /**
         * Set up a {@link FluffyMemoryLinker} that is able to link native code to Java.
         *
         * @param linker - Linker to use.
         * @return Next stage of the builder.
         */
        TypeConverterStage withLinker(FluffyMemoryLinker linker);
    }

    /**
     * Builder stage that takes care of setting up an appropriate type converter.
     */
    public static interface TypeConverterStage {
        /**
         * Set up a {@link FluffyMemoryTypeConverter} that is able to convert Java to native types
         * and vice versa.
         *
         * @param conv - The converter to set up.
         * @return Next stage of the builder.
         */
        ReturnTypeStage withTypeConverter(FluffyMemoryTypeConverter conv);
    }

    /**
     * Builder stage that takes care of setting up the function's return type if any.
     */
    public static interface ReturnTypeStage {
        /**
         * Configure the return type of the function.
         *
         * @param <T> - Java returnType of the function.
         * @param returnType - Java return type of the function. Use primitive values instead of
         *        boxed values if appropriate.
         * @return Next stage of the builder.
         */
        <T> FuncStage<T> returnType(Class<? super T> returnType);

        /**
         * Set the return type to "none". The resulting method handle will not return any value.
         *
         * @return Next stage of the builder.
         */
        FuncStage<Void> noReturnType();
    }

    /**
     * Builder stage that takes care of setting up the function's name to bind to.
     *
     * @param <T> - Used to pass the function's return type to the final call of the builder chain.
     */
    public static interface FuncStage<T> {
        /**
         * Set up the name of the native function to bind to.
         *
         * @param name - The name.
         * @return Next stage of the builder.
         */
        ArgsStage<T> func(String name);
    }

    /**
     * @param <T> - Used to pass the function's return type to the final call of the builder chain.
     */
    public static interface ArgsStage<T> {
        /**
         * Set up arguments of the function in native memory layout and correct order.
         *
         * @param args - Arguments in correct order or no argument if none.
         * @return A new instance of {@link NativeMethodHandle}.
         */
        NativeMethodHandle<T> args(MemoryLayout... args);

        /**
         * Convenience method. Like {@link #args(MemoryLayout...)} but emphasizes the fact that the
         * native method does not take any arguments.
         *
         * @return {@link CreateStage} instance.
         */
        NativeMethodHandle<T> noArgs();
    }

    private static final class NativeMethodHandleBuilder<T>
            implements LinkerStage, TypeConverterStage, ReturnTypeStage, FuncStage<T>, ArgsStage<T> {

        private SymbolLookup lib;
        private FluffyMemoryLinker linker;
        private FluffyMemoryTypeConverter conv;
        private MemoryAddress symbol;
        private Class<?> javaReturnType;
        private Optional<MemoryLayout> cReturnType = empty();

        NativeMethodHandleBuilder(SymbolLookup lib) {
            this.lib = requireNonNull(lib, "lib");
        }

        @Override
        public TypeConverterStage withLinker(FluffyMemoryLinker linker) {
            this.linker = requireNonNull(linker, "linker");
            return this;
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
            if (!VOID_RETURN_TYPES.contains(javaReturnType)) {
                cReturnType = Optional.of(conv.getNativeType(returnType));
            }
            return (FuncStage<K>) this;
        }

        @Override
        public FuncStage<Void> noReturnType() {
            return returnType(void.class);
        }

        @Override
        public ArgsStage<T> func(String funcName) {
            requireNonNull(funcName, "funcName");
            symbol = loadSymbol(lib, funcName);
            return this;
        }

        @Override
        public NativeMethodHandle<T> args(MemoryLayout... args) {
            requireNonNull(args, "args");
            FunctionDescriptor srcFuncDescr = null;
            if (cReturnType.isPresent()) {
                srcFuncDescr = FunctionDescriptor.of(cReturnType.get(), args);
            } else {
                srcFuncDescr = FunctionDescriptor.ofVoid(args);
            }

            var targetMethodType = MethodType.methodType(javaReturnType, conv.getJavaTypes(args));
            var method = linker.link(symbol, srcFuncDescr, targetMethodType);
            return new NativeMethodHandle<>(method);
        }

        @Override
        public NativeMethodHandle<T> noArgs() {
            return args();
        }

        private static MemoryAddress loadSymbol(SymbolLookup lookup, String symbolName) {
            return lookup.lookup(symbolName)
                .orElseThrow(() -> new FluffyMemoryException("Could not find symbol '" + symbolName + "' in library '" + lookup.toString() + "'."));
        }
    }
}
