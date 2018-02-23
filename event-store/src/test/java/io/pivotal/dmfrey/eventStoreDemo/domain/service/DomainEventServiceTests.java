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

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith( SpringRunner.class )
@SpringBootTest
public class DomainEventServiceTests {

    private static final String BOARD_INITIALIZED_EVENT = "{\"eventType\":\"BoardInitialized\",\"boardUuid\":\"ff4795e1-2514-4f5a-90e2-cd33dfadfbf2\",\"occurredOn\":\"2018-02-23T03:49:52.313Z\"}";

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

    private DomainEventsEntity createDomainEventsEntity() {

        DomainEventEntity domainEvent = new DomainEventEntity();
        domainEvent.setId( UUID.randomUUID().toString() );
        domainEvent.setData( BOARD_INITIALIZED_EVENT );
        domainEvent.setOccurredOn( LocalDateTime.now() );
        domainEvent.setBoardUuid( "ff4795e1-2514-4f5a-90e2-cd33dfadfbf2" );

        DomainEventsEntity domainEvents = new DomainEventsEntity();
        domainEvents.setBoardUuid( "ff4795e1-2514-4f5a-90e2-cd33dfadfbf2" );
        domainEvents.setDomainEvents( singleton( domainEvent ) );

        return domainEvents;
    }

}
