package com.itemis.fluffyj.memory;

import static com.itemis.fluffyj.memory.FluffyMemory.pointer;
import static com.itemis.fluffyj.memory.FluffyMemory.segment;
import static com.itemis.fluffyj.memory.FluffyMemory.wrap;
import static jdk.incubator.foreign.CLinker.C_LONG;
import static jdk.incubator.foreign.CLinker.C_LONG_LONG;
import static jdk.incubator.foreign.CLinker.C_POINTER;
import static jdk.incubator.foreign.CLinker.toCString;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.assertj.core.api.Assertions.assertThat;

import com.itemis.fluffyj.memory.internal.PointerOfString;
import com.itemis.fluffyj.memory.internal.StringSegment;
import com.itemis.fluffyj.memory.tests.MemoryScopedTest;

import org.junit.jupiter.api.Test;

import java.lang.invoke.MethodType;
import java.lang.invoke.WrongMethodTypeException;
import java.util.Random;

import jdk.incubator.foreign.CLinker;
import jdk.incubator.foreign.FunctionDescriptor;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.SymbolLookup;

public class RealWorldScenariosTest extends MemoryScopedTest {

    private static final int MAX_RND_STR_LENGTH = 100;
    private static final FunctionDescriptor STRLEN_C_FUNC_DESCR =
        System.getProperty("os.name").toLowerCase().contains("win") ? FunctionDescriptor.of(C_LONG_LONG, C_POINTER)
            : FunctionDescriptor.of(C_LONG, C_POINTER);
    private static final MethodType STRLEN_JAVA_METHOD_DESCR = MethodType.methodType(long.class, MemoryAddress.class);

    @Test
    void can_call_strlen_with_address_of_string_seg() {
        var expectedStringLength = new Random().nextInt(MAX_RND_STR_LENGTH);
        var rndStr = randomAlphanumeric(expectedStringLength);

        var cStr = new StringSegment(rndStr, scope);
        var ptrCStr = new PointerOfString(cStr.address(), scope);

        assertThat(strlen(cStr.address())).isEqualTo(expectedStringLength);
        assertThat(strlen(ptrCStr.getValue())).isEqualTo(expectedStringLength);
    }

    @Test
    void can_call_strlen_with_results_of_fluffy_memory_builder() {
        var expectedStringLength = new Random().nextInt(MAX_RND_STR_LENGTH);
        var rndStr = randomAlphanumeric(expectedStringLength);

        var cStr = segment().of(rndStr).allocate(scope);
        var ptrCStr = pointer().to(cStr).allocate(scope);
        var wrappedStrSeg = wrap(toCString(rndStr, scope)).as(String.class);

        assertThat(strlen(cStr.address())).isEqualTo(expectedStringLength);
        assertThat(strlen(ptrCStr.getValue())).isEqualTo(expectedStringLength);
        assertThat(strlen(wrappedStrSeg.address())).isEqualTo(expectedStringLength);
    }

    private long strlen(MemoryAddress pointerToString) {
        var linker = CLinker.getInstance();
        var cLib = CLinker.systemLookup();
        var strlenCFuncRef = loadSymbol(cLib, "strlen");
        var strlenJavaMethodRef = linker.downcallHandle(strlenCFuncRef, STRLEN_JAVA_METHOD_DESCR, STRLEN_C_FUNC_DESCR);

        long result = -1;
        try {
            result = (long) strlenJavaMethodRef.invokeExact(pointerToString);
        } catch (WrongMethodTypeException e) {
            throw e;
        } catch (Throwable t) {
            var errMsg = t.getMessage() == null ? "No further information" : t.getMessage();
            throw new RuntimeException("Calling the library function caused a problem: " + t.getClass().getSimpleName() + ": " + errMsg, t);
        }

        return result;
    }

    private static MemoryAddress loadSymbol(SymbolLookup lookup, String symbolName) {
        return lookup.lookup(symbolName)
            .orElseThrow(() -> new RuntimeException("Could not find symbol '" + symbolName + "' in library '" + lookup.toString() + "'."));
    }
}
