package org.flossware.agent.domain.session;

public enum SessionState {
    CREATED, ACTIVE, SUSPENDED, CLOSED;
    public boolean canTransitionTo(SessionState target) {
        return switch (this) {
            case CREATED -> target == ACTIVE || target == CLOSED;
            case ACTIVE -> target == SUSPENDED || target == CLOSED;
            case SUSPENDED -> target == ACTIVE || target == CLOSED;
            case CLOSED -> false;
        };
    }
    public boolean isTerminal() { return this == CLOSED; }
    public boolean isResumable() { return this == ACTIVE || this == SUSPENDED; }
}
