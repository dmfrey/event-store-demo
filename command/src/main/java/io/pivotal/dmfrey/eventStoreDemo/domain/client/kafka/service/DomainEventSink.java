package io.pivotal.dmfrey.eventStoreDemo.domain.client.kafka.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.pivotal.dmfrey.eventStoreDemo.domain.events.DomainEvent;
import io.pivotal.dmfrey.eventStoreDemo.domain.model.Board;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Serialized;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.support.serializer.JsonSerde;

import static io.pivotal.dmfrey.eventStoreDemo.domain.client.kafka.config.KafkaConfig.BOARD_EVENTS_SNAPSHOTS;

@Profile( "kafka" )
@EnableBinding( DomainEventSink.BoardEventsStreamsProcessor.class )
public class DomainEventSink {

    private final Serde<DomainEvent> domainEventSerde;
    private final Serde<Board> boardSerde;

    public DomainEventSink( final ObjectMapper mapper ) {

        domainEventSerde = new JsonSerde<>( DomainEvent.class, mapper );
        boardSerde = new JsonSerde<>( Board.class, mapper );

    }

    @StreamListener( "input" )
    public void process( KStream<String, DomainEvent> input ) {

        input
                .groupBy( (s, domainEvent) -> domainEvent.getBoardUuid().toString(), Serialized.with( Serdes.String(), domainEventSerde ) )
                .aggregate(
                        Board::new,
                        (s, domainEvent, board) -> board.handleEvent( domainEvent ),
                        boardSerde,
                        BOARD_EVENTS_SNAPSHOTS
                );

    }

    interface BoardEventsStreamsProcessor {

        /**
         * @return {@link Input} binding for {@link KStream} type.
         */
        @Input( "input" )
        KStream<?, ?> input();

    }

}
