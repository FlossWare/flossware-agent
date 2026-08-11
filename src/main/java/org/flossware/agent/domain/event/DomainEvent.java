package org.flossware.agent.domain.event;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.flossware.agent.domain.id.Identifiers.AgentId;
import org.flossware.agent.domain.id.Identifiers.EventId;
import org.flossware.agent.domain.id.Identifiers.SessionId;

/** Immutable fact describing a meaningful domain occurrence. */
public final class DomainEvent {
    private final EventId id;
    private final EventType type;
    private final AgentId agentId;
    private final SessionId sessionId;
    private final Instant occurredAt;
    private final Map<String, String> payload;

    private DomainEvent(EventId id, EventType type, AgentId agentId, SessionId sessionId,
                        Instant occurredAt, Map<String, String> payload) {
        this.id = Objects.requireNonNull(id, "id");
        this.type = Objects.requireNonNull(type, "type");
        this.agentId = agentId;
        this.sessionId = sessionId;
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
        this.payload = Collections.unmodifiableMap(Objects.requireNonNull(payload, "payload"));
    }

    public static DomainEvent of(EventType type, AgentId agentId, SessionId sessionId,
                                 Map<String, String> payload) {
        return new DomainEvent(EventId.random(), type, agentId, sessionId, Instant.now(), Map.copyOf(payload));
    }

    public static DomainEvent of(EventType type) {
        return new DomainEvent(EventId.random(), type, null, null, Instant.now(), Map.of());
    }

    public EventId id() { return id; }
    public EventType type() { return type; }
    public Optional<AgentId> agentId() { return Optional.ofNullable(agentId); }
    public Optional<SessionId> sessionId() { return Optional.ofNullable(sessionId); }
    public Instant occurredAt() { return occurredAt; }
    public Map<String, String> payload() { return payload; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DomainEvent other)) return false;
        return id.equals(other.id);
    }
    @Override public int hashCode() { return id.hashCode(); }
}
