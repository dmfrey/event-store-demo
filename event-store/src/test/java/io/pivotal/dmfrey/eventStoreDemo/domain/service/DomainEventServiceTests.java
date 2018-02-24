package io.pivotal.dmfrey.eventStoreDemo.domain.service;

import io.pivotal.dmfrey.eventStoreDemo.domain.model.DomainEvents;
import io.pivotal.dmfrey.eventStoreDemo.domain.persistence.DomainEventEntity;
import io.pivotal.dmfrey.eventStoreDemo.domain.persistence.DomainEventsEntity;
import io.pivotal.dmfrey.eventStoreDemo.domain.persistence.DomainEventsRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.tuple.Tuple;
import org.springframework.tuple.TupleBuilder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith( SpringRunner.class )
@SpringBootTest
public class DomainEventServiceTests {

    private static final String BOARD_INITIALIZED_EVENT = "{\"eventType\":\"BoardInitialized\",\"boardUuid\":\"ff4795e1-2514-4f5a-90e2-cd33dfadfbf2\",\"occurredOn\":\"2018-02-23T03:49:52.313Z\"}";
    private static final String BOARD_RENAMED_EVENT = "{\"eventType\":\"BoardRenamed\",\"boardUuid\":\"ff4795e1-2514-4f5a-90e2-cd33dfadfbf2\",\"occurredOn\":\"2018-02-23T03:51:36.520Z\",\"name\":\"My Board\"}";

    @Autowired
    private DomainEventService service;

    @MockBean
    private DomainEventsRepository repository;

    @MockBean
    private NotificationPublisher notificationPublisher;

    @Test
    public void testGetDomainEvents() throws Exception {

        DomainEventsEntity domainEventsEntity = createDomainEventsEntity();
        when( this.repository.findById( anyString() ) ).thenReturn( Optional.of( domainEventsEntity ) );

        DomainEvents domainEvents = this.service.getDomainEvents( "ff4795e1-2514-4f5a-90e2-cd33dfadfbf2" );
        assertThat( domainEvents ).isNotNull();
        assertThat( domainEvents.getBoardUuid() ).isEqualTo( "ff4795e1-2514-4f5a-90e2-cd33dfadfbf2" );
        assertThat( domainEvents.getDomainEvents() )
                .hasSize( 1 )
                .containsExactly( BOARD_INITIALIZED_EVENT );

        verify( this.repository, times( 1 ) ).findById( anyString() );

    }

    @Test( expected = IllegalArgumentException.class )
    public void testGetDomainEventsNotFound() throws Exception {

        when( this.repository.findById( anyString() ) ).thenThrow( new IllegalArgumentException() );

        this.service.getDomainEvents( "ff4795e1-2514-4f5a-90e2-cd33dfadfbf2" );

        verify( this.repository, times( 1 ) ).findById( anyString() );

    }

    @Test
    public void testProcessBoardInitializedEvent() throws Exception {

        this.service.processDomainEvent( TupleBuilder.fromString( BOARD_INITIALIZED_EVENT ) );

        verify( this.repository, times( 1 ) ).save( any( DomainEventsEntity.class ) );
        verify( this.notificationPublisher, times( 1 ) ).sendNotification( any( Tuple.class ) );

    }

    @Test
    public void testProcessBoardRenamedEvent() throws Exception {

        DomainEventsEntity domainEventsEntity = createDomainEventsEntity();
        when( this.repository.findById( anyString() ) ).thenReturn( Optional.of( domainEventsEntity ) );

        this.service.processDomainEvent( TupleBuilder.fromString( BOARD_RENAMED_EVENT ) );

        verify( this.repository, times( 1 ) ).findById( anyString() );
        verify( this.repository, times( 1 ) ).save( any( DomainEventsEntity.class ) );
        verify( this.notificationPublisher, times( 1 ) ).sendNotification( any( Tuple.class ) );

    }

    private DomainEventsEntity createDomainEventsEntity() {

        DomainEventEntity domainEvent = new DomainEventEntity();
        domainEvent.setId( UUID.randomUUID().toString() );
        domainEvent.setData( BOARD_INITIALIZED_EVENT );
        domainEvent.setOccurredOn( LocalDateTime.now() );
        domainEvent.setBoardUuid( "ff4795e1-2514-4f5a-90e2-cd33dfadfbf2" );

        DomainEventsEntity domainEvents = new DomainEventsEntity();
        domainEvents.setBoardUuid( "ff4795e1-2514-4f5a-90e2-cd33dfadfbf2" );
        domainEvents.getDomainEvents().add( domainEvent );

        return domainEvents;
    }

}
