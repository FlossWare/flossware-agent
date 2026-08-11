package org.flossware.agent.domain;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.flossware.agent.domain.agent.Agent;
import org.flossware.agent.domain.context.Context;
import org.flossware.agent.domain.event.DomainEvent;
import org.flossware.agent.domain.event.EventType;
import org.flossware.agent.domain.id.Identifiers.AgentId;
import org.flossware.agent.domain.id.Identifiers.SessionId;
import org.flossware.agent.domain.interaction.Interaction;
import org.flossware.agent.domain.memory.Memory;
import org.flossware.agent.domain.memory.MemoryKind;
import org.flossware.agent.domain.message.Message;
import org.flossware.agent.domain.message.MessageRole;
import org.flossware.agent.domain.model.ModelRequest;
import org.flossware.agent.domain.id.Identifiers.ModelId;
import org.flossware.agent.domain.session.Session;
import org.flossware.agent.domain.tool.ToolDefinition;
import org.junit.jupiter.api.Test;

/** Returned collections and maps must be unmodifiable snapshots. */
class ImmutabilityContractTest {

    @Test
    void interactionMessagesUnmodifiable() {
        Interaction i = Interaction.start(SessionId.random())
                .appendMessage(Message.of(MessageRole.USER, "hi"));
        assertThrows(UnsupportedOperationException.class,
                () -> i.messages().add(Message.of(MessageRole.ASSISTANT, "no")));
        assertThrows(UnsupportedOperationException.class,
                () -> i.messages().clear());
    }

    @Test
    void contextCollectionsUnmodifiable() {
        Context ctx = Context.of(
                List.of(Message.of(MessageRole.USER, "u")),
                List.of(Memory.create(AgentId.of("a"), MemoryKind.WORKING, "m")),
                List.of(ToolDefinition.of("t", "d", "{}")),
                Map.of("k", "v"));
        assertThrows(UnsupportedOperationException.class, () -> ctx.messages().add(null));
        assertThrows(UnsupportedOperationException.class, () -> ctx.memories().clear());
        assertThrows(UnsupportedOperationException.class, () -> ctx.tools().remove(0));
        assertThrows(UnsupportedOperationException.class, () -> ctx.attributes().put("x", "y"));
    }

    @Test
    void domainEventPayloadUnmodifiable() {
        Map<String, String> source = new HashMap<>();
        source.put("a", "1");
        DomainEvent e = DomainEvent.of(EventType.SESSION_STARTED, AgentId.of("a"),
                SessionId.of("s"), source);
        assertThrows(UnsupportedOperationException.class, () -> e.payload().put("b", "2"));
        source.put("c", "3"); // mutating source after construction must not affect event
        assertFalse(e.payload().containsKey("c"));
        assertEquals(1, e.payload().size());
    }

    @Test
    void sessionMetadataUnmodifiable() {
        Session s = Session.create(Agent.create("a", "d").id());
        assertThrows(UnsupportedOperationException.class,
                () -> s.metadata().put("x", "y"));
    }

    @Test
    void agentConfigurationUnmodifiable() {
        Agent a = Agent.create("a", "d");
        assertThrows(UnsupportedOperationException.class,
                () -> a.configuration().put("x", "y"));
    }

    @Test
    void modelRequestCollectionsUnmodifiable() {
        ModelRequest req = ModelRequest.builder(ModelId.of("m"))
                .messages(List.of(Message.of(MessageRole.USER, "h")))
                .tools(List.of(ToolDefinition.of("t", "d", "{}")))
                .parameters(Map.of("p", "v"))
                .build();
        assertThrows(UnsupportedOperationException.class, () -> req.messages().add(null));
        assertThrows(UnsupportedOperationException.class, () -> req.tools().clear());
        assertThrows(UnsupportedOperationException.class, () -> req.parameters().put("q", "w"));
    }

    @Test
    void transitionsDoNotMutateOriginal() {
        Session s = Session.create(Agent.create("a", "d").id());
        Session active = s.activate();
        assertNotSame(s, active);
        assertNotEquals(s.state(), active.state());
        assertEquals(org.flossware.agent.domain.session.SessionState.CREATED, s.state());
    }
}
