package io.pivotal.dmfrey.eventStoreDemo.domain.client.eventStore.service;

import io.pivotal.dmfrey.eventStoreDemo.domain.client.BoardClient;
import io.pivotal.dmfrey.eventStoreDemo.domain.model.Board;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;
import static org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties.StubsMode.LOCAL;

@RunWith( SpringRunner.class )
@SpringBootTest( webEnvironment = MOCK,
        properties = {
                "spring.cloud.service-registry.auto-registration.enabled=false",
                "stubrunner.idsToServiceIds.event-store=event-store"
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
public class EventStoryContractTests {

    @Autowired
    private BoardClient client;

    private final UUID boardUuid = UUID.fromString( "ff4795e1-2514-4f5a-90e2-cd33dfadfbf2" );
    private final UUID storyUuid = UUID.fromString( "cf06e86b-046c-407f-8a5e-7212c87499f2" );

    static {

        System.setProperty( "eureka.client.enabled", "false" );
        System.setProperty( "spring.cloud.config.failFast", "false" );

    }

    @Test
    public void testFind() throws Exception {

        Board found = this.client.find( this.boardUuid );
        assertThat( found ).isNotNull();
        assertThat( found.getBoardUuid() ).isEqualTo( this.boardUuid );
        assertThat( found.getName() ).isEqualTo( "My Board" );
        assertThat( found.getStories() ).hasSize( 1 );
        assertThat( found.getStories().get( this.storyUuid ).getStoryUuid() ).isEqualTo( this.storyUuid );
        assertThat( found.getStories().get( this.storyUuid ).getName() ).isEqualTo( "Updated Story 1" );

    }

}
