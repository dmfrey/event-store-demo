package io.pivotal.dmfrey.eventStoreDemo.domain.client.kafka.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.pivotal.dmfrey.eventStoreDemo.domain.events.DomainEvent;
import io.pivotal.dmfrey.eventStoreDemo.domain.model.Board;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Serialized;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.support.serializer.JsonSerde;

import java.io.IOException;

import static io.pivotal.dmfrey.eventStoreDemo.domain.client.kafka.config.KafkaClientConfig.BOARD_EVENTS_SNAPSHOTS;

@Profile( "kafka" )
@EnableBinding( BoardEventsStreamsProcessor.class )
public class DomainEventSinkImpl implements DomainEventSink {

    private final ObjectMapper mapper;
    private final Serde<DomainEvent> domainEventSerde;
    private final Serde<Board> boardSerde;

    public DomainEventSinkImpl( final ObjectMapper mapper ) {

        this.mapper = mapper;
        this.domainEventSerde = new JsonSerde<>( DomainEvent.class, mapper );
        this.boardSerde = new JsonSerde<>( Board.class, mapper );

    }

    @StreamListener( "input" )
    public void process( KStream<Object, byte[]> input ) {

        input
                .map( (key, value) -> {

                    try {

                        DomainEvent domainEvent = mapper.readValue( value, DomainEvent.class );

                        return new KeyValue<>( domainEvent.getBoardUuid().toString(), domainEvent );

                    } catch( IOException e ) {
                        e.printStackTrace();
                    }

                    return null;
                })
                .groupBy( (s, domainEvent) -> s, Serialized.with( Serdes.String(), domainEventSerde ) )
                .aggregate(
                        Board::new,
                        (key, domainEvent, board) -> board.handleEvent( domainEvent ),
                        Materialized.<String, Board, KeyValueStore<Bytes, byte[]>>as( BOARD_EVENTS_SNAPSHOTS )
                            .withKeySerde( Serdes.String() )
                            .withValueSerde( boardSerde )
                );

    }

}
