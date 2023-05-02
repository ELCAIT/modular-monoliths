package com.example.modularmonoliths.common.event;

import org.jmolecules.event.types.DomainEvent;

public interface EventPublisher {

    void publish(DomainEvent event);

}
