package org.flossware.agent.domain;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;

import org.flossware.agent.domain.agent.Agent;
import org.flossware.agent.domain.agent.AgentStatus;
import org.flossware.agent.domain.artifact.Artifact;
import org.flossware.agent.domain.context.Context;
import org.flossware.agent.domain.event.DomainEvent;
import org.flossware.agent.domain.event.EventType;
import org.flossware.agent.domain.id.Identifiers.AgentId;
import org.flossware.agent.domain.id.Identifiers.InteractionId;
import org.flossware.agent.domain.id.Identifiers.ModelId;
import org.flossware.agent.domain.id.Identifiers.ProviderId;
import org.flossware.agent.domain.id.Identifiers.SessionId;
import org.flossware.agent.domain.interaction.Interaction;
import org.flossware.agent.domain.interaction.InteractionState;
import org.flossware.agent.domain.memory.Memory;
import org.flossware.agent.domain.memory.MemoryKind;
import org.flossware.agent.domain.message.Message;
import org.flossware.agent.domain.message.MessageRole;
import org.flossware.agent.domain.model.ModelDescriptor;
import org.flossware.agent.domain.model.ModelRequest;
import org.flossware.agent.domain.model.ModelResponse;
import org.flossware.agent.domain.session.Session;
import org.flossware.agent.domain.session.SessionState;
import org.flossware.agent.domain.task.Task;
import org.flossware.agent.domain.task.TaskState;
import org.flossware.agent.domain.tool.ToolDefinition;
import org.flossware.agent.domain.tool.ToolInvocation;
import org.junit.jupiter.api.Test;

class DomainModelTest {

    @Test
    void identifiersEqualityAndValidation() {
        assertEquals(AgentId.of("a1"), AgentId.of("a1"));
        assertNotEquals(AgentId.of("a"), AgentId.of("b"));
        assertThrows(IllegalArgumentException.class, () -> AgentId.of(""));
        assertThrows(NullPointerException.class, () -> AgentId.of(null));
        SessionId id = SessionId.random();
        assertEquals(id, SessionId.of(id.value()));
    }

    @Test
    void sessionLifecycle() {
        Session s = Session.create(Agent.create("t", "d").id());
        assertEquals(SessionState.CREATED, s.state());
        Session active = s.activate();
        assertTrue(active.state().isResumable());
        Session closed = active.suspend().close();
        assertTrue(closed.state().isTerminal());
        assertEquals(s.id(), closed.id());
        assertThrows(IllegalStateException.class, closed::activate);
        assertThrows(IllegalStateException.class, s::suspend);
    }

    @Test
    void sessionRestore() {
        Session original = Session.create(Agent.create("t", "d").id()).activate();
        Session restored = Session.restore(original.id(), original.agentId(), original.state(),
                original.createdAt(), original.updatedAt(), original.metadata());
        assertEquals(original, restored);
        assertEquals(SessionState.ACTIVE, restored.state());
    }

    @Test
    void interactionLifecycle() {
        Interaction i = Interaction.start(SessionId.random());
        assertEquals(InteractionState.STARTED, i.state());
        Message m = Message.of(MessageRole.USER, "hi");
        Interaction u = i.appendMessage(m);
        assertEquals(1, u.messages().size());
        Interaction c = u.complete();
        assertTrue(c.state().isTerminal());
        assertThrows(IllegalStateException.class, () -> c.appendMessage(Message.of(MessageRole.USER, "x")));
        assertThrows(UnsupportedOperationException.class,
                () -> u.messages().add(Message.of(MessageRole.ASSISTANT, "b")));
    }

    @Test
    void agentAndTaskLifecycle() {
        Agent a = Agent.create("coder", "desc");
        assertTrue(a.status().isUsable());
        Agent d = a.disable();
        assertEquals(AgentStatus.DISABLED, d.status());
        assertEquals(a.id(), d.id());
        assertThrows(IllegalStateException.class, d.archive()::enable);
        assertThrows(IllegalArgumentException.class, () -> Agent.create("", "d"));

        Task t = Task.create(a.id(), "job");
        assertEquals(TaskState.PENDING, t.state());
        Task done = t.start().complete("ok");
        assertEquals("ok", done.result().orElseThrow());
        assertThrows(IllegalStateException.class, done::start);
        assertThrows(IllegalStateException.class, () -> t.complete("x"));
    }

    @Test
    void messageMemoryContext() {
        Message a = Message.of(MessageRole.USER, "hi");
        Message b = Message.of(a.id(), MessageRole.USER, "hi", a.createdAt(), Map.of());
        assertEquals(a, b);

        Memory m = Memory.create(Agent.create("a", "d").id(), MemoryKind.LONG_TERM, "java");
        assertEquals("kotlin", m.withContent("kotlin").content());
        assertEquals(m.id(), m.withContent("kotlin").id());

        assertTrue(Context.empty().isEmpty());
        Context ctx = Context.of(
                List.of(a),
                List.of(m),
                List.of(ToolDefinition.of("search", "s", "{}")),
                Map.of("k", "v"));
        assertFalse(ctx.isEmpty());
        assertThrows(UnsupportedOperationException.class, () -> ctx.messages().add(a));
    }

    @Test
    void eventArtifactToolModel() {
        Agent agent = Agent.create("a", "d");
        DomainEvent e = DomainEvent.of(EventType.SESSION_STARTED, agent.id(), SessionId.random(), Map.of("s", "t"));
        assertEquals(EventType.SESSION_STARTED, e.type());

        assertThrows(IllegalArgumentException.class,
                () -> Artifact.create(agent.id(), "r", "text/plain", ""));
        assertEquals("s3://b/k", Artifact.create(agent.id(), "r.pdf", "application/pdf", "s3://b/k").locator());

        ToolDefinition def = ToolDefinition.of("echo", "e", "{}");
        ToolInvocation inv = ToolInvocation.start(def.id(), InteractionId.random(), "{}");
        assertFalse(inv.isComplete());
        assertTrue(inv.complete("ok", true).isComplete());

        ModelId mid = ModelId.of("m");
        assertEquals(128000, ModelDescriptor.of(mid, ProviderId.of("p"), "M", 128000, Map.of())
                .contextWindow().orElseThrow());
        ModelRequest req = ModelRequest.builder(mid)
                .messages(List.of(Message.of(MessageRole.USER, "h")))
                .temperature(0.2)
                .build();
        assertEquals(0.2, req.temperature().orElseThrow());
        assertEquals(MessageRole.ASSISTANT,
                ModelResponse.of(Message.of(MessageRole.ASSISTANT, "w")).message().role());
        assertThrows(IllegalArgumentException.class, () -> ToolDefinition.of("", "d", "{}"));
    }
}
