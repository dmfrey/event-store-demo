package io.pivotal.dmfrey.eventStoreDemo.domain.client.kafka.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.pivotal.dmfrey.eventStoreDemo.domain.events.DomainEvent;
import io.pivotal.dmfrey.eventStoreDemo.domain.model.Board;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.*;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.support.serializer.JsonSerde;

import static io.pivotal.dmfrey.eventStoreDemo.domain.client.kafka.config.KafkaConfig.BOARD_EVENTS_SNAPSHOTS;

@Profile( "kafka" )
@EnableBinding( BoardEventsStreamsProcessor.class )
@Slf4j
public class DomainEventSinkImpl implements DomainEventSink {

    private final Serde<DomainEvent> domainEventSerde;
    private final Serde<Board> boardSerde;

    public DomainEventSinkImpl( final ObjectMapper mapper ) {

        domainEventSerde = new JsonSerde<>( DomainEvent.class, mapper );
        boardSerde = new JsonSerde<>( Board.class, mapper );

    }

    @StreamListener( "input" )
    public void process( KStream<Object, DomainEvent> input ) {

        input
                .map( new KeyValueMapper<Object, DomainEvent, KeyValue<String, DomainEvent>>() {
                         @Override
                         public KeyValue<String, DomainEvent> apply( Object key, DomainEvent value ) {
                             KeyValue<String, DomainEvent> kv = new KeyValue<>( value.getBoardUuid().toString(), value );
                             System.out.println( "kv=" + kv.toString() );
                             return kv;
                         }
                     }
                )
//                .groupBy( (s, domainEvent) -> s, Serialized.with( Serdes.String(), domainEventSerde ) )
                .groupByKey()
                .aggregate(
                        new Initializer<Board>() {
                            @Override
                            public Board apply() {
                                return new Board();
                            }
                        },
                        new Aggregator<String, DomainEvent, Board>() {
                            @Override
                            public Board apply( String key, DomainEvent value, Board aggregate ) {
                                return aggregate.handleEvent( value );
                            }
                        },
                        Materialized.as( BOARD_EVENTS_SNAPSHOTS )
                ).print();

    }

}
