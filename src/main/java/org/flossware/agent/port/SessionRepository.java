package org.flossware.agent.port;

import java.util.List;
import java.util.Optional;
import org.flossware.agent.domain.id.Identifiers.AgentId;
import org.flossware.agent.domain.id.Identifiers.SessionId;
import org.flossware.agent.domain.session.Session;

public interface SessionRepository {
    Session save(Session session);
    Optional<Session> findById(SessionId id);
    List<Session> findByAgentId(AgentId agentId);
    void delete(SessionId id);
}
