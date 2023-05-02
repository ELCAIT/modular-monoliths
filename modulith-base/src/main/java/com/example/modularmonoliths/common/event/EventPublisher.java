package com.example.modularmonoliths.common.event;

public interface EventPublisher {

    void publish(DomainEvent event);

}
