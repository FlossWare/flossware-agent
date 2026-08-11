package org.flossware.agent.domain.memory;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.flossware.agent.domain.id.Identifiers.AgentId;
import org.flossware.agent.domain.id.Identifiers.MemoryId;
import org.flossware.agent.domain.id.Identifiers.SessionId;

/** Durable information intentionally retained for later recall. */
public final class Memory {
    private final MemoryId id;
    private final AgentId agentId;
    private final SessionId sessionId;
    private final MemoryKind kind;
    private final String content;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final Map<String, String> metadata;

    private Memory(MemoryId id, AgentId agentId, SessionId sessionId, MemoryKind kind, String content,
                   Instant createdAt, Instant updatedAt, Map<String, String> metadata) {
        this.id = Objects.requireNonNull(id, "id");
        this.agentId = Objects.requireNonNull(agentId, "agentId");
        this.sessionId = sessionId;
        this.kind = Objects.requireNonNull(kind, "kind");
        this.content = Objects.requireNonNull(content, "content");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt");
        this.metadata = Collections.unmodifiableMap(Objects.requireNonNull(metadata, "metadata"));
    }

    public static Memory create(AgentId agentId, MemoryKind kind, String content) {
        Instant now = Instant.now();
        return new Memory(MemoryId.random(), agentId, null, kind, content, now, now, Map.of());
    }

    public static Memory restore(MemoryId id, AgentId agentId, SessionId sessionId, MemoryKind kind,
                                 String content, Instant createdAt, Instant updatedAt,
                                 Map<String, String> metadata) {
        return new Memory(id, agentId, sessionId, kind, content, createdAt, updatedAt, Map.copyOf(metadata));
    }

    public MemoryId id() { return id; }
    public AgentId agentId() { return agentId; }
    public Optional<SessionId> sessionId() { return Optional.ofNullable(sessionId); }
    public MemoryKind kind() { return kind; }
    public String content() { return content; }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }
    public Map<String, String> metadata() { return metadata; }

    public Memory withContent(String newContent) {
        return new Memory(id, agentId, sessionId, kind, newContent, createdAt, Instant.now(), metadata);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Memory other)) return false;
        return id.equals(other.id);
    }
    @Override public int hashCode() { return id.hashCode(); }
}
