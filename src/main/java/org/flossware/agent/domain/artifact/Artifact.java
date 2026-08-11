package org.flossware.agent.domain.artifact;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.flossware.agent.domain.id.Identifiers.AgentId;
import org.flossware.agent.domain.id.Identifiers.ArtifactId;
import org.flossware.agent.domain.id.Identifiers.InteractionId;
import org.flossware.agent.domain.id.Identifiers.SessionId;

/** Durable output referenced by an opaque storage locator. */
public final class Artifact {
    private final ArtifactId id;
    private final AgentId agentId;
    private final SessionId sessionId;
    private final InteractionId interactionId;
    private final String name;
    private final String mediaType;
    private final String locator;
    private final Instant createdAt;
    private final Map<String, String> metadata;

    private Artifact(ArtifactId id, AgentId agentId, SessionId sessionId, InteractionId interactionId,
                     String name, String mediaType, String locator, Instant createdAt,
                     Map<String, String> metadata) {
        this.id = Objects.requireNonNull(id, "id");
        this.agentId = Objects.requireNonNull(agentId, "agentId");
        this.sessionId = sessionId;
        this.interactionId = interactionId;
        Objects.requireNonNull(name, "name");
        if (name.isBlank()) throw new IllegalArgumentException("name must not be blank");
        this.name = name;
        this.mediaType = mediaType == null ? "application/octet-stream" : mediaType;
        Objects.requireNonNull(locator, "locator");
        if (locator.isBlank()) throw new IllegalArgumentException("locator must not be blank");
        this.locator = locator;
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
        this.metadata = Collections.unmodifiableMap(Objects.requireNonNull(metadata, "metadata"));
    }

    public static Artifact create(AgentId agentId, String name, String mediaType, String locator) {
        return new Artifact(ArtifactId.random(), agentId, null, null, name, mediaType, locator,
                Instant.now(), Map.of());
    }

    public ArtifactId id() { return id; }
    public AgentId agentId() { return agentId; }
    public Optional<SessionId> sessionId() { return Optional.ofNullable(sessionId); }
    public Optional<InteractionId> interactionId() { return Optional.ofNullable(interactionId); }
    public String name() { return name; }
    public String mediaType() { return mediaType; }
    public String locator() { return locator; }
    public Instant createdAt() { return createdAt; }
    public Map<String, String> metadata() { return metadata; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Artifact other)) return false;
        return id.equals(other.id);
    }
    @Override public int hashCode() { return id.hashCode(); }
}
