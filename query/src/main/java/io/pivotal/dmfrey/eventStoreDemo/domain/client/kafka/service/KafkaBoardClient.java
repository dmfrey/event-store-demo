package io.pivotal.dmfrey.eventStoreDemo.domain.client.kafka.service;

import io.pivotal.dmfrey.eventStoreDemo.domain.client.BoardClient;
import io.pivotal.dmfrey.eventStoreDemo.domain.events.DomainEvent;
import io.pivotal.dmfrey.eventStoreDemo.domain.model.Board;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.errors.InvalidStateStoreException;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.cloud.stream.binder.kafka.streams.QueryableStoreRegistry;

import java.util.List;
import java.util.UUID;

import static io.pivotal.dmfrey.eventStoreDemo.domain.client.kafka.config.KafkaClientConfig.BOARD_EVENTS_SNAPSHOTS;

@Slf4j
public class KafkaBoardClient implements BoardClient {

    private final QueryableStoreRegistry queryableStoreRegistry;

    public KafkaBoardClient(
            final QueryableStoreRegistry queryableStoreRegistry
    ) {

        this.queryableStoreRegistry = queryableStoreRegistry;

    }

    @Override
    public Board find( final UUID boardUuid ) {
        log.debug( "find : enter" );

        try {

            ReadOnlyKeyValueStore<String, Board> store = queryableStoreRegistry.getQueryableStoreType( BOARD_EVENTS_SNAPSHOTS, QueryableStoreTypes.<String, Board>keyValueStore() );

            Board board = store.get( boardUuid.toString() );
            if( null != board ) {

                log.debug( "find : board=" + board.toString() );

                log.debug( "find : exit" );
                return board;

            } else {

                throw new IllegalArgumentException( "board[" + boardUuid.toString() + "] not found!" );
            }

        } catch( InvalidStateStoreException e ) {
            log.error( "find : error", e );

        }

        throw new IllegalArgumentException( "board[" + boardUuid.toString() + "] not found!" );
    }

}
