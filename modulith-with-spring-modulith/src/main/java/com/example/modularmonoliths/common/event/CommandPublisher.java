package com.example.modularmonoliths.common.event;

public interface CommandPublisher {

    void publish(Command command);

}
