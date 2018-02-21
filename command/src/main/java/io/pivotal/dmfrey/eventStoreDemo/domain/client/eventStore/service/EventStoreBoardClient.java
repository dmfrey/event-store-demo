package io.pivotal.dmfrey.eventStoreDemo.domain.client.eventStore.service;

import io.pivotal.dmfrey.eventStoreDemo.domain.client.BoardClient;
import io.pivotal.dmfrey.eventStoreDemo.domain.client.eventStore.config.RestConfig;
import io.pivotal.dmfrey.eventStoreDemo.domain.events.DomainEvents;
import io.pivotal.dmfrey.eventStoreDemo.domain.events.DomainEvent;
import io.pivotal.dmfrey.eventStoreDemo.domain.model.Board;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

@Slf4j
public class EventStoreBoardClient implements BoardClient {

    private final RestConfig.EventStoreClient eventStoreClient;

    public EventStoreBoardClient( final RestConfig.EventStoreClient eventStoreClient ) {

        this.eventStoreClient = eventStoreClient;

    }

    @Override
    public void save( final Board board ) {
        log.debug( "save : enter" );

        List<DomainEvent> newChanges = board.changes();

        newChanges.forEach( domainEvent -> {
            log.debug( "save : domainEvent=" + domainEvent );

            ResponseEntity accepted = this.eventStoreClient.addNewDomainEvent( domainEvent );
            if( !accepted.getStatusCode().equals( HttpStatus.ACCEPTED ) ) {

                throw new IllegalStateException( "could not add DomainEvent to the Event Store" );
            }
        });
        board.flushChanges();

        log.debug( "save : exit" );
    }

    @Override
    public Board find( final UUID boardUuid ) {
        log.debug( "find : enter" );

        DomainEvents domainEvents = this.eventStoreClient.getDomainEventsForBoardUuid( boardUuid );
        if( domainEvents.getDomainEvents().isEmpty() ) {

            log.warn( "find : exit, target[" + boardUuid.toString() + "] not found" );
            throw new IllegalArgumentException( "board[" + boardUuid.toString() + "] not found" );
        }

        Board board = Board.createFrom( boardUuid, domainEvents.getDomainEvents() );
        board.flushChanges();

        log.debug( "find : exit" );
        return board;
    }

}
