package com.itemis.fluffyj.memory;

import static java.lang.foreign.Linker.nativeLinker;
import static java.lang.invoke.MethodHandles.lookup;
import static java.lang.invoke.MethodType.methodType;
import static java.util.Arrays.stream;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.empty;

import com.itemis.fluffyj.memory.api.FluffyMemoryTypeConverter;
import com.itemis.fluffyj.memory.error.FluffyMemoryException;
import com.itemis.fluffyj.memory.internal.FluffyMemoryFuncPointerBuilderStages.ArgsStage;
import com.itemis.fluffyj.memory.internal.FluffyMemoryFuncPointerBuilderStages.BinderStage;
import com.itemis.fluffyj.memory.internal.FluffyMemoryFuncPointerBuilderStages.CFuncStage;
import com.itemis.fluffyj.memory.internal.FluffyMemoryFuncPointerBuilderStages.FuncStage;
import com.itemis.fluffyj.memory.internal.FluffyMemoryFuncPointerBuilderStages.ReturnTypeStage;
import com.itemis.fluffyj.memory.internal.FluffyMemoryFuncPointerBuilderStages.TypeConverterStage;

import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;


/**
 * Builder for a native pointer to a JVM method (function, effectively a memory address) which may
 * act as a function pointer from the perspective of native code. Should not be used directly.
 */
public final class FluffyMemoryFuncPointerBuilder
        implements CFuncStage, FuncStage, ArgsStage, TypeConverterStage, ReturnTypeStage, BinderStage {

    private final String funcName;

    private Object receiver;
    private MemoryLayout[] nativeArgTypes;
    private Optional<MemoryLayout> nativeReturnType = empty();
    private Class<?>[] javaArgTypes;
    private Class<?> javaReturnType;
    private FluffyMemoryTypeConverter typeConverter;

    /**
     * Construct a new instance.
     *
     * @param funcName - Name of the JVM method to point to.
     * @param conv - A {@link FluffyMemoryTypeConverter} that will be used to convert between JVM
     *        and native type arguments and return types.
     */
    public FluffyMemoryFuncPointerBuilder(final String funcName, final FluffyMemoryTypeConverter conv) {
        this.funcName = requireNonNull(funcName, "funcName");
        this.typeConverter = requireNonNull(conv, "conv");
    }

    /**
     * Construct a new instance with a pre set {@link FluffyMemoryTypeConverter} that converts
     * between JVM and C.
     *
     * @param funcName - Name of the JVM method to point to.
     */
    public FluffyMemoryFuncPointerBuilder(final String funcName) {
        this.funcName = requireNonNull(funcName, "funcName");
    }

    @Override
    public ArgsStage of(final Object receiver) {
        this.receiver = requireNonNull(receiver, "receiver");
        return this;
    }

    @Override
    public TypeConverterStage ofType(final Object receiver) {
        this.receiver = requireNonNull(receiver, "receiver");
        return this;
    }

    @Override
    public ArgsStage withTypeConverter(final FluffyMemoryTypeConverter conv) {
        this.typeConverter = requireNonNull(conv, "conv");
        return this;
    }

    @Override
    public ReturnTypeStage withArgs(final MemoryLayout... args) {
        nativeArgTypes = requireNonNull(args, "args");
        javaArgTypes = typeConverter.getJvmTypes(args);
        return this;
    }

    @Override
    public ReturnTypeStage withoutArgs() {
        return withArgs();
    }

    @Override
    public MemorySegment autoBind() {
        return autoBindTo(Arena.global());
    }

    @Override
    public MemorySegment autoBindTo(final Arena arena) {
        requireNonNull(arena, "arena");

        final var candidateMethods = extractMethodCandidates();

        if (candidateMethods.isEmpty()) {
            throwCannotFindMethod(null);
        } else if (candidateMethods.stream().anyMatch(Method::isSynthetic)) {
            throwCannotBindSynthMethod();
        } else if (candidateMethods.size() > 1) {
            throw new FluffyMemoryException(
                "Cannot autobind overloaded method '" + funcName + "'. Please perform manual bind.");
        }
        final var method = candidateMethods.get(0);

        if (!method.canAccess(receiver)) {
            throwCannotBindNonAccessibleMethod();
        }

        javaArgTypes = method.getParameterTypes();
        try {
            nativeArgTypes = typeConverter.getNativeTypes(javaArgTypes);
        } catch (final FluffyMemoryException e) {
            throw new FluffyMemoryException(
                "Method '" + funcName + "' of type " + receiver.getClass().getCanonicalName()
                    + " has unsupported argument types.",
                e);
        }
        javaReturnType = method.getReturnType();
        if (javaReturnType.equals(Void.class)) {
            throw new FluffyMemoryException("Return type " + Void.class.getCanonicalName() + " is unsupported. Use "
                + void.class.getCanonicalName());
        }
        if (!(javaReturnType.equals(void.class))) {
            try {
                nativeReturnType = Optional.of(typeConverter.getNativeType(javaReturnType));
            } catch (final FluffyMemoryException e) {
                throw new FluffyMemoryException(
                    "Method '" + funcName + "' of type " + receiver.getClass().getCanonicalName()
                        + " has unsupported return type.",
                    e);
            }
        }
        return bindTo(arena);
    }

    @Override
    public BinderStage andReturnType(final MemoryLayout returnType) {
        requireNonNull(returnType, "returnType");

        nativeReturnType = Optional.of(returnType);
        javaReturnType = typeConverter.getJvmType(returnType);
        return this;
    }

    @Override
    public BinderStage andNoReturnType() {
        javaReturnType = void.class;
        return this;
    }

    @Override
    public MemorySegment bindTo(final Arena arena) {
        requireNonNull(arena, "arena");

        if (extractMethodCandidates().stream().anyMatch(Method::isSynthetic)) {
            throwCannotBindSynthMethod();
        }

        MethodHandle callback = null;
        try {
            callback = lookup().bind(receiver, funcName, methodType(javaReturnType, javaArgTypes));
        } catch (final NoSuchMethodException e) {
            throwCannotFindMethod(e);
        } catch (final IllegalAccessException e) {
            throwCannotBindNonAccessibleMethod();
        }

        if (nativeReturnType.isPresent()) {
            return nativeLinker()
                .upcallStub(callback, FunctionDescriptor.of(nativeReturnType.get(), nativeArgTypes), arena);
        }
        return nativeLinker().upcallStub(callback, FunctionDescriptor.ofVoid(nativeArgTypes), arena);
    }

    @Override
    public MemorySegment bindToGlobalArena() {
        return bindTo(Arena.global());
    }

    private List<Method> extractMethodCandidates() {
        return stream(receiver.getClass().getDeclaredMethods()).filter(mthd -> mthd.getName().equals(funcName))
            .toList();
    }

    private void throwCannotBindSynthMethod() {
        throw new FluffyMemoryException("Cannot create function pointer to synthetic JVM methods.");
    }

    private void throwCannotBindNonAccessibleMethod() {
        throw new FluffyMemoryException("Cannot create function pointer to non accessible JVM methods.");
    }

    private void throwCannotFindMethod(final Throwable cause) {
        final var errMsg = "Could not find method '" + funcName + "' in type " + receiver.getClass().getCanonicalName();
        if (cause == null) {
            throw new FluffyMemoryException(errMsg);
        }
        throw new FluffyMemoryException(errMsg, cause);
    }
}
