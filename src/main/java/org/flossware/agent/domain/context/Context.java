package org.flossware.agent.domain.context;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.flossware.agent.domain.memory.Memory;
import org.flossware.agent.domain.message.Message;
import org.flossware.agent.domain.tool.ToolDefinition;

/** Assembled snapshot of information for an agent operation. Value object. */
public final class Context {
    private final List<Message> messages;
    private final List<Memory> memories;
    private final List<ToolDefinition> tools;
    private final Map<String, String> attributes;

    private Context(List<Message> messages, List<Memory> memories, List<ToolDefinition> tools,
                    Map<String, String> attributes) {
        this.messages = List.copyOf(Objects.requireNonNull(messages, "messages"));
        this.memories = List.copyOf(Objects.requireNonNull(memories, "memories"));
        this.tools = List.copyOf(Objects.requireNonNull(tools, "tools"));
        this.attributes = Collections.unmodifiableMap(Objects.requireNonNull(attributes, "attributes"));
    }

    public static Context empty() {
        return new Context(List.of(), List.of(), List.of(), Map.of());
    }

    public static Context of(List<Message> messages, List<Memory> memories,
                             List<ToolDefinition> tools, Map<String, String> attributes) {
        return new Context(messages, memories, tools, attributes);
    }

    public List<Message> messages() { return messages; }
    public List<Memory> memories() { return memories; }
    public List<ToolDefinition> tools() { return tools; }
    public Map<String, String> attributes() { return attributes; }
    public boolean isEmpty() {
        return messages.isEmpty() && memories.isEmpty() && tools.isEmpty() && attributes.isEmpty();
    }
}
