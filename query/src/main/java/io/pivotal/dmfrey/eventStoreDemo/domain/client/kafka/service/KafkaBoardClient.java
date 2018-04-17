package io.pivotal.dmfrey.eventStoreDemo.domain.client.kafka.service;

import io.pivotal.dmfrey.eventStoreDemo.domain.client.BoardClient;
import io.pivotal.dmfrey.eventStoreDemo.domain.model.Board;
import org.apache.kafka.streams.errors.InvalidStateStoreException;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.cloud.stream.binder.kafka.streams.QueryableStoreRegistry;

import java.util.UUID;

import static io.pivotal.dmfrey.eventStoreDemo.domain.client.kafka.config.KafkaClientConfig.BOARD_EVENTS_SNAPSHOTS;

public class KafkaBoardClient implements BoardClient {

    private final QueryableStoreRegistry queryableStoreRegistry;

    public KafkaBoardClient(
            final QueryableStoreRegistry queryableStoreRegistry
    ) {

        this.queryableStoreRegistry = queryableStoreRegistry;

    }

    @Override
    public Board find( final UUID boardUuid ) {

        try {

            ReadOnlyKeyValueStore<String, Board> store = queryableStoreRegistry.getQueryableStoreType( BOARD_EVENTS_SNAPSHOTS, QueryableStoreTypes.<String, Board>keyValueStore() );

            Board board = store.get( boardUuid.toString() );
            if( null != board ) {

                return board;

            } else {

                throw new IllegalArgumentException( "board[" + boardUuid.toString() + "] not found!" );
            }

        } catch( InvalidStateStoreException e ) {
            e.printStackTrace();

        }

        throw new IllegalArgumentException( "board[" + boardUuid.toString() + "] not found!" );
    }

    @Override
    public void removeFromCache( final UUID boardUuid ) {

        throw new UnsupportedOperationException( "this method is not implemented in kafka client" );
    }

}
