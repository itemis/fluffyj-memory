package com.itemis.fluffyj.memory.internal;

import com.itemis.fluffyj.exceptions.InstantiationNotPermittedException;
import com.itemis.fluffyj.memory.api.FluffyMemoryTypeConverter;

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.MemorySession;

public final class FluffyMemoryFuncPointerBuilderStages {

    private FluffyMemoryFuncPointerBuilderStages() {
        throw new InstantiationNotPermittedException();
    }

    public interface FuncStage {
        TypeConverterStage ofType(Object receiver);
    }

    public interface CFuncStage {
        ArgsStage of(Object receiver);
    }

    public interface TypeConverterStage {
        ArgsStage withTypeConverter(FluffyMemoryTypeConverter conv);
    }

    public interface ArgsStage {
        ReturnTypeStage withArgs(MemoryLayout... args);

        ReturnTypeStage withoutArgs();

        MemorySegment autoBind();

        MemorySegment autoBindTo(MemorySession session);
    }

    public interface ReturnTypeStage {
        BinderStage andReturnType(MemoryLayout returnType);

        BinderStage andNoReturnType();
    }

    public interface BinderStage {
        MemorySegment bindToGlobalSession();

        MemorySegment bindTo(MemorySession session);
    }
}
