package org.flossware.agent.domain.model;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.flossware.agent.domain.message.Message;
import org.flossware.agent.domain.tool.ToolInvocation;

/** Provider-neutral model inference response. */
public final class ModelResponse {
    private final Message message;
    private final List<ToolInvocation> toolCalls;
    private final String finishReason;
    private final Integer inputTokens;
    private final Integer outputTokens;
    private final Map<String, String> metadata;

    private ModelResponse(Message message, List<ToolInvocation> toolCalls, String finishReason,
                          Integer inputTokens, Integer outputTokens, Map<String, String> metadata) {
        this.message = Objects.requireNonNull(message, "message");
        this.toolCalls = List.copyOf(Objects.requireNonNull(toolCalls, "toolCalls"));
        this.finishReason = finishReason;
        this.inputTokens = inputTokens;
        this.outputTokens = outputTokens;
        this.metadata = Collections.unmodifiableMap(Objects.requireNonNull(metadata, "metadata"));
    }

    public static ModelResponse of(Message message) {
        return new ModelResponse(message, List.of(), null, null, null, Map.of());
    }

    public Message message() { return message; }
    public List<ToolInvocation> toolCalls() { return toolCalls; }
    public Optional<String> finishReason() { return Optional.ofNullable(finishReason); }
    public Optional<Integer> inputTokens() { return Optional.ofNullable(inputTokens); }
    public Optional<Integer> outputTokens() { return Optional.ofNullable(outputTokens); }
    public Map<String, String> metadata() { return metadata; }
}
