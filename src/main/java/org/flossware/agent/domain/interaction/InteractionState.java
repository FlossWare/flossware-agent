package org.flossware.agent.domain.interaction;

public enum InteractionState {
    STARTED, COMPLETED, FAILED, CANCELLED;
    public boolean canTransitionTo(InteractionState target) {
        return this == STARTED && (target == COMPLETED || target == FAILED || target == CANCELLED);
    }
    public boolean isTerminal() { return this != STARTED; }
}
