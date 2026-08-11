package org.flossware.agent.domain.message;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import org.flossware.agent.domain.id.Identifiers.MessageId;

/** Immutable typed communication associated with an interaction. */
public final class Message {
    private final MessageId id;
    private final MessageRole role;
    private final String content;
    private final Instant createdAt;
    private final Map<String, String> metadata;

    private Message(MessageId id, MessageRole role, String content, Instant createdAt,
                    Map<String, String> metadata) {
        this.id = Objects.requireNonNull(id, "id");
        this.role = Objects.requireNonNull(role, "role");
        this.content = Objects.requireNonNull(content, "content");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
        this.metadata = Collections.unmodifiableMap(Objects.requireNonNull(metadata, "metadata"));
    }

    public static Message of(MessageRole role, String content) {
        return new Message(MessageId.random(), role, content, Instant.now(), Map.of());
    }

    public static Message of(MessageId id, MessageRole role, String content, Instant createdAt,
                             Map<String, String> metadata) {
        return new Message(id, role, content, createdAt, Map.copyOf(metadata));
    }

    public MessageId id() { return id; }
    public MessageRole role() { return role; }
    public String content() { return content; }
    public Instant createdAt() { return createdAt; }
    public Map<String, String> metadata() { return metadata; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Message other)) return false;
        return id.equals(other.id);
    }
    @Override public int hashCode() { return id.hashCode(); }
    @Override public String toString() {
        return "Message{id=" + id.value() + ", role=" + role + "}";
    }
}
