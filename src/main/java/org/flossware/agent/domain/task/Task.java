package org.flossware.agent.domain.task;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.flossware.agent.domain.id.Identifiers.AgentId;
import org.flossware.agent.domain.id.Identifiers.SessionId;
import org.flossware.agent.domain.id.Identifiers.TaskId;

/** Durable unit of work that may outlive a single interaction. */
public final class Task {
    private final TaskId id;
    private final AgentId agentId;
    private final SessionId sessionId;
    private final String description;
    private final TaskState state;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final String result;
    private final Map<String, String> metadata;

    private Task(TaskId id, AgentId agentId, SessionId sessionId, String description, TaskState state,
                 Instant createdAt, Instant updatedAt, String result, Map<String, String> metadata) {
        this.id = Objects.requireNonNull(id, "id");
        this.agentId = Objects.requireNonNull(agentId, "agentId");
        this.sessionId = sessionId;
        this.description = Objects.requireNonNull(description, "description");
        this.state = Objects.requireNonNull(state, "state");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt");
        this.result = result;
        this.metadata = Collections.unmodifiableMap(Objects.requireNonNull(metadata, "metadata"));
    }

    public static Task create(AgentId agentId, String description) {
        Instant now = Instant.now();
        return new Task(TaskId.random(), agentId, null, description, TaskState.PENDING, now, now, null, Map.of());
    }

    public static Task restore(TaskId id, AgentId agentId, SessionId sessionId, String description,
                               TaskState state, Instant createdAt, Instant updatedAt, String result,
                               Map<String, String> metadata) {
        return new Task(id, agentId, sessionId, description, state, createdAt, updatedAt, result, Map.copyOf(metadata));
    }

    public TaskId id() { return id; }
    public AgentId agentId() { return agentId; }
    public Optional<SessionId> sessionId() { return Optional.ofNullable(sessionId); }
    public String description() { return description; }
    public TaskState state() { return state; }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }
    public Optional<String> result() { return Optional.ofNullable(result); }
    public Map<String, String> metadata() { return metadata; }

    public Task start() { return transitionTo(TaskState.RUNNING, null); }
    public Task complete(String taskResult) { return transitionTo(TaskState.COMPLETED, taskResult); }
    public Task fail(String reason) { return transitionTo(TaskState.FAILED, reason); }
    public Task cancel() { return transitionTo(TaskState.CANCELLED, null); }

    private Task transitionTo(TaskState target, String newResult) {
        if (!state.canTransitionTo(target)) {
            throw new IllegalStateException("Cannot transition task from " + state + " to " + target);
        }
        return new Task(id, agentId, sessionId, description, target, createdAt, Instant.now(),
                newResult != null ? newResult : result, metadata);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task other)) return false;
        return id.equals(other.id);
    }
    @Override public int hashCode() { return id.hashCode(); }
}
