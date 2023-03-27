package com.itemis.fluffyj.memory.tests;

import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.lang.foreign.Arena;
import java.lang.foreign.SegmentScope;

public abstract class MemoryScopeEnabledTest {

    protected Arena arena;
    protected SegmentScope scope;

    @BeforeEach
    void setUpScope() {
        arena = Arena.openConfined();
        scope = arena.scope();
    }

    @AfterEach
    void tearDownScope() {
        if (arena != null && scope != null && scope.isAlive()) {
            try {
                arena.close();
            } catch (Exception e) {
                fail("Could not close test arena.", e);
            }
        }
    }
}
