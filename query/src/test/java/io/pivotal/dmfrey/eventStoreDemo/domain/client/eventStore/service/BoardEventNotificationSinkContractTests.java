package io.pivotal.dmfrey.eventStoreDemo.domain.client.eventStore.service;

import io.pivotal.dmfrey.eventStoreDemo.domain.service.BoardService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.stubrunner.StubTrigger;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;
import static org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties.StubsMode.LOCAL;

@RunWith( SpringRunner.class )
@SpringBootTest( webEnvironment = MOCK,
        properties = {
                "spring.cloud.service-registry.auto-registration.enabled=false"
        }
)
@ActiveProfiles( "event-store" )
@AutoConfigureStubRunner(
        ids = {
                "io.pivotal.dmfrey:event-store:+:9082"
        },
        stubsMode = LOCAL
)
@DirtiesContext
public class BoardEventNotificationSinkContractTests {

    @Autowired
    private StubTrigger stubTrigger;

    @MockBean
    private BoardService service;

    static {

        System.setProperty( "eureka.client.enabled", "false" );
        System.setProperty( "spring.cloud.config.failFast", "false" );

    }

    @Test
    public void testReceiveBoardInitializedEvent() {

        this.stubTrigger.trigger( "should_publish_board_initialized" );

        verify( this.service, times( 0 ) ).uncacheTarget( any( UUID.class ) );
        verifyNoMoreInteractions( this.service );

    }

    @Test
    public void testReceiveBoardRenamedEvent() {

        this.stubTrigger.trigger( "should_publish_board_renamed" );

        verify( this.service, times( 1 ) ).uncacheTarget( any( UUID.class ) );
        verifyNoMoreInteractions( this.service );

    }

    @Test
    public void testReceiveStoryAddedEvent() {

        this.stubTrigger.trigger( "should_publish_story_added" );

        verify( this.service, times( 1 ) ).uncacheTarget( any( UUID.class ) );
        verifyNoMoreInteractions( this.service );

    }

    @Test
    public void testReceiveStoryUpdatedEvent() {

        this.stubTrigger.trigger( "should_publish_story_updated" );

        verify( this.service, times( 1 ) ).uncacheTarget( any( UUID.class ) );
        verifyNoMoreInteractions( this.service );

    }

    @Test
    public void testReceiveStoryDeletedEvent() {

        this.stubTrigger.trigger( "should_publish_story_deleted" );

        verify( this.service, times( 1 ) ).uncacheTarget( any( UUID.class ) );
        verifyNoMoreInteractions( this.service );

    }

}
