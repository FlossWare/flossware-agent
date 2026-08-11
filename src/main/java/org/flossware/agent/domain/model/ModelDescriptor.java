package org.flossware.agent.domain.model;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.flossware.agent.domain.id.Identifiers.ModelId;
import org.flossware.agent.domain.id.Identifiers.ProviderId;

/** Provider-neutral description of a logical inference capability. */
public final class ModelDescriptor {
    private final ModelId id;
    private final ProviderId providerId;
    private final String displayName;
    private final Integer contextWindow;
    private final Map<String, String> capabilities;

    private ModelDescriptor(ModelId id, ProviderId providerId, String displayName,
                            Integer contextWindow, Map<String, String> capabilities) {
        this.id = Objects.requireNonNull(id, "id");
        this.providerId = Objects.requireNonNull(providerId, "providerId");
        this.displayName = displayName == null ? id.value() : displayName;
        this.contextWindow = contextWindow;
        this.capabilities = Collections.unmodifiableMap(Objects.requireNonNull(capabilities, "capabilities"));
    }

    public static ModelDescriptor of(ModelId id, ProviderId providerId) {
        return new ModelDescriptor(id, providerId, id.value(), null, Map.of());
    }

    public static ModelDescriptor of(ModelId id, ProviderId providerId, String displayName,
                                     Integer contextWindow, Map<String, String> capabilities) {
        return new ModelDescriptor(id, providerId, displayName, contextWindow, Map.copyOf(capabilities));
    }

    public ModelId id() { return id; }
    public ProviderId providerId() { return providerId; }
    public String displayName() { return displayName; }
    public Optional<Integer> contextWindow() { return Optional.ofNullable(contextWindow); }
    public Map<String, String> capabilities() { return capabilities; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ModelDescriptor other)) return false;
        return id.equals(other.id) && providerId.equals(other.providerId);
    }
    @Override public int hashCode() { return Objects.hash(id, providerId); }
}
