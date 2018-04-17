package io.pivotal.dmfrey.eventStoreDemo;

import io.pivotal.dmfrey.eventStoreDemo.domain.events.BoardInitialized;
import io.pivotal.dmfrey.eventStoreDemo.domain.events.BoardRenamed;
import io.pivotal.dmfrey.eventStoreDemo.domain.events.StoryAdded;
import io.pivotal.dmfrey.eventStoreDemo.domain.model.Board;
import io.pivotal.dmfrey.eventStoreDemo.domain.model.Story;
import io.pivotal.dmfrey.eventStoreDemo.domain.service.BoardService;
import io.pivotal.dmfrey.eventStoreDemo.endpoint.QueryController;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.verifier.messaging.MessageVerifier;
import org.springframework.cloud.contract.verifier.messaging.boot.AutoConfigureMessageVerifier;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith( SpringRunner.class )
@SpringBootTest(
        properties = {
                "--spring.cloud.service-registry.auto-registration.enabled=false"
        }
)
@AutoConfigureMessageVerifier
public abstract class ContractBaseTests {

    @Autowired
    private MessageVerifier verifier;

    @Autowired
    private QueryController controller;

    @MockBean
    private BoardService service;

    @Before
    public void setup() {

        when( this.service.find( any( UUID.class )) ).thenReturn( createBoard() );

        RestAssuredMockMvc.standaloneSetup( this.controller );

        verifyNoMoreInteractions( this.service );

    }

    private Board createBoard() {

        UUID boardUuid = UUID.randomUUID();
        BoardInitialized boardInitialized = new BoardInitialized( boardUuid, Instant.now() );
        BoardRenamed boardRenamed = new BoardRenamed( "My Board", boardUuid, Instant.now() );
        StoryAdded storyAdded1 = new StoryAdded( UUID.fromString( "10240df9-4a1e-4fa4-bbd1-0bb33d764603" ), "Story 1", boardUuid, Instant.now() );
        StoryAdded storyAdded2 = new StoryAdded( UUID.fromString( "6f9b00bd-e47a-47ff-84e6-fc0171d0bc89" ), "Story 2", boardUuid, Instant.now() );

        return Board.createFrom( boardUuid, Arrays.asList( boardInitialized, boardRenamed, storyAdded1, storyAdded2 ) );
    }

}
