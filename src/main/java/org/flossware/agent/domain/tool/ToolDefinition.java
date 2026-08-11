package org.flossware.agent.domain.tool;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import org.flossware.agent.domain.id.Identifiers.ToolId;

/** Provider-neutral capability description. Parameter schema is opaque JSON. */
public final class ToolDefinition {
    private final ToolId id;
    private final String name;
    private final String description;
    private final String parameterSchema;
    private final Map<String, String> metadata;

    private ToolDefinition(ToolId id, String name, String description, String parameterSchema,
                           Map<String, String> metadata) {
        this.id = Objects.requireNonNull(id, "id");
        Objects.requireNonNull(name, "name");
        if (name.isBlank()) throw new IllegalArgumentException("name must not be blank");
        this.name = name;
        this.description = description == null ? "" : description;
        this.parameterSchema = parameterSchema == null ? "{}" : parameterSchema;
        this.metadata = Collections.unmodifiableMap(Objects.requireNonNull(metadata, "metadata"));
    }

    public static ToolDefinition of(String name, String description, String parameterSchema) {
        return new ToolDefinition(ToolId.of(name), name, description, parameterSchema, Map.of());
    }

    public ToolId id() { return id; }
    public String name() { return name; }
    public String description() { return description; }
    public String parameterSchema() { return parameterSchema; }
    public Map<String, String> metadata() { return metadata; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ToolDefinition other)) return false;
        return id.equals(other.id);
    }
    @Override public int hashCode() { return id.hashCode(); }
}
