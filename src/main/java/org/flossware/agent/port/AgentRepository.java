package org.flossware.agent.port;

import java.util.Optional;
import org.flossware.agent.domain.agent.Agent;
import org.flossware.agent.domain.id.Identifiers.AgentId;

public interface AgentRepository {
    Agent save(Agent agent);
    Optional<Agent> findById(AgentId id);
    void delete(AgentId id);
}
