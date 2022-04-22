package com.itemis.fluffyj.memory.tests;

import static com.itemis.fluffyj.exceptions.ThrowablePrettyfier.pretty;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import jdk.incubator.foreign.ResourceScope;

public abstract class MemoryScopedTest {

    protected ResourceScope scope;

    @BeforeEach
    void setUpScope() {
        scope = ResourceScope.newConfinedScope();
    }

    @AfterEach
    protected void tearDownScope() {
        if (scope != null && scope.isAlive()) {
            try {
                scope.close();
            } catch (Exception e) {
                System.err.println("WARN: Could not close resource scope: " + pretty(e));
            }
        }
    }

}
