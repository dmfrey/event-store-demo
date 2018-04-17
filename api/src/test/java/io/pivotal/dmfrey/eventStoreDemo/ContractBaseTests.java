package io.pivotal.dmfrey.eventStoreDemo;

import io.pivotal.dmfrey.eventStoreDemo.domain.model.Board;
import io.pivotal.dmfrey.eventStoreDemo.domain.model.Story;
import io.pivotal.dmfrey.eventStoreDemo.domain.service.BoardService;
import io.pivotal.dmfrey.eventStoreDemo.endpoint.ApiController;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.verifier.messaging.MessageVerifier;
import org.springframework.cloud.contract.verifier.messaging.boot.AutoConfigureMessageVerifier;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URI;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
    private ApiController controller;

    @MockBean
    private BoardService service;

    private String boardUuid = "11111111-90ab-cdef-1234-567890abcdef";
    private String storyOneUuid = "10240df9-4a1e-4fa4-bbd1-0bb33d764603";
    private String storyTwoUuid = "6f9b00bd-e47a-47ff-84e6-fc0171d0bc89";

    @Before
    public void setup() {

        when( this.service.createBoard() ).thenReturn( ResponseEntity.created( URI.create( "http://localhost/boards/" + this.boardUuid ) ).build() );
        when( this.service.board( any( UUID.class ) ) ).thenReturn( ResponseEntity.ok( createBoard() ) );
        when( this.service.renameBoard( any( UUID.class ), anyString() ) ).thenReturn( ResponseEntity.accepted().build() );
        when( this.service.addStory( any( UUID.class ), anyString() ) ).thenReturn( ResponseEntity.created( URI.create( "http://localhost/boards/" + this.boardUuid + "/stories/" + this.storyOneUuid ) ).build() );
        when( this.service.updateStory( any( UUID.class ), any( UUID.class ), anyString() ) ).thenReturn( ResponseEntity.accepted().build() );
        when( this.service.deleteStory( any( UUID.class ), any( UUID.class ) ) ).thenReturn( ResponseEntity.accepted().build() );

        RestAssuredMockMvc.standaloneSetup( this.controller );

        verifyNoMoreInteractions( this.service );

    }

    private Board createBoard() {

        Board board = new Board();
        board.setName( "My Board" );

        List<Story> stories = new ArrayList<>();
        Story story1 = new Story();
        story1.setStoryUuid( UUID.fromString( storyOneUuid ) );
        story1.setName( "Story 1" );
        stories.add( story1 );

        Story story2 = new Story();
        story2.setStoryUuid( UUID.fromString( storyTwoUuid ) );
        story2.setName( "Story 2" );
        stories.add( story2 );

        board.setBacklog( stories );

        return board;
    }

}
