package org.flossware.agent.domain;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.flossware.agent.domain.agent.Agent;
import org.flossware.agent.domain.agent.AgentStatus;
import org.flossware.agent.domain.id.Identifiers.AgentId;
import org.flossware.agent.domain.id.Identifiers.InteractionId;
import org.flossware.agent.domain.id.Identifiers.MemoryId;
import org.flossware.agent.domain.id.Identifiers.SessionId;
import org.flossware.agent.domain.id.Identifiers.TaskId;
import org.flossware.agent.domain.interaction.Interaction;
import org.flossware.agent.domain.interaction.InteractionState;
import org.flossware.agent.domain.memory.Memory;
import org.flossware.agent.domain.memory.MemoryKind;
import org.flossware.agent.domain.message.Message;
import org.flossware.agent.domain.message.MessageRole;
import org.flossware.agent.domain.session.Session;
import org.flossware.agent.domain.session.SessionState;
import org.flossware.agent.domain.task.Task;
import org.flossware.agent.domain.task.TaskState;
import org.junit.jupiter.api.Test;

/** Restore factories must round-trip identity and field values for platform persistence. */
class RestoreSemanticsTest {

    @Test
    void sessionRestoreRoundTrip() {
        Session original = Session.create(Agent.create("a", "d").id()).activate();
        Session restored = Session.restore(
                original.id(), original.agentId(), original.state(),
                original.createdAt(), original.updatedAt(), original.metadata());
        assertEquals(original, restored);
        assertEquals(original.state(), restored.state());
        assertEquals(original.agentId(), restored.agentId());
        assertEquals(original.createdAt(), restored.createdAt());
        assertEquals(original.updatedAt(), restored.updatedAt());
    }

    @Test
    void sessionRestoreWithExplicitFields() {
        SessionId sid = SessionId.of("sess-1");
        AgentId aid = AgentId.of("agent-1");
        Instant created = Instant.parse("2024-01-01T00:00:00Z");
        Instant updated = Instant.parse("2024-01-02T00:00:00Z");
        Session restored = Session.restore(sid, aid, SessionState.SUSPENDED,
                created, updated, Map.of("k", "v"));
        assertEquals(sid, restored.id());
        assertEquals(SessionState.SUSPENDED, restored.state());
        assertEquals("v", restored.metadata().get("k"));
        assertEquals(created, restored.createdAt());
    }

    @Test
    void interactionRestoreRoundTrip() {
        Interaction original = Interaction.start(SessionId.random())
                .appendMessage(Message.of(MessageRole.USER, "hi"));
        Interaction restored = Interaction.restore(
                original.id(), original.sessionId(), original.state(),
                original.messages(), original.startedAt(), original.completedAt(),
                original.metadata());
        assertEquals(original, restored);
        assertEquals(1, restored.messages().size());
        assertEquals(original.messages().get(0), restored.messages().get(0));
    }

    @Test
    void interactionRestoreTerminal() {
        Instant started = Instant.parse("2024-06-01T12:00:00Z");
        Instant completed = Instant.parse("2024-06-01T12:01:00Z");
        Interaction restored = Interaction.restore(
                InteractionId.of("ix-1"), SessionId.of("s-1"), InteractionState.FAILED,
                List.of(), started, completed, Map.of());
        assertEquals(InteractionState.FAILED, restored.state());
        assertEquals(completed, restored.completedAt());
        assertThrows(IllegalStateException.class,
                () -> restored.appendMessage(Message.of(MessageRole.USER, "nope")));
    }

    @Test
    void agentRestoreRoundTrip() {
        Agent original = Agent.create("n", "d").disable();
        Agent restored = Agent.restore(
                original.id(), original.name(), original.description(),
                original.preferredModelId(), original.status(),
                original.createdAt(), original.updatedAt(), original.configuration());
        assertEquals(original, restored);
        assertEquals(AgentStatus.DISABLED, restored.status());
    }

    @Test
    void taskRestoreRoundTrip() {
        Task original = Task.create(AgentId.of("a1"), "job").start().complete("done");
        Task restored = Task.restore(
                original.id(), original.agentId(), original.sessionId().orElse(null),
                original.description(), original.state(), original.createdAt(),
                original.updatedAt(), original.result().orElse(null), original.metadata());
        assertEquals(original, restored);
        assertEquals("done", restored.result().orElseThrow());
        assertEquals(TaskState.COMPLETED, restored.state());
    }

    @Test
    void memoryRestoreRoundTrip() {
        Memory original = Memory.create(AgentId.of("a1"), MemoryKind.LONG_TERM, "fact");
        Memory restored = Memory.restore(
                original.id(), original.agentId(), original.sessionId().orElse(null),
                original.kind(), original.content(), original.createdAt(),
                original.updatedAt(), original.metadata());
        assertEquals(original, restored);
        assertEquals("fact", restored.content());
    }

    @Test
    void messageEqualityByIdAcrossReconstruction() {
        Message original = Message.of(MessageRole.SYSTEM, "sys");
        Message reconstructed = Message.of(
                original.id(), original.role(), original.content(),
                original.createdAt(), original.metadata());
        assertEquals(original, reconstructed);
        assertEquals(original.hashCode(), reconstructed.hashCode());
    }
}
