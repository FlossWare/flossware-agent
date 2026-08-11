package org.flossware.agent.domain.session;

import static org.junit.jupiter.api.Assertions.*;

import org.flossware.agent.domain.agent.Agent;
import org.flossware.agent.domain.id.Identifiers.AgentId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

class SessionLifecycleTest {

    private static Session newSession() {
        return Session.create(Agent.create("agent", "d").id());
    }

    @Test
    void createStartsInCreated() {
        assertEquals(SessionState.CREATED, newSession().state());
    }

    @Test
    void createdCanActivateOrClose() {
        Session s = newSession();
        assertDoesNotThrow(s::activate);
        assertDoesNotThrow(s::close);
        assertThrows(IllegalStateException.class, s::suspend);
    }

    @Test
    void activeCanSuspendOrClose() {
        Session s = newSession().activate();
        assertTrue(s.state().isResumable());
        assertDoesNotThrow(s::suspend);
        assertDoesNotThrow(s::close);
        assertThrows(IllegalStateException.class, s::activate);
    }

    @Test
    void suspendedCanActivateOrClose() {
        Session s = newSession().activate().suspend();
        assertTrue(s.state().isResumable());
        assertDoesNotThrow(s::activate);
        assertDoesNotThrow(s::close);
        assertThrows(IllegalStateException.class, s::suspend);
    }

    @Test
    void closedIsTerminalAndRejectsAllTransitions() {
        Session s = newSession().close();
        assertTrue(s.state().isTerminal());
        assertThrows(IllegalStateException.class, s::activate);
        assertThrows(IllegalStateException.class, s::suspend);
        assertThrows(IllegalStateException.class, s::close);
        assertThrows(IllegalStateException.class,
                () -> s.transitionTo(SessionState.CREATED));
    }

    @ParameterizedTest
    @CsvSource({
            "CREATED, ACTIVE, true",
            "CREATED, CLOSED, true",
            "CREATED, SUSPENDED, false",
            "CREATED, CREATED, false",
            "ACTIVE, SUSPENDED, true",
            "ACTIVE, CLOSED, true",
            "ACTIVE, CREATED, false",
            "ACTIVE, ACTIVE, false",
            "SUSPENDED, ACTIVE, true",
            "SUSPENDED, CLOSED, true",
            "SUSPENDED, SUSPENDED, false",
            "SUSPENDED, CREATED, false",
            "CLOSED, ACTIVE, false",
            "CLOSED, SUSPENDED, false",
            "CLOSED, CLOSED, false",
            "CLOSED, CREATED, false"
    })
    void transitionMatrix(SessionState from, SessionState to, boolean allowed) {
        assertEquals(allowed, from.canTransitionTo(to));
    }

    @Test
    void transitionPreservesIdentityAndAgent() {
        Session s = newSession().activate();
        Session closed = s.close();
        assertEquals(s.id(), closed.id());
        assertEquals(s.agentId(), closed.agentId());
        assertEquals(s.createdAt(), closed.createdAt());
        assertTrue(closed.updatedAt().compareTo(s.updatedAt()) >= 0);
    }

    @Test
    void nullAgentIdRejected() {
        assertThrows(NullPointerException.class, () -> Session.create(null));
    }
}
