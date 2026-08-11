package org.flossware.agent.port;

import java.util.List;
import java.util.Optional;
import org.flossware.agent.domain.id.Identifiers.AgentId;
import org.flossware.agent.domain.id.Identifiers.MemoryId;
import org.flossware.agent.domain.id.Identifiers.SessionId;
import org.flossware.agent.domain.memory.Memory;

public interface MemoryRepository {
    Memory save(Memory memory);
    Optional<Memory> findById(MemoryId id);
    List<Memory> findByAgentId(AgentId agentId);
    List<Memory> findBySessionId(SessionId sessionId);
    void delete(MemoryId id);
}
