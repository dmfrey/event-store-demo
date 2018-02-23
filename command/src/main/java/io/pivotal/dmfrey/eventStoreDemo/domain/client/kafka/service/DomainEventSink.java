package io.pivotal.dmfrey.eventStoreDemo.domain.client.kafka.service;

import org.apache.kafka.streams.kstream.KStream;

public interface DomainEventSink {

    void process( KStream<Object, byte[]> input );

}
