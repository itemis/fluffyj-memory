package com.itemis.fluffyj.memory;

import static com.itemis.fluffyj.memory.FluffyMemory.pointer;
import static com.itemis.fluffyj.tests.FluffyTestHelper.assertFinal;
import static com.itemis.fluffyj.tests.FluffyTestHelper.assertIsStaticHelper;
import static com.itemis.fluffyj.tests.FluffyTestHelper.assertNullArgNotAccepted;
import static java.lang.foreign.Arena.global;
import static java.lang.foreign.ValueLayout.JAVA_BYTE;
import static java.lang.foreign.ValueLayout.JAVA_CHAR;
import static java.lang.foreign.ValueLayout.JAVA_INT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import com.itemis.fluffyj.memory.api.FluffyMemoryTypeConverter;
import com.itemis.fluffyj.memory.error.FluffyMemoryException;
import com.itemis.fluffyj.memory.internal.FluffyMemoryFuncPointerBuilderStages;
import com.itemis.fluffyj.memory.internal.FluffyMemoryFuncPointerBuilderStages.ArgsStage;
import com.itemis.fluffyj.memory.internal.FluffyMemoryFuncPointerBuilderStages.BinderStage;
import com.itemis.fluffyj.memory.internal.FluffyMemoryFuncPointerBuilderStages.CFuncStage;
import com.itemis.fluffyj.memory.internal.FluffyMemoryFuncPointerBuilderStages.ReturnTypeStage;
import com.itemis.fluffyj.memory.internal.FluffyMemoryFuncPointerBuilderStages.TypeConverterStage;
import com.itemis.fluffyj.memory.internal.impl.CDataTypeConverter;
import com.itemis.fluffyj.memory.tests.ArenafiedTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.util.Comparator;

public class FunctionPointerTest extends ArenafiedTest {

    private static final String TEST_FUNC = "testFunc";
    private static final String NO_ARGS = "noArgs";
    private static final String RETURN_TYPE_VOID = "returnTypeIsObjectVoid";

    private FluffyMemoryTypeConverter convMock;

    @BeforeEach
    void setUp() {
        convMock = Mockito.mock(FluffyMemoryTypeConverter.class);
    }

    @Test
    void builder_is_final() {
        assertFinal(FluffyMemoryFuncPointerBuilder.class);
    }

    @Test
    void test_correct_stage_order() {
        final var firstStage = pointer().toFunc(TEST_FUNC);
        assertThat(firstStage).isInstanceOf(FluffyMemoryFuncPointerBuilder.class);

        final var secondStage = firstStage.ofType(new TestType());
        assertThat(secondStage).isInstanceOf(TypeConverterStage.class);

        final var thirdStage = secondStage.withTypeConverter(new CDataTypeConverter());
        assertThat(thirdStage).isInstanceOf(ArgsStage.class);

        final var otherFourthStage = thirdStage.withoutArgs();
        assertThat(otherFourthStage).isInstanceOf(ReturnTypeStage.class);

        final var fourthStage = thirdStage.withArgs(JAVA_BYTE);
        assertThat(fourthStage).isInstanceOf(ReturnTypeStage.class);

        final var otherFifthStage = fourthStage.andNoReturnType();
        assertThat(otherFifthStage).isInstanceOf(BinderStage.class);

        final var fifthStage = fourthStage.andReturnType(JAVA_INT);
        assertThat(fifthStage).isInstanceOf(BinderStage.class);

        final var sixthStage = fifthStage.bindTo(arena);
        assertThat(sixthStage).isInstanceOf(MemorySegment.class);

        final var otherSixthStage = fifthStage.bindToGlobalArena();
        assertThat(otherSixthStage).isInstanceOf(MemorySegment.class);
    }

    @Test
    void test_bind_to_void_method_with_no_args_use_shortcuts() {
        final var result =
            pointer().toCFunc(NO_ARGS).of(new TestType()).withoutArgs().andNoReturnType().bindToGlobalArena();

        assertThat(result).isNotNull();
    }

    @Test
    void test_bind_to_void_method_with_no_args_dont_use_shortcuts() {
        final var result =
            pointer().toCFunc(NO_ARGS).of(new TestType()).withArgs().andNoReturnType().bindTo(global());
        assertThat(result).isNotNull();
    }

    @Test
    void when_manual_bind_unknown_method_then_throw_exception() {
        final var methodName = "unknownMethod";
        assertThatThrownBy(
            () -> pointer().toCFunc(methodName).of(this).withArgs().andNoReturnType().bindTo(global()))
                .isInstanceOf(FluffyMemoryException.class)
                .hasMessage("Could not find method '" + methodName + "' in type " + this.getClass().getCanonicalName())
                .hasCauseInstanceOf(NoSuchMethodException.class);
    }

    @Test
    void when_manual_bind_private_method_then_throw_exception() {
        final var methodName = "privateMethod";
        assertThatThrownBy(
            () -> pointer().toCFunc(methodName).of(new TestType()).withArgs().andNoReturnType().bindToGlobalArena())
                .isInstanceOf(FluffyMemoryException.class)
                .hasMessage("Cannot create function pointer to non accessible JVM methods.");
    }

    @Test
    void when_manual_bind_synthetic_method_then_throw_exception() {
        final var methodName = "compare";
        assertThatThrownBy(() -> pointer().toCFunc(methodName).of(new BridgeMethodDemo()).withoutArgs()
            .andNoReturnType().bindTo(arena))
                .isInstanceOf(FluffyMemoryException.class)
                .hasMessage("Cannot create function pointer to synthetic JVM methods.");
    }

    @Test
    void test_auto_bind_happy_path() {
        final var result = pointer().toCFunc(TEST_FUNC).of(new TestType()).autoBind();
        assertThat(result).isNotNull();
    }

    @Test
    void test_auto_bind_happy_path_custom_arena() {
        final var result = pointer().toCFunc(TEST_FUNC).of(new TestType()).autoBindTo(arena);

        assertThat(result.scope().isAlive()).isTrue();

        arena.close();

        assertThat(result.scope().isAlive()).isFalse();
    }

    @Test
    void test_auto_bind_no_args_no_return_type() {
        final var result = pointer().toCFunc(NO_ARGS).of(new TestType()).autoBind();
        assertThat(result).isNotNull();
    }

    @Test
    void test_auto_bind_Void_return_type() {
        assertThatThrownBy(() -> pointer().toCFunc(RETURN_TYPE_VOID).of(new TestType()).autoBind())
            .isInstanceOf(FluffyMemoryException.class)
            .hasMessage("Return type " + Void.class.getCanonicalName() + " is unsupported. Use "
                + void.class.getCanonicalName());
    }

    @Test
    void when_auto_bind_overloaded_method_then_throw_exception() {
        final var methodName = "overloadedMethod";
        assertThatThrownBy(() -> pointer().toCFunc(methodName).of(new MethodOverloaded()).autoBind())
            .isInstanceOf(FluffyMemoryException.class)
            .hasMessage("Cannot autobind overloaded method '" + methodName + "'. Please perform manual bind.");
    }

    @Test
    void when_auto_bind_unknown_method_then_throw_exception() {
        final var methodName = "unknownMethod";
        assertThatThrownBy(() -> pointer().toCFunc(methodName).of(this).autoBind())
            .isInstanceOf(FluffyMemoryException.class)
            .hasMessage("Could not find method '" + methodName + "' in type " + this.getClass().getCanonicalName());
    }

    @Test
    void when_auto_bind_method_with_unsupported_args_then_throw_exception() {
        final var methodName = "unsupportedArgs";
        assertThatThrownBy(() -> pointer().toCFunc(methodName).of(new TestType()).autoBind())
            .isInstanceOf(FluffyMemoryException.class)
            .hasMessage("Method '" + methodName + "' of type " + TestType.class.getCanonicalName()
                + " has unsupported argument types.")
            .hasRootCauseInstanceOf(FluffyMemoryException.class)
            .hasRootCauseMessage("Cannot provide native memory layout for JVM type java.lang.String");
    }

    @Test
    void when_auto_bind_method_with_unsupported_returnType_then_throw_exception() {
        final var methodName = "unsupportedReturnType";
        assertThatThrownBy(() -> pointer().toCFunc(methodName).of(new TestType()).autoBind())
            .isInstanceOf(FluffyMemoryException.class)
            .hasMessage("Method '" + methodName + "' of type " + TestType.class.getCanonicalName()
                + " has unsupported return type.")
            .hasRootCauseInstanceOf(FluffyMemoryException.class)
            .hasRootCauseMessage("Cannot provide native memory layout for JVM type java.lang.String");
    }

    @Test
    void when_auto_bind_synthetic_method_then_throw_exception() {
        final var methodName = "compare";
        assertThatThrownBy(() -> pointer().toCFunc(methodName).of(new BridgeMethodDemo()).autoBind())
            .isInstanceOf(FluffyMemoryException.class)
            .hasMessage("Cannot create function pointer to synthetic JVM methods.");
    }

    @Test
    void when_auto_bind_private_method_then_throw_exception() {
        final var methodName = "privateMethod";
        assertThatThrownBy(() -> pointer().toCFunc(methodName).of(new TestType()).autoBind())
            .isInstanceOf(FluffyMemoryException.class)
            .hasMessage("Cannot create function pointer to non accessible JVM methods.");
    }

    @Test
    void StagesClass_is_static_helper() {
        assertIsStaticHelper(FluffyMemoryFuncPointerBuilderStages.class);
    }

    @Test
    void toCFunc_returns_cfuncStage() {
        assertThat(pointer().toCFunc(TEST_FUNC)).isInstanceOf(CFuncStage.class);
    }

    @Test
    void toCFunc_of__returns_argsStage() {
        assertThat(pointer().toCFunc(TEST_FUNC).of(new TestType())).isInstanceOf(ArgsStage.class);
    }

    @Test
    void withArgs_uses_typeConv() {
        final var testArg = JAVA_CHAR;
        pointer().toFunc(TEST_FUNC).ofType(new TestType()).withTypeConverter(convMock).withArgs(testArg);

        verify(convMock).getJvmTypes(testArg);
    }

    @Test
    void autoBindTo_uses_typeConv() {
        final var convSpy = spy(new CDataTypeConverter());
        pointer().toFunc(TEST_FUNC).ofType(new TestType()).withTypeConverter(convSpy).autoBindTo(arena);

        verify(convSpy).getNativeTypes(any());
    }

    @Test
    void andReturnType_uses_typeConv() {
        final var testReturnType = JAVA_INT;
        pointer().toFunc(TEST_FUNC).ofType(new TestType()).withTypeConverter(convMock).withoutArgs()
            .andReturnType(testReturnType);

        verify(convMock).getJvmType(testReturnType);
    }

    @Test
    void toCFunc_does_not_accept_null() {
        assertNullArgNotAccepted(() -> pointer().toCFunc(null), "funcName");
    }

    @Test
    void toFunc_does_not_accept_null() {
        assertNullArgNotAccepted(() -> pointer().toFunc(null), "funcName");
    }


    @Test
    void builder_constructor_does_not_accept_null() {
        assertNullArgNotAccepted(() -> new FluffyMemoryFuncPointerBuilder(null), "funcName");
        assertNullArgNotAccepted(() -> new FluffyMemoryFuncPointerBuilder(null, convMock), "funcName");
        assertNullArgNotAccepted(() -> new FluffyMemoryFuncPointerBuilder("funcName", null), "conv");
    }

    @Test
    void of_does_not_accept_null() {
        assertNullArgNotAccepted(() -> pointer().toCFunc(TEST_FUNC).of(null), "receiver");
    }

    @Test
    void ofType_does_not_accept_null() {
        assertNullArgNotAccepted(() -> pointer().toFunc(TEST_FUNC).ofType(null), "receiver");
    }

    @Test
    void withTypeConverter_does_not_accept_null() {
        assertNullArgNotAccepted(() -> pointer().toFunc(TEST_FUNC).ofType(new TestType()).withTypeConverter(null),
            "conv");
    }

    @Test
    void withArgs_does_not_accept_null() {
        assertNullArgNotAccepted(() -> pointer().toCFunc(TEST_FUNC).of(new TestType()).withArgs((MemoryLayout[]) null),
            "args");
    }

    @Test
    void autoBindTo_does_not_accept_null() {
        assertNullArgNotAccepted(() -> pointer().toCFunc(TEST_FUNC).of(new TestType()).autoBindTo(null), "arena");
    }

    @Test
    void andReturnType_does_not_accept_null() {
        assertNullArgNotAccepted(
            () -> pointer().toCFunc(TEST_FUNC).of(new TestType()).withoutArgs().andReturnType(null), "returnType");
    }

    @Test
    void bindTo_does_not_accept_null() {
        assertNullArgNotAccepted(
            () -> pointer().toCFunc(TEST_FUNC).of(TEST_FUNC).withoutArgs().andNoReturnType().bindTo(null), "arena");
    }

    // Will only be used to bind a MethodType in a test
    @SuppressWarnings("unused")
    private static final class MethodOverloaded {
        void overloadedMethod() {}

        int overloadedMethod(final int arg) {
            return -1;
        }
    }

    // Will only be used to bind a MethodType in a test
    @SuppressWarnings("unused")
    private static final class TestType {
        private final String field = "a synthetic getter will be created for this field as soon as it is accessed";

        int testFunc(final byte arg) {
            return 0;
        }

        Void returnTypeIsObjectVoid() {
            return null;
        }

        void noArgs() {}

        void unsupportedArgs(final String unsupportedArg) {}

        String unsupportedReturnType() {
            return null;
        }

        private void privateMethod() {}
    }

    public class BridgeMethodDemo implements Comparator<Integer> {
        @Override
        public int compare(final Integer o1, final Integer o2) {
            return 0;
        }
    }
}
