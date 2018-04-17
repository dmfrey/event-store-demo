package io.pivotal.dmfrey.eventStoreDemo.domain.client.eventStore.service;

import io.pivotal.dmfrey.eventStoreDemo.domain.client.BoardClient;
import io.pivotal.dmfrey.eventStoreDemo.domain.client.eventStore.config.RestConfig;
import io.pivotal.dmfrey.eventStoreDemo.domain.events.DomainEvents;
import io.pivotal.dmfrey.eventStoreDemo.domain.model.Board;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.UUID;

public class EventStoreBoardClient implements BoardClient {

    private final RestConfig.EventStoreClient eventStoreClient;

    public EventStoreBoardClient( final RestConfig.EventStoreClient eventStoreClient ) {

        this.eventStoreClient = eventStoreClient;

    }

    @Override
    @Cacheable( "boards" )
    public Board find( final UUID boardUuid ) {

        DomainEvents domainEvents = this.eventStoreClient.getDomainEventsForBoardUuid( boardUuid );
        if( null == domainEvents ) {

            throw new IllegalArgumentException( "board[" + boardUuid.toString() + "] not found" );
        }

        Board board = Board.createFrom( boardUuid, domainEvents.getDomainEvents() );

        return board;
    }

    @Override
    @CacheEvict( value = "boards", key = "#boardUuid" )
    public void removeFromCache( final UUID boardUuid ) {

        // this method is intentionally left blank

    }

}
