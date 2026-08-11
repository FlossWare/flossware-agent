package org.flossware.agent.domain.agent;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class AgentLifecycleTest {

    @Test
    void createIsActiveAndUsable() {
        Agent a = Agent.create("coder", "writes code");
        assertEquals(AgentStatus.ACTIVE, a.status());
        assertTrue(a.status().isUsable());
        assertEquals("coder", a.name());
    }

    @Test
    void activeMayDisableOrArchive() {
        Agent a = Agent.create("a", "d");
        assertDoesNotThrow(a::disable);
        assertDoesNotThrow(a::archive);
        assertThrows(IllegalStateException.class, a::enable);
    }

    @Test
    void disabledMayEnableOrArchive() {
        Agent d = Agent.create("a", "d").disable();
        assertFalse(d.status().isUsable());
        assertDoesNotThrow(d::enable);
        assertDoesNotThrow(d::archive);
        assertThrows(IllegalStateException.class, d::disable);
    }

    @Test
    void archivedIsTerminal() {
        Agent a = Agent.create("a", "d").archive();
        assertThrows(IllegalStateException.class, a::enable);
        assertThrows(IllegalStateException.class, a::disable);
        assertThrows(IllegalStateException.class, a::archive);
    }

    @ParameterizedTest
    @CsvSource({
            "ACTIVE, DISABLED, true",
            "ACTIVE, ARCHIVED, true",
            "ACTIVE, ACTIVE, false",
            "DISABLED, ACTIVE, true",
            "DISABLED, ARCHIVED, true",
            "DISABLED, DISABLED, false",
            "ARCHIVED, ACTIVE, false",
            "ARCHIVED, DISABLED, false",
            "ARCHIVED, ARCHIVED, false"
    })
    void transitionMatrix(AgentStatus from, AgentStatus to, boolean allowed) {
        assertEquals(allowed, from.canTransitionTo(to));
    }

    @Test
    void transitionPreservesIdentity() {
        Agent a = Agent.create("n", "d");
        Agent disabled = a.disable();
        assertEquals(a.id(), disabled.id());
        assertEquals(a.name(), disabled.name());
        assertEquals(a.createdAt(), disabled.createdAt());
    }

    @Test
    void blankOrNullNameRejected() {
        assertThrows(IllegalArgumentException.class, () -> Agent.create("", "d"));
        assertThrows(IllegalArgumentException.class, () -> Agent.create("   ", "d"));
        assertThrows(NullPointerException.class, () -> Agent.create(null, "d"));
    }
}
