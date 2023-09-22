package com.itemis.fluffyj.memory.tests;

import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment.Scope;

public abstract class ArenafiedTest {

    protected Arena arena;
    protected Scope scope;

    @BeforeEach
    void setUpArenaAndScope() {
        arena = Arena.ofShared();
        scope = arena.scope();
    }

    @AfterEach
    void tearDownScope() {
        if (arena != null && scope != null && scope.isAlive()) {
            try {
                arena.close();
            } catch (final Exception e) {
                fail("Could not close test arena.", e);
            }
        }
    }
}
