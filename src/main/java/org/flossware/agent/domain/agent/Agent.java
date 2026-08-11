package org.flossware.agent.domain.agent;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import org.flossware.agent.domain.id.Identifiers.AgentId;
import org.flossware.agent.domain.id.Identifiers.ModelId;

/** Configured actor definition (not a runtime process). */
public final class Agent {
    private final AgentId id;
    private final String name;
    private final String description;
    private final ModelId preferredModelId;
    private final AgentStatus status;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final Map<String, String> configuration;

    private Agent(AgentId id, String name, String description, ModelId preferredModelId,
                  AgentStatus status, Instant createdAt, Instant updatedAt,
                  Map<String, String> configuration) {
        this.id = Objects.requireNonNull(id, "id");
        Objects.requireNonNull(name, "name");
        if (name.isBlank()) throw new IllegalArgumentException("name must not be blank");
        this.name = name;
        this.description = description == null ? "" : description;
        this.preferredModelId = preferredModelId;
        this.status = Objects.requireNonNull(status, "status");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt");
        this.configuration = Collections.unmodifiableMap(Objects.requireNonNull(configuration, "configuration"));
    }

    public static Agent create(String name, String description) {
        Instant now = Instant.now();
        return new Agent(AgentId.random(), name, description, null, AgentStatus.ACTIVE, now, now, Map.of());
    }

    public static Agent restore(AgentId id, String name, String description, ModelId preferredModelId,
                                AgentStatus status, Instant createdAt, Instant updatedAt,
                                Map<String, String> configuration) {
        return new Agent(id, name, description, preferredModelId, status, createdAt, updatedAt,
                Map.copyOf(configuration));
    }

    public AgentId id() { return id; }
    public String name() { return name; }
    public String description() { return description; }
    public ModelId preferredModelId() { return preferredModelId; }
    public AgentStatus status() { return status; }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }
    public Map<String, String> configuration() { return configuration; }

    public Agent disable() { return transitionTo(AgentStatus.DISABLED); }
    public Agent enable() { return transitionTo(AgentStatus.ACTIVE); }
    public Agent archive() { return transitionTo(AgentStatus.ARCHIVED); }

    public Agent transitionTo(AgentStatus target) {
        if (!status.canTransitionTo(target)) {
            throw new IllegalStateException(
                    "Cannot transition agent from " + status + " to " + target);
        }
        return new Agent(id, name, description, preferredModelId, target, createdAt, Instant.now(), configuration);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Agent other)) return false;
        return id.equals(other.id);
    }
    @Override public int hashCode() { return id.hashCode(); }
}
