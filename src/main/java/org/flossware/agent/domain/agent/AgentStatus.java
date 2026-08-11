package org.flossware.agent.domain.agent;

public enum AgentStatus {
    ACTIVE, DISABLED, ARCHIVED;
    public boolean canTransitionTo(AgentStatus target) {
        return switch (this) {
            case ACTIVE -> target == DISABLED || target == ARCHIVED;
            case DISABLED -> target == ACTIVE || target == ARCHIVED;
            case ARCHIVED -> false;
        };
    }
    public boolean isUsable() { return this == ACTIVE; }
}
