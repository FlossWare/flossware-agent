package org.flossware.agent.domain.interaction;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.flossware.agent.domain.id.Identifiers.InteractionId;
import org.flossware.agent.domain.id.Identifiers.SessionId;
import org.flossware.agent.domain.message.Message;

/** Unit of exchange within a session. Message lists are immutable snapshots. */
public final class Interaction {
    private final InteractionId id;
    private final SessionId sessionId;
    private final InteractionState state;
    private final List<Message> messages;
    private final Instant startedAt;
    private final Instant completedAt;
    private final Map<String, String> metadata;

    private Interaction(InteractionId id, SessionId sessionId, InteractionState state,
                        List<Message> messages, Instant startedAt, Instant completedAt,
                        Map<String, String> metadata) {
        this.id = Objects.requireNonNull(id, "id");
        this.sessionId = Objects.requireNonNull(sessionId, "sessionId");
        this.state = Objects.requireNonNull(state, "state");
        this.messages = List.copyOf(Objects.requireNonNull(messages, "messages"));
        this.startedAt = Objects.requireNonNull(startedAt, "startedAt");
        this.completedAt = completedAt;
        this.metadata = Collections.unmodifiableMap(Objects.requireNonNull(metadata, "metadata"));
    }

    public static Interaction start(SessionId sessionId) {
        Instant now = Instant.now();
        return new Interaction(InteractionId.random(), sessionId, InteractionState.STARTED,
                List.of(), now, null, Map.of());
    }

    public static Interaction restore(InteractionId id, SessionId sessionId, InteractionState state,
                                      List<Message> messages, Instant startedAt, Instant completedAt,
                                      Map<String, String> metadata) {
        return new Interaction(id, sessionId, state, messages, startedAt, completedAt, Map.copyOf(metadata));
    }

    public InteractionId id() { return id; }
    public SessionId sessionId() { return sessionId; }
    public InteractionState state() { return state; }
    public List<Message> messages() { return messages; }
    public Instant startedAt() { return startedAt; }
    public Instant completedAt() { return completedAt; }
    public Map<String, String> metadata() { return metadata; }

    public Interaction appendMessage(Message message) {
        if (state.isTerminal()) {
            throw new IllegalStateException("Cannot modify interaction in " + state);
        }
        List<Message> updated = new ArrayList<>(messages);
        updated.add(Objects.requireNonNull(message, "message"));
        return new Interaction(id, sessionId, state, updated, startedAt, completedAt, metadata);
    }

    public Interaction complete() { return transitionTo(InteractionState.COMPLETED); }
    public Interaction fail() { return transitionTo(InteractionState.FAILED); }
    public Interaction cancel() { return transitionTo(InteractionState.CANCELLED); }

    public Interaction transitionTo(InteractionState target) {
        if (!state.canTransitionTo(target)) {
            throw new IllegalStateException(
                    "Cannot transition interaction from " + state + " to " + target);
        }
        return new Interaction(id, sessionId, target, messages, startedAt, Instant.now(), metadata);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Interaction other)) return false;
        return id.equals(other.id);
    }
    @Override public int hashCode() { return id.hashCode(); }
}
