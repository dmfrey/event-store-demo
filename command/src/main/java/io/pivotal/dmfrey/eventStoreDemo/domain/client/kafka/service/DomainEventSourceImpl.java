package io.pivotal.dmfrey.eventStoreDemo.domain.client.kafka.service;

import io.pivotal.dmfrey.eventStoreDemo.domain.events.DomainEvent;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.annotation.Publisher;

@Profile( "kafka" )
@EnableBinding( Source.class )
public class DomainEventSourceImpl implements DomainEventSource {

    @Publisher( channel = Source.OUTPUT )
    public DomainEvent publish( final DomainEvent event ) {

        return event;
    }

}
