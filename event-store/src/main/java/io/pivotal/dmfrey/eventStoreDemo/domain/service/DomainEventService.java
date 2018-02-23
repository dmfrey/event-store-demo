package io.pivotal.dmfrey.eventStoreDemo.domain.service;

import io.pivotal.dmfrey.eventStoreDemo.domain.model.DomainEvents;
import io.pivotal.dmfrey.eventStoreDemo.domain.persistence.DomainEventEntity;
import io.pivotal.dmfrey.eventStoreDemo.domain.persistence.DomainEventsEntity;
import io.pivotal.dmfrey.eventStoreDemo.domain.persistence.DomainEventsRepository;
import io.pivotal.dmfrey.eventStoreDemo.domain.service.converters.JsonStringToTupleConverter;
import io.pivotal.dmfrey.eventStoreDemo.domain.service.converters.TupleToJsonStringConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.tuple.Tuple;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Slf4j
@Transactional( readOnly = true )
public class DomainEventService {

    private final DomainEventsRepository domainEventsRepository;
    private final NotificationPublisher publisher;
    private final TupleToJsonStringConverter toJsonStringConverter;
    private final JsonStringToTupleConverter toTupleConverter;


    public DomainEventService(
            final DomainEventsRepository domainEventsRepository, final NotificationPublisher publisher,
            final TupleToJsonStringConverter toJsonStringConverter, final JsonStringToTupleConverter toTupleConverter) {

        this.domainEventsRepository = domainEventsRepository;
        this.publisher = publisher;
        this.toJsonStringConverter = toJsonStringConverter;
        this.toTupleConverter = toTupleConverter;

    }

    public DomainEvents getDomainEvents( final String boardUuid ) {
        log.debug( "processDomainEvent : enter" );

        log.debug( "processDomainEvent : boardUuid=" + boardUuid );

        return domainEventsRepository.findById( boardUuid )
                .map( DomainEventsEntity::toModel )
                .orElseThrow( IllegalArgumentException::new );
    }

    @Transactional
    public void processDomainEvent( final Tuple event ) {
        log.debug( "processDomainEvent : enter" );

        log.debug( "processDomainEvent : event[{}] ", event );

        String eventType = event.getString( "eventType" );
        switch ( eventType ) {

            case "BoardInitialized":
                processBoardInitialized( event );
                break;

            default:
                processBoardEvent( event );
                break;
        }

        log.debug( "processDomainEvent : calling publisher.sendNotification( event )" );
        publisher.sendNotification( event );

        log.debug( "processDomainEvent : exit" );
    }

    private void processBoardInitialized( final Tuple event ) {
        log.debug( "processBoardInitialized : enter " );

        String boardUuid = event.getString( "boardUuid" );

        DomainEventsEntity domainEventsEntity = new DomainEventsEntity( boardUuid );

        DomainEventEntity domainEventEntity = new DomainEventEntity();
        domainEventEntity.setId( UUID.randomUUID().toString() );

        Instant occurredOn = Instant.parse( event.getString( "occurredOn" ) );
        domainEventEntity.setOccurredOn( LocalDateTime.ofInstant( occurredOn, ZoneOffset.UTC ) );

        domainEventEntity.setData( this.toJsonStringConverter.convert( event ) );

        domainEventsEntity.getDomainEvents().add( domainEventEntity );

        this.domainEventsRepository.save( domainEventsEntity );

    }

    private void processBoardEvent( final Tuple event ) {
        log.debug( "processBoardEvent : enter " );

        String boardUuid = event.getString( "boardUuid" );

        this.domainEventsRepository.findById( boardUuid )
                .ifPresent( found -> {
                    log.debug( "processBoardEvent : a DomainEventsEntity[{}] was found for boardUuid[{}]. ",
                            found,
                            boardUuid);

                    DomainEventEntity domainEventEntity = new DomainEventEntity();
                    domainEventEntity.setId( UUID.randomUUID().toString() );

                    Instant occurredOn = Instant.parse( event.getString( "occurredOn" ) );
                    domainEventEntity.setOccurredOn( LocalDateTime.ofInstant( occurredOn, ZoneOffset.UTC ) );

                    domainEventEntity.setData( toJsonStringConverter.convert( event ) );

                    found.getDomainEvents().add( domainEventEntity );
                    this.domainEventsRepository.save( found );

                });

    }

}
