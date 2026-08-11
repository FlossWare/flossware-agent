package org.flossware.agent.domain.task;

import static org.junit.jupiter.api.Assertions.*;

import org.flossware.agent.domain.agent.Agent;
import org.flossware.agent.domain.id.Identifiers.AgentId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class TaskLifecycleTest {

    private static Task pending() {
        return Task.create(Agent.create("a", "d").id(), "do work");
    }

    @Test
    void createStartsPendingWithoutResult() {
        Task t = pending();
        assertEquals(TaskState.PENDING, t.state());
        assertTrue(t.result().isEmpty());
    }

    @Test
    void pendingMayStartOrCancel() {
        assertDoesNotThrow(() -> pending().start());
        assertDoesNotThrow(() -> pending().cancel());
        assertThrows(IllegalStateException.class, () -> pending().complete("x"));
        assertThrows(IllegalStateException.class, () -> pending().fail("x"));
    }

    @Test
    void runningMayCompleteFailOrCancel() {
        Task running = pending().start();
        assertEquals(TaskState.RUNNING, running.state());
        assertEquals("ok", running.complete("ok").result().orElseThrow());
        assertEquals("boom", pending().start().fail("boom").result().orElseThrow());
        assertTrue(pending().start().cancel().result().isEmpty());
        assertThrows(IllegalStateException.class, running::start);
    }

    @Test
    void terminalRejectsAllTransitions() {
        for (Task terminal : new Task[] {
                pending().start().complete("done"),
                pending().start().fail("err"),
                pending().cancel() }) {
            assertTrue(terminal.state().isTerminal());
            assertThrows(IllegalStateException.class, terminal::start);
            assertThrows(IllegalStateException.class, () -> terminal.complete("x"));
            assertThrows(IllegalStateException.class, () -> terminal.fail("x"));
            assertThrows(IllegalStateException.class, terminal::cancel);
        }
    }

    @ParameterizedTest
    @CsvSource({
            "PENDING, RUNNING, true",
            "PENDING, CANCELLED, true",
            "PENDING, COMPLETED, false",
            "PENDING, FAILED, false",
            "PENDING, PENDING, false",
            "RUNNING, COMPLETED, true",
            "RUNNING, FAILED, true",
            "RUNNING, CANCELLED, true",
            "RUNNING, PENDING, false",
            "RUNNING, RUNNING, false",
            "COMPLETED, RUNNING, false",
            "FAILED, CANCELLED, false",
            "CANCELLED, PENDING, false"
    })
    void transitionMatrix(TaskState from, TaskState to, boolean allowed) {
        assertEquals(allowed, from.canTransitionTo(to));
    }

    @Test
    void transitionPreservesIdentity() {
        Task t = pending().start();
        Task done = t.complete("r");
        assertEquals(t.id(), done.id());
        assertEquals(t.agentId(), done.agentId());
        assertEquals(t.description(), done.description());
    }

    @Test
    void nullAgentOrDescriptionRejected() {
        AgentId id = Agent.create("a", "d").id();
        assertThrows(NullPointerException.class, () -> Task.create(null, "x"));
        assertThrows(NullPointerException.class, () -> Task.create(id, null));
    }
}
