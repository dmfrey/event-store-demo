package io.pivotal.dmfrey.eventStoreDemo.domain.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith( SpringRunner.class )
@SpringBootTest(
        properties = {
                "--spring.cloud.service-registry.auto-registration.enabled=false"
        }
)
@ActiveProfiles( "event-store" )
public class BoardEventNotificationSinkTests {

    private static final String BOARD_INITIALIZED_EVENT = "{\"eventType\":\"BoardInitialized\",\"boardUuid\":\"ff4795e1-2514-4f5a-90e2-cd33dfadfbf2\",\"occurredOn\":\"2018-02-23T03:49:52.313Z\"}";
    private static final String BOARD_RENAMED_EVENT = "{\"eventType\":\"BoardRenamed\",\"boardUuid\":\"ff4795e1-2514-4f5a-90e2-cd33dfadfbf2\",\"occurredOn\":\"2018-02-23T03:51:36.520Z\",\"name\":\"My Board\"}";
    private static final String STORY_ADDED_EVENT = "{\"eventType\":\"StoryAdded\",\"boardUuid\":\"ff4795e1-2514-4f5a-90e2-cd33dfadfbf2\",\"occurredOn\":\"2018-02-23T03:52:15.876Z\",\"storyUuid\":\"242500df-373e-4e70-90bc-3c8cd54c81d8\",\"name\":\"My Story 1\",\"story\":{\"name\":\"My Story 1\"}}";
    private static final String STORY_UPDATED_EVENT = "{\"eventType\":\"StoryUpdated\",\"boardUuid\":\"ff4795e1-2514-4f5a-90e2-cd33dfadfbf2\",\"occurredOn\":\"2018-02-23T03:52:15.876Z\",\"storyUuid\":\"242500df-373e-4e70-90bc-3c8cd54c81d8\",\"name\":\"My Story 1 Updated\",\"story\":{\"name\":\"My Story 1 Updated\"}}";
    private static final String STORY_DELETED_EVENT = "{\"eventType\":\"StoryDeleted\",\"boardUuid\":\"ff4795e1-2514-4f5a-90e2-cd33dfadfbf2\",\"occurredOn\":\"2018-02-23T03:52:15.876Z\",\"storyUuid\":\"242500df-373e-4e70-90bc-3c8cd54c81d8\"}";

    @Autowired
    private Sink channels;

    @MockBean
    private BoardService service;

    @Test
    public void testProcessNotificationBoardInitialized() throws Exception {

        Message message = MessageBuilder.withPayload( BOARD_INITIALIZED_EVENT ).setHeader( MessageHeaders.CONTENT_TYPE,"application/x-spring-tuple" ).build();
        this.channels.input().send( message );

        verify( this.service, times( 0 ) ).uncacheTarget( any( UUID.class ) );

    }

    @Test
    public void testProcessNotificationBoardRenamed() throws Exception {

        this.channels.input().send( new GenericMessage<>( BOARD_RENAMED_EVENT ) );

        verify( this.service, times( 1 ) ).uncacheTarget( any( UUID.class ) );

    }

    @Test
    public void testProcessNotificationStoryAdded() throws Exception {

        this.channels.input().send( new GenericMessage<>( STORY_ADDED_EVENT ) );

        verify( this.service, times( 1 ) ).uncacheTarget( any( UUID.class ) );

    }

    @Test
    public void testProcessNotificationStoryUpdated() throws Exception {

        this.channels.input().send( new GenericMessage<>( STORY_UPDATED_EVENT ) );

        verify( this.service, times( 1 ) ).uncacheTarget( any( UUID.class ) );

    }

    @Test
    public void testProcessNotificationStoryDeleted() throws Exception {

        this.channels.input().send( new GenericMessage<>( STORY_DELETED_EVENT ) );

        verify( this.service, times( 1 ) ).uncacheTarget( any( UUID.class ) );

    }

}
