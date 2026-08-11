package org.flossware.agent.port;

import org.flossware.agent.domain.event.DomainEvent;

public interface EventPublisher {
    void publish(DomainEvent event);
}
