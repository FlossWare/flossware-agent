package org.flossware.agent.port;

import java.util.List;
import java.util.Optional;
import org.flossware.agent.domain.id.Identifiers.AgentId;
import org.flossware.agent.domain.id.Identifiers.TaskId;
import org.flossware.agent.domain.task.Task;

public interface TaskRepository {
    Task save(Task task);
    Optional<Task> findById(TaskId id);
    List<Task> findByAgentId(AgentId agentId);
    void delete(TaskId id);
}
