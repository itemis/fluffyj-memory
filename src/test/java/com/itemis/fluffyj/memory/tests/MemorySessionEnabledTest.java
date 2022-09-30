package com.itemis.fluffyj.memory.tests;

import static com.itemis.fluffyj.exceptions.ThrowablePrettyfier.pretty;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.lang.foreign.MemorySession;

public abstract class MemorySessionEnabledTest {

    protected MemorySession session;

    @BeforeEach
    void setUpSession() {
        session = MemorySession.openConfined();
    }

    @AfterEach
    protected void tearDownSession() {
        if (session != null && session.isAlive()) {
            try {
                session.close();
            } catch (Exception e) {
                System.err.println("WARN: Could not close memory session: " + pretty(e));
            }
        }
    }

}
