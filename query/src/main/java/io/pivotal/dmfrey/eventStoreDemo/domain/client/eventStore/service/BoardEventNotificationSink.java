package io.pivotal.dmfrey.eventStoreDemo.domain.client.eventStore.service;

import io.pivotal.dmfrey.eventStoreDemo.domain.service.BoardService;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.context.annotation.Profile;
import org.springframework.tuple.Tuple;
import org.springframework.tuple.TupleBuilder;
import org.springframework.util.Assert;

import java.util.UUID;

@Profile( "event-store" )
@EnableBinding( Sink.class )
public class BoardEventNotificationSink {

    private final BoardService service;

    public BoardEventNotificationSink( final BoardService service ) {
        this.service = service;

    }

    @StreamListener( Sink.INPUT )
    public void processNotification( final String json ) {

        Tuple event = TupleBuilder.fromString( json );

        Assert.hasText( event.getString( "eventType" ), "eventType not set" );
        Assert.hasText( event.getString( "boardUuid" ), "boardUuid not set" );
        Assert.hasText( event.getString( "occurredOn" ), "occurredOn not set" );

        String eventType = event.getString( "eventType" );
        if( eventType.equals( "BoardInitialized" ) ) {

            return;
        }

        this.service.uncacheTarget( UUID.fromString( event.getString( "boardUuid" ) ) );

    }

}
