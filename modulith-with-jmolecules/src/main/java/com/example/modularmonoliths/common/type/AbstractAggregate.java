package com.example.modularmonoliths.common.type;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.jmolecules.ddd.types.AggregateRoot;
import org.jmolecules.ddd.types.Identifier;
import org.jmolecules.event.types.DomainEvent;
import org.springframework.data.domain.AfterDomainEventPublication;
import org.springframework.data.domain.DomainEvents;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@MappedSuperclass
public abstract class AbstractAggregate<T extends AbstractAggregate<T, ID>, ID extends Identifier> implements AggregateRoot<T, ID> {

    @Transient
    @JsonIgnore
    private final List<DomainEvent> events = new ArrayList<>();

    protected void registerEvent(DomainEvent event) {
        this.events.add(event);
    }

    @DomainEvents
    public Collection<DomainEvent> getEvents() {
        events.forEach(event -> log.info("<e> {}", event));
        return Collections.unmodifiableCollection(events);
    }

    @AfterDomainEventPublication
    public void clearEvents() {
        events.clear();
    }

}
