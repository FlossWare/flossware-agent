package org.flossware.agent.domain.tool;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import org.flossware.agent.domain.id.Identifiers.InteractionId;
import org.flossware.agent.domain.id.Identifiers.ToolId;

/** Immutable record of a tool invocation and optional result. */
public final class ToolInvocation {
    private final ToolId toolId;
    private final InteractionId interactionId;
    private final String arguments;
    private final Instant invokedAt;
    private final String result;
    private final boolean success;
    private final Instant completedAt;

    private ToolInvocation(ToolId toolId, InteractionId interactionId, String arguments,
                           Instant invokedAt, String result, boolean success, Instant completedAt) {
        this.toolId = Objects.requireNonNull(toolId, "toolId");
        this.interactionId = Objects.requireNonNull(interactionId, "interactionId");
        this.arguments = arguments == null ? "" : arguments;
        this.invokedAt = Objects.requireNonNull(invokedAt, "invokedAt");
        this.result = result;
        this.success = success;
        this.completedAt = completedAt;
    }

    public static ToolInvocation start(ToolId toolId, InteractionId interactionId, String arguments) {
        return new ToolInvocation(toolId, interactionId, arguments, Instant.now(), null, false, null);
    }

    public ToolInvocation complete(String resultPayload, boolean wasSuccessful) {
        return new ToolInvocation(toolId, interactionId, arguments, invokedAt, resultPayload,
                wasSuccessful, Instant.now());
    }

    public ToolId toolId() { return toolId; }
    public InteractionId interactionId() { return interactionId; }
    public String arguments() { return arguments; }
    public Instant invokedAt() { return invokedAt; }
    public Optional<String> result() { return Optional.ofNullable(result); }
    public boolean success() { return success; }
    public Optional<Instant> completedAt() { return Optional.ofNullable(completedAt); }
    public boolean isComplete() { return completedAt != null; }
}
