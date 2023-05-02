package com.example.modularmonoliths.common.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.example.modularmonoliths.common.log.LogPrefix;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
class CommandPublisherImpl implements CommandPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void publish(Command command) {
        log.info("{} {}", LogPrefix.COMMAND, command);
        applicationEventPublisher.publishEvent(command);
    }

}
