package org.flossware.agent.port;

import java.util.List;
import java.util.Optional;
import org.flossware.agent.domain.artifact.Artifact;
import org.flossware.agent.domain.id.Identifiers.AgentId;
import org.flossware.agent.domain.id.Identifiers.ArtifactId;
import org.flossware.agent.domain.id.Identifiers.SessionId;

public interface ArtifactRepository {
    Artifact save(Artifact artifact);
    Optional<Artifact> findById(ArtifactId id);
    List<Artifact> findByAgentId(AgentId agentId);
    List<Artifact> findBySessionId(SessionId sessionId);
    void delete(ArtifactId id);
}
