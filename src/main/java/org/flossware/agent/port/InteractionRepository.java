package org.flossware.agent.port;

import java.util.List;
import java.util.Optional;
import org.flossware.agent.domain.id.Identifiers.InteractionId;
import org.flossware.agent.domain.id.Identifiers.SessionId;
import org.flossware.agent.domain.interaction.Interaction;

public interface InteractionRepository {
    Interaction save(Interaction interaction);
    Optional<Interaction> findById(InteractionId id);
    List<Interaction> findBySessionId(SessionId sessionId);
}
