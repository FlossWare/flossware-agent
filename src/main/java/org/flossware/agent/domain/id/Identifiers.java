package org.flossware.agent.domain.id;

import java.util.Objects;
import java.util.UUID;

/** Strongly typed, immutable domain identifiers with value equality. */
public final class Identifiers {
    private Identifiers() {}

    public sealed interface DomainId permits AgentId, SessionId, InteractionId, MessageId,
            MemoryId, TaskId, ToolId, ModelId, ProviderId, EventId, ArtifactId {
        String value();
    }

    public record AgentId(String value) implements DomainId {
        public AgentId { requireNonBlank(value, "AgentId"); }
        public static AgentId random() { return new AgentId(UUID.randomUUID().toString()); }
        public static AgentId of(String value) { return new AgentId(value); }
    }
    public record SessionId(String value) implements DomainId {
        public SessionId { requireNonBlank(value, "SessionId"); }
        public static SessionId random() { return new SessionId(UUID.randomUUID().toString()); }
        public static SessionId of(String value) { return new SessionId(value); }
    }
    public record InteractionId(String value) implements DomainId {
        public InteractionId { requireNonBlank(value, "InteractionId"); }
        public static InteractionId random() { return new InteractionId(UUID.randomUUID().toString()); }
        public static InteractionId of(String value) { return new InteractionId(value); }
    }
    public record MessageId(String value) implements DomainId {
        public MessageId { requireNonBlank(value, "MessageId"); }
        public static MessageId random() { return new MessageId(UUID.randomUUID().toString()); }
        public static MessageId of(String value) { return new MessageId(value); }
    }
    public record MemoryId(String value) implements DomainId {
        public MemoryId { requireNonBlank(value, "MemoryId"); }
        public static MemoryId random() { return new MemoryId(UUID.randomUUID().toString()); }
        public static MemoryId of(String value) { return new MemoryId(value); }
    }
    public record TaskId(String value) implements DomainId {
        public TaskId { requireNonBlank(value, "TaskId"); }
        public static TaskId random() { return new TaskId(UUID.randomUUID().toString()); }
        public static TaskId of(String value) { return new TaskId(value); }
    }
    public record ToolId(String value) implements DomainId {
        public ToolId { requireNonBlank(value, "ToolId"); }
        public static ToolId of(String value) { return new ToolId(value); }
    }
    public record ModelId(String value) implements DomainId {
        public ModelId { requireNonBlank(value, "ModelId"); }
        public static ModelId of(String value) { return new ModelId(value); }
    }
    public record ProviderId(String value) implements DomainId {
        public ProviderId { requireNonBlank(value, "ProviderId"); }
        public static ProviderId of(String value) { return new ProviderId(value); }
    }
    public record EventId(String value) implements DomainId {
        public EventId { requireNonBlank(value, "EventId"); }
        public static EventId random() { return new EventId(UUID.randomUUID().toString()); }
        public static EventId of(String value) { return new EventId(value); }
    }
    public record ArtifactId(String value) implements DomainId {
        public ArtifactId { requireNonBlank(value, "ArtifactId"); }
        public static ArtifactId random() { return new ArtifactId(UUID.randomUUID().toString()); }
        public static ArtifactId of(String value) { return new ArtifactId(value); }
    }

    private static void requireNonBlank(String value, String name) {
        Objects.requireNonNull(value, name + " value must not be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException(name + " value must not be blank");
        }
    }
}
