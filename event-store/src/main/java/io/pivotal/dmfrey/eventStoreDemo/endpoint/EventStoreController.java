package io.pivotal.dmfrey.eventStoreDemo.endpoint;

import io.pivotal.dmfrey.eventStoreDemo.domain.service.DomainEventService;
import org.springframework.http.ResponseEntity;
import org.springframework.tuple.Tuple;
import org.springframework.tuple.TupleBuilder;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class EventStoreController {

    private final DomainEventService service;

    public EventStoreController( final DomainEventService service ) {

        this.service = service;

    }

    @PostMapping( "/" )
    public ResponseEntity saveEvent( @RequestBody String json ) {

        Tuple event = TupleBuilder.fromString( json );

        Assert.isTrue( event.hasFieldName( "eventType" ), "eventType is required" );
        Assert.isTrue( event.hasFieldName( "boardUuid" ), "boardUuid is required" );
        Assert.isTrue( event.hasFieldName( "occurredOn" ), "occurredOn is required" );

        this.service.processDomainEvent( event );

        return ResponseEntity
                .accepted()
                .build();
    }

    @GetMapping( "/{boardUuid}" )
    public ResponseEntity domainEvents( @PathVariable( "boardUuid" ) UUID boardUuid ) {

        return ResponseEntity
                .ok( this.service.getDomainEvents( boardUuid.toString() ) );
    }
}
