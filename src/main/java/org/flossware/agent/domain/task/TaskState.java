package org.flossware.agent.domain.task;

public enum TaskState {
    PENDING, RUNNING, COMPLETED, FAILED, CANCELLED;
    public boolean canTransitionTo(TaskState target) {
        return switch (this) {
            case PENDING -> target == RUNNING || target == CANCELLED;
            case RUNNING -> target == COMPLETED || target == FAILED || target == CANCELLED;
            default -> false;
        };
    }
    public boolean isTerminal() { return this == COMPLETED || this == FAILED || this == CANCELLED; }
}
