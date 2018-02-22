package io.pivotal.dmfrey.eventStoreDemo.domain.client.kafka.service;

import io.pivotal.dmfrey.eventStoreDemo.domain.events.DomainEvent;
import org.apache.kafka.streams.kstream.KStream;

public interface DomainEventSink {

    void process( KStream<String, DomainEvent> input );

}
