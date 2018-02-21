package io.pivotal.dmfrey.eventStoreDemo.domain.client.kafka.service;

import io.pivotal.dmfrey.eventStoreDemo.domain.events.DomainEvent;

public interface DomainEventSource {

    DomainEvent publish( final DomainEvent event );
}
