package org.flossware.agent.domain.model;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.flossware.agent.domain.id.Identifiers.ModelId;
import org.flossware.agent.domain.message.Message;
import org.flossware.agent.domain.tool.ToolDefinition;

/** Provider-neutral model inference request. */
public final class ModelRequest {
    private final ModelId modelId;
    private final List<Message> messages;
    private final List<ToolDefinition> tools;
    private final Double temperature;
    private final Integer maxTokens;
    private final Map<String, String> parameters;

    private ModelRequest(ModelId modelId, List<Message> messages, List<ToolDefinition> tools,
                         Double temperature, Integer maxTokens, Map<String, String> parameters) {
        this.modelId = Objects.requireNonNull(modelId, "modelId");
        this.messages = List.copyOf(Objects.requireNonNull(messages, "messages"));
        this.tools = List.copyOf(Objects.requireNonNull(tools, "tools"));
        this.temperature = temperature;
        this.maxTokens = maxTokens;
        this.parameters = Collections.unmodifiableMap(Objects.requireNonNull(parameters, "parameters"));
    }

    public static Builder builder(ModelId modelId) { return new Builder(modelId); }
    public ModelId modelId() { return modelId; }
    public List<Message> messages() { return messages; }
    public List<ToolDefinition> tools() { return tools; }
    public Optional<Double> temperature() { return Optional.ofNullable(temperature); }
    public Optional<Integer> maxTokens() { return Optional.ofNullable(maxTokens); }
    public Map<String, String> parameters() { return parameters; }

    public static final class Builder {
        private final ModelId modelId;
        private List<Message> messages = List.of();
        private List<ToolDefinition> tools = List.of();
        private Double temperature;
        private Integer maxTokens;
        private Map<String, String> parameters = Map.of();
        private Builder(ModelId modelId) { this.modelId = modelId; }
        public Builder messages(List<Message> m) { this.messages = m; return this; }
        public Builder tools(List<ToolDefinition> t) { this.tools = t; return this; }
        public Builder temperature(Double t) { this.temperature = t; return this; }
        public Builder maxTokens(Integer m) { this.maxTokens = m; return this; }
        public Builder parameters(Map<String, String> p) { this.parameters = p; return this; }
        public ModelRequest build() {
            return new ModelRequest(modelId, messages, tools, temperature, maxTokens, parameters);
        }
    }
}
