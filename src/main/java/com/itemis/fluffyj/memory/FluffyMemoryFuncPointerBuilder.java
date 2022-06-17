package com.itemis.fluffyj.memory;

import static java.lang.invoke.MethodHandles.lookup;
import static java.lang.invoke.MethodType.methodType;
import static java.util.Arrays.stream;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.empty;
import static jdk.incubator.foreign.ResourceScope.globalScope;

import com.itemis.fluffyj.memory.api.FluffyMemoryTypeConverter;
import com.itemis.fluffyj.memory.error.FluffyMemoryException;
import com.itemis.fluffyj.memory.internal.FluffyMemoryFuncPointerBuilderStages.ArgsStage;
import com.itemis.fluffyj.memory.internal.FluffyMemoryFuncPointerBuilderStages.BinderStage;
import com.itemis.fluffyj.memory.internal.FluffyMemoryFuncPointerBuilderStages.CFuncStage;
import com.itemis.fluffyj.memory.internal.FluffyMemoryFuncPointerBuilderStages.FuncStage;
import com.itemis.fluffyj.memory.internal.FluffyMemoryFuncPointerBuilderStages.ReturnTypeStage;
import com.itemis.fluffyj.memory.internal.FluffyMemoryFuncPointerBuilderStages.TypeConverterStage;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import jdk.incubator.foreign.CLinker;
import jdk.incubator.foreign.FunctionDescriptor;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemoryLayout;
import jdk.incubator.foreign.ResourceScope;

public final class FluffyMemoryFuncPointerBuilder implements CFuncStage, FuncStage, ArgsStage, TypeConverterStage, ReturnTypeStage, BinderStage {

    private Object receiver;
    private MemoryLayout[] nativeArgTypes;
    private Optional<MemoryLayout> nativeReturnType = empty();
    private Class<?>[] javaArgTypes;
    private Class<?> javaReturnType;

    private final String funcName;
    private FluffyMemoryTypeConverter typeConverter;

    public FluffyMemoryFuncPointerBuilder(String funcName, FluffyMemoryTypeConverter conv) {
        this.funcName = requireNonNull(funcName, "funcName");
        this.typeConverter = requireNonNull(conv, "conv");
    }

    public FluffyMemoryFuncPointerBuilder(String funcName) {
        this.funcName = requireNonNull(funcName, "funcName");
    }

    @Override
    public ArgsStage of(Object receiver) {
        this.receiver = requireNonNull(receiver, "receiver");
        return this;
    }

    @Override
    public TypeConverterStage ofType(Object receiver) {
        this.receiver = requireNonNull(receiver, "receiver");
        return this;
    }

    @Override
    public ArgsStage withTypeConverter(FluffyMemoryTypeConverter conv) {
        this.typeConverter = requireNonNull(conv, "conv");
        return this;
    }

    @Override
    public ReturnTypeStage withArgs(MemoryLayout... args) {
        nativeArgTypes = requireNonNull(args, "args");
        javaArgTypes = typeConverter.getJavaTypes(args);
        return this;
    }

    @Override
    public ReturnTypeStage withoutArgs() {
        return withArgs();
    }

    @Override
    public MemoryAddress autoBind() {
        return autoBindTo(globalScope());
    }

    @Override
    public MemoryAddress autoBindTo(ResourceScope scope) {
        requireNonNull(scope, "scope");

        var candidateMethods = extractMethodCandidates();

        if (candidateMethods.isEmpty()) {
            throwCannotFindMethod(null);
        } else if (candidateMethods.stream().anyMatch(mthd -> mthd.isSynthetic())) {
            throwCannotBindSynthMethod();
        } else if (candidateMethods.size() > 1) {
            throw new FluffyMemoryException("Cannot autobind overloaded method '" + funcName + "'. Please perform manual bind.");
        }
        var method = candidateMethods.get(0);

        if (!method.canAccess(receiver)) {
            throwCannotBindNonAccessibleMethod();
        }

        javaArgTypes = method.getParameterTypes();
        try {
            nativeArgTypes = typeConverter.getNativeTypes(javaArgTypes);
        } catch (FluffyMemoryException e) {
            throw new FluffyMemoryException("Method '" + funcName + "' of type " + receiver.getClass().getCanonicalName() + " has unsupported argument types.",
                e);
        }
        javaReturnType = method.getReturnType();
        if (javaReturnType.equals(Void.class)) {
            throw new FluffyMemoryException("Return type " + Void.class.getCanonicalName() + " is unsupported. Use " + void.class.getCanonicalName());
        } else if (!(javaReturnType.equals(void.class))) {
            try {
                nativeReturnType = Optional.of(typeConverter.getNativeType(javaReturnType));
            } catch (FluffyMemoryException e) {
                throw new FluffyMemoryException(
                    "Method '" + funcName + "' of type " + receiver.getClass().getCanonicalName() + " has unsupported return type.",
                    e);
            }
        }
        return bindTo(scope);
    }

    @Override
    public BinderStage andReturnType(MemoryLayout returnType) {
        requireNonNull(returnType, "returnType");

        nativeReturnType = Optional.of(returnType);
        javaReturnType = typeConverter.getJavaType(returnType);
        return this;
    }

    @Override
    public BinderStage andNoReturnType() {
        javaReturnType = void.class;
        return this;
    }

    @Override
    public MemoryAddress bindTo(ResourceScope scope) {
        requireNonNull(scope, "scope");

        if (extractMethodCandidates().stream().anyMatch(mthd -> mthd.isSynthetic())) {
            throwCannotBindSynthMethod();
        }

        MethodHandle callback = null;
        try {
            callback = lookup().bind(receiver, funcName, methodType(javaReturnType, javaArgTypes));
        } catch (NoSuchMethodException e) {
            throwCannotFindMethod(e);
        } catch (IllegalAccessException e) {
            throwCannotBindNonAccessibleMethod();
        }

        if (nativeReturnType.isPresent()) {
            return CLinker.getInstance().upcallStub(callback, FunctionDescriptor.of(nativeReturnType.get(), nativeArgTypes), scope);
        } else {
            return CLinker.getInstance().upcallStub(callback, FunctionDescriptor.ofVoid(nativeArgTypes), scope);
        }
    }

    @Override
    public MemoryAddress bindToGlobalScope() {
        return bindTo(globalScope());
    }

    private List<Method> extractMethodCandidates() {
        return stream(receiver.getClass().getDeclaredMethods()).filter(mthd -> mthd.getName().equals(funcName)).toList();
    }

    private void throwCannotBindSynthMethod() {
        throw new FluffyMemoryException("Cannot create function pointer to synthetic Java methods.");
    }

    private void throwCannotBindNonAccessibleMethod() {
        throw new FluffyMemoryException("Cannot create function pointer to non accessible Java methods.");
    }

    private void throwCannotFindMethod(Throwable cause) {
        var errMsg = "Could not find method '" + funcName + "' in type " + receiver.getClass().getCanonicalName();
        if (cause == null) {
            throw new FluffyMemoryException(errMsg);
        } else {
            throw new FluffyMemoryException(errMsg, cause);
        }
    }
}
