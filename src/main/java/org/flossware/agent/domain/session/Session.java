package org.flossware.agent.domain.session;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import org.flossware.agent.domain.id.Identifiers.AgentId;
import org.flossware.agent.domain.id.Identifiers.SessionId;

/** Durable, resumable conversation/execution context. */
public final class Session {
    private final SessionId id;
    private final AgentId agentId;
    private final SessionState state;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final Map<String, String> metadata;

    private Session(SessionId id, AgentId agentId, SessionState state,
                    Instant createdAt, Instant updatedAt, Map<String, String> metadata) {
        this.id = Objects.requireNonNull(id, "id");
        this.agentId = Objects.requireNonNull(agentId, "agentId");
        this.state = Objects.requireNonNull(state, "state");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt");
        this.metadata = Collections.unmodifiableMap(Objects.requireNonNull(metadata, "metadata"));
    }

    public static Session create(AgentId agentId) {
        Instant now = Instant.now();
        return new Session(SessionId.random(), agentId, SessionState.CREATED, now, now, Map.of());
    }

    public static Session restore(SessionId id, AgentId agentId, SessionState state,
                                  Instant createdAt, Instant updatedAt, Map<String, String> metadata) {
        return new Session(id, agentId, state, createdAt, updatedAt, Map.copyOf(metadata));
    }

    public SessionId id() { return id; }
    public AgentId agentId() { return agentId; }
    public SessionState state() { return state; }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }
    public Map<String, String> metadata() { return metadata; }

    public Session activate() { return transitionTo(SessionState.ACTIVE); }
    public Session suspend() { return transitionTo(SessionState.SUSPENDED); }
    public Session close() { return transitionTo(SessionState.CLOSED); }

    public Session transitionTo(SessionState target) {
        if (!state.canTransitionTo(target)) {
            throw new IllegalStateException(
                    "Cannot transition session " + id.value() + " from " + state + " to " + target);
        }
        return new Session(id, agentId, target, createdAt, Instant.now(), metadata);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Session other)) return false;
        return id.equals(other.id);
    }
    @Override public int hashCode() { return id.hashCode(); }
    @Override public String toString() {
        return "Session{id=" + id.value() + ", state=" + state + "}";
    }
}
