package io.pivotal.dmfrey.eventStoreDemo.domain.client.kafka.service;

import org.apache.kafka.streams.kstream.KStream;
import org.springframework.cloud.stream.annotation.Input;

public interface BoardEventsStreamsProcessor {

    @Input( "input" )
    KStream<?, ?> input();

}
