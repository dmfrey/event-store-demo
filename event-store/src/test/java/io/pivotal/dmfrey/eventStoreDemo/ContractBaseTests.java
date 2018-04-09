package io.pivotal.dmfrey.eventStoreDemo;

import io.pivotal.dmfrey.eventStoreDemo.domain.model.DomainEvents;
import io.pivotal.dmfrey.eventStoreDemo.domain.service.DomainEventService;
import io.pivotal.dmfrey.eventStoreDemo.domain.service.NotificationPublisher;
import io.pivotal.dmfrey.eventStoreDemo.domain.service.converters.TupleToJsonStringConverter;
import io.pivotal.dmfrey.eventStoreDemo.endpoint.EventStoreController;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.verifier.messaging.MessageVerifier;
import org.springframework.cloud.contract.verifier.messaging.boot.AutoConfigureMessageVerifier;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.tuple.Tuple;
import org.springframework.tuple.TupleBuilder;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith( SpringRunner.class )
@SpringBootTest(
        properties = {
                "spring.cloud.service-registry.auto-registration.enabled=false"
        }
)
@AutoConfigureMessageVerifier
public abstract class ContractBaseTests {

    private static final String BOARD_INITIALIZED = "{\"eventType\":\"BoardInitialized\",\"boardUuid\":\"ff4795e1-2514-4f5a-90e2-cd33dfadfbf2\",\"occurredOn\":\"2018-02-23T03:49:52.313Z\"}";
    private static final String BOARD_RENAMED = "{\"eventType\":\"BoardRenamed\",\"boardUuid\":\"ff4795e1-2514-4f5a-90e2-cd33dfadfbf2\",\"occurredOn\":\"2018-02-23T03:49:52.313Z\", \"name\": \"My Board\"}";
    private static final String STORY_ONE_ADDED = "{\"eventType\":\"StoryAdded\",\"boardUuid\":\"ff4795e1-2514-4f5a-90e2-cd33dfadfbf2\",\"occurredOn\":\"2018-02-23T03:49:52.313Z\", \"storyUuid\": \"cf06e86b-046c-407f-8a5e-7212c87499f2\", \"name\": \"Story 1\"}";
    private static final String STORY_ONE_UPDATED = "{\"eventType\":\"StoryUpdated\",\"boardUuid\":\"ff4795e1-2514-4f5a-90e2-cd33dfadfbf2\",\"occurredOn\":\"2018-02-23T03:49:52.313Z\", \"storyUuid\": \"cf06e86b-046c-407f-8a5e-7212c87499f2\", \"name\": \"Updated Story 1\"}";
    private static final String STORY_TWO_ADDED = "{\"eventType\":\"StoryAdded\",\"boardUuid\":\"ff4795e1-2514-4f5a-90e2-cd33dfadfbf2\",\"occurredOn\":\"2018-02-23T03:49:52.313Z\", \"storyUuid\": \"d5466d21-b78d-4640-8528-b950b8667bfb\", \"name\": \"Story 2\"}";
    private static final String STORY_TWO_UPDATED = "{\"eventType\":\"StoryUpdated\",\"boardUuid\":\"ff4795e1-2514-4f5a-90e2-cd33dfadfbf2\",\"occurredOn\":\"2018-02-23T03:49:52.313Z\", \"storyUuid\": \"d5466d21-b78d-4640-8528-b950b8667bfb\", \"name\": \"Updated Story 2\"}";
    private static final String STORY_TWO_DELETED = "{\"eventType\":\"StoryDeleted\",\"boardUuid\":\"ff4795e1-2514-4f5a-90e2-cd33dfadfbf2\",\"occurredOn\":\"2018-02-23T03:49:52.313Z\", \"storyUuid\": \"d5466d21-b78d-4640-8528-b950b8667bfb\"}";

    @Autowired
    private MessageVerifier verifier;

    @Autowired
    private EventStoreController controller;

    @Autowired
    private NotificationPublisher publisher;

    @MockBean
    private DomainEventService service;

    static {
        System.setProperty( "eureka.client.enabled", "false" );
        System.setProperty( "spring.cloud.config.failFast", "false" );
    }

    @Before
    public void setup() {

        doNothing().when( this.service ).processDomainEvent( any( Tuple.class ) );
        when( this.service.getDomainEvents( anyString() ) ).thenReturn( createDomainEvents() );

        RestAssuredMockMvc.standaloneSetup( this.controller );

        verifyNoMoreInteractions( this.service );

    }

    public void shouldPublishBoardInitialized() throws IOException {

        Tuple event = TupleBuilder.fromString( BOARD_INITIALIZED );
        this.publisher.sendNotification( event );

    }

    public void shouldPublishBoardRenamed() throws IOException {

        Tuple event = TupleBuilder.fromString( BOARD_RENAMED );
        this.publisher.sendNotification( event );

    }

    public void shouldPublishStoryAdded() throws IOException {

        Tuple event = TupleBuilder.fromString( STORY_TWO_ADDED );
        this.publisher.sendNotification( event );

    }

    public void shouldPublishStoryUpdated() throws IOException {

        Tuple event = TupleBuilder.fromString( STORY_TWO_UPDATED );
        this.publisher.sendNotification( event );

    }

    public void shouldPublishStoryDeleted() throws IOException {

        Tuple event = TupleBuilder.fromString( STORY_TWO_DELETED );
        this.publisher.sendNotification( event );

    }

    private DomainEvents createDomainEvents() {

        DomainEvents domainEvents = new DomainEvents();
        domainEvents.setBoardUuid( "ff4795e1-2514-4f5a-90e2-cd33dfadfbf2" );

        List<String> events = Arrays.asList( BOARD_INITIALIZED, BOARD_RENAMED, STORY_ONE_ADDED, STORY_ONE_UPDATED, STORY_TWO_ADDED, STORY_TWO_UPDATED, STORY_TWO_DELETED );
        domainEvents.setDomainEvents( events );

        return domainEvents;
    }

}
