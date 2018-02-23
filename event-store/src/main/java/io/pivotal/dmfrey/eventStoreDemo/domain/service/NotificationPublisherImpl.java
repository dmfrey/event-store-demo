package io.pivotal.dmfrey.eventStoreDemo.domain.service;

import io.pivotal.dmfrey.eventStoreDemo.domain.service.converters.TupleToJsonStringConverter;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.integration.annotation.Publisher;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.tuple.Tuple;

@EnableBinding( Source.class )
public class NotificationPublisherImpl implements NotificationPublisher {

    private final TupleToJsonStringConverter converter;

    public NotificationPublisherImpl( final TupleToJsonStringConverter tupleToJsonStringConverter ) {

        this.converter = tupleToJsonStringConverter;

    }

    @Publisher( channel = Source.OUTPUT )
    public Message<String> sendNotification( Tuple event ) {

        String payload = converter.convert( event );

        return MessageBuilder
                .withPayload( payload )
                .setHeader( "x-delay", 1000 )
                .build();
    }

}
