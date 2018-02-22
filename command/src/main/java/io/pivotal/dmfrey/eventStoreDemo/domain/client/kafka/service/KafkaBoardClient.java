package io.pivotal.dmfrey.eventStoreDemo.domain.client.kafka.service;

import io.pivotal.dmfrey.eventStoreDemo.domain.client.BoardClient;
import io.pivotal.dmfrey.eventStoreDemo.domain.events.DomainEvent;
import io.pivotal.dmfrey.eventStoreDemo.domain.model.Board;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.errors.InvalidStateStoreException;
import org.apache.kafka.streams.state.QueryableStoreType;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.cloud.stream.binder.kafka.streams.QueryableStoreRegistry;
import org.springframework.kafka.core.StreamsBuilderFactoryBean;

import java.util.List;
import java.util.UUID;

import static io.pivotal.dmfrey.eventStoreDemo.domain.client.kafka.config.KafkaConfig.BOARD_EVENTS_SNAPSHOTS;

@Slf4j
public class KafkaBoardClient implements BoardClient {

    private final DomainEventSource domainEventSource;
    private final QueryableStoreRegistry queryableStoreRegistry;

    public KafkaBoardClient(
            final DomainEventSource domainEventSource,
            final QueryableStoreRegistry queryableStoreRegistry
    ) {

        this.domainEventSource = domainEventSource;
        this.queryableStoreRegistry = queryableStoreRegistry;

    }

    @Override
    public void save( final Board board ) {
        log.debug( "save : enter" );

        List<DomainEvent> newChanges = board.changes();

        newChanges.forEach( domainEvent -> {
            log.debug( "save : domainEvent=" + domainEvent );

            this.domainEventSource.publish( domainEvent );

        });
        board.flushChanges();

        log.debug( "save : exit" );
    }

    @Override
    public Board find( final UUID boardUuid ) {
        log.debug( "find : enter" );

//        while( true ) {

//            try {

                ReadOnlyKeyValueStore<String, Board> store = queryableStoreRegistry.getQueryableStoreType( BOARD_EVENTS_SNAPSHOTS, QueryableStoreTypes.<String, Board>keyValueStore() );

                Board board = store.get( boardUuid.toString() );
                if( null != board ) {

                    board.flushChanges();
                    log.debug("find : board=" + board.toString());

                    log.debug("find : exit");
                    return board;

                } else {

                    throw new IllegalArgumentException( "board[" + boardUuid.toString() + "] not found!" );
                }

//            } catch( InvalidStateStoreException e ) {
//
//                try {
//                    Thread.sleep( 100 );
//                } catch( InterruptedException e1 ) {
//                    log.error( "find : thread interrupted", e1 );
//                }
//
//            }

//        }

    }

}
