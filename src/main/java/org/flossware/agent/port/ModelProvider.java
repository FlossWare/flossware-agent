package org.flossware.agent.port;

import java.util.List;
import java.util.Optional;
import org.flossware.agent.domain.id.Identifiers.ModelId;
import org.flossware.agent.domain.id.Identifiers.ProviderId;
import org.flossware.agent.domain.model.ModelDescriptor;
import org.flossware.agent.domain.model.ModelRequest;
import org.flossware.agent.domain.model.ModelResponse;

/** Port for provider-neutral model access. */
public interface ModelProvider {
    ProviderId providerId();
    List<ModelDescriptor> listModels();
    Optional<ModelDescriptor> findModel(ModelId modelId);
    ModelResponse complete(ModelRequest request);
}
