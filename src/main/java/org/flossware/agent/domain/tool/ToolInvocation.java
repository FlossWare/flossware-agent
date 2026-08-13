package org.flossware.agent.domain.tool;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import org.flossware.agent.domain.id.Identifiers.InteractionId;
import org.flossware.agent.domain.id.Identifiers.ToolId;
import org.flossware.agent.domain.id.Identifiers.ToolInvocationId;

/** Immutable record of a tool invocation and optional result, with stable identity. */
public final class ToolInvocation {
    private final ToolInvocationId id;
    private final ToolId toolId;
    private final InteractionId interactionId;
    private final String arguments;
    private final Instant invokedAt;
    private final String result;
    private final boolean success;
    private final Instant completedAt;

    private ToolInvocation(ToolInvocationId id, ToolId toolId, InteractionId interactionId, String arguments,
                           Instant invokedAt, String result, boolean success, Instant completedAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.toolId = Objects.requireNonNull(toolId, "toolId");
        this.interactionId = Objects.requireNonNull(interactionId, "interactionId");
        this.arguments = arguments == null ? "" : arguments;
        this.invokedAt = Objects.requireNonNull(invokedAt, "invokedAt");
        this.result = result;
        this.success = success;
        this.completedAt = completedAt;
    }

    public static ToolInvocation start(ToolId toolId, InteractionId interactionId, String arguments) {
        return new ToolInvocation(ToolInvocationId.random(), toolId, interactionId, arguments,
                Instant.now(), null, false, null);
    }

    /**
     * Reconstructs a previously persisted invocation with a fixed identity and timestamps.
     * Structural validation matches construction rules; does not enforce lifecycle transitions.
     */
    public static ToolInvocation restore(ToolInvocationId id, ToolId toolId, InteractionId interactionId,
                                         String arguments, Instant invokedAt, String result,
                                         boolean success, Instant completedAt) {
        return new ToolInvocation(id, toolId, interactionId, arguments, invokedAt, result, success, completedAt);
    }

    public ToolInvocation complete(String resultPayload, boolean wasSuccessful) {
        if (isComplete()) {
            throw new IllegalStateException("Tool invocation already completed: " + id.value());
        }
        return new ToolInvocation(id, toolId, interactionId, arguments, invokedAt, resultPayload,
                wasSuccessful, Instant.now());
    }

    public ToolInvocationId id() { return id; }
    public ToolId toolId() { return toolId; }
    public InteractionId interactionId() { return interactionId; }
    public String arguments() { return arguments; }
    public Instant invokedAt() { return invokedAt; }
    public Optional<String> result() { return Optional.ofNullable(result); }
    public boolean success() { return success; }
    public Optional<Instant> completedAt() { return Optional.ofNullable(completedAt); }
    public boolean isComplete() { return completedAt != null; }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ToolInvocation other)) {
            return false;
        }
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
