package io.pivotal.dmfrey.eventStoreDemo.endpoint;

import io.pivotal.dmfrey.eventStoreDemo.domain.model.Board;
import io.pivotal.dmfrey.eventStoreDemo.domain.service.BoardService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URI;
import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith( SpringRunner.class )
@WebMvcTest( ApiController.class )
@AutoConfigureRestDocs( outputDir = "build/generated-snippets", uriPort = 8765 )
public class ApiControllerTests {

    private static final String BOARD_JSON = "{\"name\":\"My Board\",\"backlog\":[{\"storyUuid\":\"242500df-373e-4e70-90bc-3c8cd54c81d8\",\"name\":\"My Story 1\"}]}";

    @Autowired
    MockMvc mockMvc;

    @MockBean
    BoardService service;

    @Test
    public void testCreateBoard() throws Exception {

        UUID boardUuid = UUID.randomUUID();
        when( this.service.createBoard() ).thenReturn( ResponseEntity.created( URI.create( "http://localhost/boards/" + boardUuid.toString() ) ).build() );

        this.mockMvc.perform( post( "/boards" ) )
                .andDo( print() )
                .andExpect( status().isCreated() )
                .andExpect( header().string( HttpHeaders.LOCATION, is( equalTo( "http://localhost/boards/" + boardUuid.toString() ) ) ) )
                .andDo( document("create-board" ) );

        verify( this.service, times( 1 ) ).createBoard();

    }

    @Test
    public void testRenameBoard() throws Exception {

        UUID boardUuid = UUID.randomUUID();
        when( this.service.renameBoard( any( UUID.class ), anyString() ) ).thenReturn( ResponseEntity.accepted().build() );

        this.mockMvc.perform( patch( "/boards/{boardUuid}", boardUuid ).param( "name", "Test Board" ) )
                .andDo( print() )
                .andExpect( status().isAccepted() )
                .andDo( document("rename-board",
                        pathParameters(
                                parameterWithName( "boardUuid" ).description( "The unique id of the board" )
                        ),
                        requestParameters(
                                parameterWithName( "name" ).description( "The new name of the Board" )
                        )
                ));

        verify( this.service, times( 1 ) ).renameBoard( any( UUID.class ), anyString() );

    }

    @Test
    public void testCreateStoryOnBoard() throws Exception {

        UUID boardUuid = UUID.randomUUID();
        UUID storyUuid = UUID.randomUUID();
        when( this.service.addStory( any( UUID.class ), anyString() ) ).thenReturn( ResponseEntity.created( URI.create( "http://localhost/boards/" + boardUuid.toString() + "/stories/" + storyUuid.toString() ) ).build() );

        this.mockMvc.perform( post( "/boards/{boardUuid}/stories", boardUuid ).param( "name", "Test Story" ) )
                .andDo( print() )
                .andExpect( status().isCreated() )
                .andExpect( header().string( HttpHeaders.LOCATION, is( equalTo( "http://localhost/boards/" + boardUuid.toString() + "/stories/" + storyUuid.toString() ) ) ) )
                .andDo( document("add-story",
                        pathParameters(
                                parameterWithName( "boardUuid" ).description( "The unique id of the board" )
                        ),
                        requestParameters(
                                parameterWithName( "name" ).description( "The new story to add to the Board" )
                        )
                ));

        verify( this.service, times( 1 ) ).addStory( any( UUID.class ), anyString() );

    }

    @Test
    public void testUpdateStoryOnBoard() throws Exception {

        UUID boardUuid = UUID.randomUUID();
        UUID storyUuid = UUID.randomUUID();
        when( this.service.updateStory( any( UUID.class ), any( UUID.class ), anyString() ) ).thenReturn( ResponseEntity.accepted().build() );

        this.mockMvc.perform( put( "/boards/{boardUuid}/stories/{storyUuid}", boardUuid, storyUuid ).param( "name", "Test Story Updated" ) )
                .andDo( print() )
                .andExpect( status().isAccepted() )
                .andDo( document("update-story",
                        pathParameters(
                                parameterWithName( "boardUuid" ).description( "The unique id of the board" ),
                                parameterWithName( "storyUuid" ).description( "The unique id of the story on the board" )
                        ),
                        requestParameters(
                                parameterWithName( "name" ).description( "The new name of the Board" )
                        )
                ));


        verify( this.service, times( 1 ) ).updateStory( any( UUID.class ), any( UUID.class ), anyString() );

    }

    @Test
    public void testDeleteStoryOnBoard() throws Exception {

        UUID boardUuid = UUID.randomUUID();
        UUID storyUuid = UUID.randomUUID();
        when( this.service.deleteStory( any( UUID.class ), any( UUID.class ) ) ).thenReturn( ResponseEntity.accepted().build() );

        this.mockMvc.perform( RestDocumentationRequestBuilders.delete( "/boards/{boardUuid}/stories/{storyUuid}", boardUuid, storyUuid ) )
                .andDo( print() )
                .andExpect( status().isAccepted() )
                .andDo( document("delete-story",
                        pathParameters(
                                parameterWithName( "boardUuid" ).description( "The unique id of the board" ),
                                parameterWithName( "storyUuid" ).description( "The unique id of the story on the board to be deleted" )
                        )
                ));

        verify( this.service, times( 1 ) ).deleteStory( any( UUID.class ), any( UUID.class ) );

    }

    @Test
    public void testBoard() throws Exception {

        when( this.service.board( any( UUID.class ) ) ).thenReturn( new ResponseEntity<>( craeteBoard(), HttpStatus.OK ) );

        this.mockMvc.perform( get( "/boards/{boardUuid}", UUID.randomUUID() ) )
                .andExpect( status().isOk() )
                .andDo( print() )
                .andExpect( jsonPath( "$.name", is( equalTo( "My Board" ) ) ) )
                .andExpect( jsonPath( "$.backlog" ).isArray() )
                .andDo( document("get-board",
                        pathParameters(
                                parameterWithName( "boardUuid" ).description( "The unique id of the board" )
                        )
                ));


        verify( this.service, times( 1 ) ).board( any( UUID.class ) );

    }

    private Board craeteBoard() {

        Board board = new Board();
        board.setName( "My Board" );

        return board;
    }

}
