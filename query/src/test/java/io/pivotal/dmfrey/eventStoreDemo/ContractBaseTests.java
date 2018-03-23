package io.pivotal.dmfrey.eventStoreDemo;

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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.mockito.Matchers.any;
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

    }

    private Board createBoard() {

        Board board = new Board();
        board.setName( "My Board" );

        Map<UUID, Story> stories = new HashMap<>();
        Story story1 = new Story();
        story1.setStoryUuid( UUID.fromString( "10240df9-4a1e-4fa4-bbd1-0bb33d764603" ) );
        story1.setName( "Story 1" );
        stories.put( story1.getStoryUuid(), story1 );

        Story story2 = new Story();
        story2.setStoryUuid( UUID.fromString( "6f9b00bd-e47a-47ff-84e6-fc0171d0bc89" ) );
        story2.setName( "Story 2" );
        stories.put( story2.getStoryUuid(), story2 );

        board.setStories( stories );

        return board;
    }

}
