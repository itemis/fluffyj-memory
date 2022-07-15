package com.itemis.fluffyj.memory.internal;

import com.itemis.fluffyj.exceptions.InstantiationNotPermittedException;
import com.itemis.fluffyj.memory.api.FluffyMemoryTypeConverter;

import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemoryLayout;
import jdk.incubator.foreign.ResourceScope;

public final class FluffyMemoryFuncPointerBuilderStages {

    private FluffyMemoryFuncPointerBuilderStages() {
        throw new InstantiationNotPermittedException();
    }

    public static interface FuncStage {
        public TypeConverterStage ofType(Object receiver);
    }

    public static interface CFuncStage {
        public ArgsStage of(Object receiver);
    }

    public static interface TypeConverterStage {
        public ArgsStage withTypeConverter(FluffyMemoryTypeConverter conv);
    }

    public static interface ArgsStage {
        ReturnTypeStage withArgs(MemoryLayout... args);

        ReturnTypeStage withoutArgs();

        MemoryAddress autoBind();

        MemoryAddress autoBindTo(ResourceScope scope);
    }

    public static interface ReturnTypeStage {
        BinderStage andReturnType(MemoryLayout returnType);

        BinderStage andNoReturnType();
    }

    public static interface BinderStage {
        MemoryAddress bindTo(ResourceScope scope);

        MemoryAddress bindToGlobalScope();
    }
}
