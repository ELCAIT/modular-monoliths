package com.example.modularmonoliths.common.event;

import org.jmolecules.event.types.DomainEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.example.modularmonoliths.common.log.LogPrefix;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
class EventPublisherImpl implements EventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void publish(DomainEvent event) {
        log.info("{} {}", LogPrefix.EVENT, event);
        applicationEventPublisher.publishEvent(event);
    }

}
