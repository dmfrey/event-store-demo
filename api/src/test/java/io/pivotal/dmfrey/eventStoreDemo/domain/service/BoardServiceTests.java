package io.pivotal.dmfrey.eventStoreDemo.domain.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.pivotal.dmfrey.eventStoreDemo.domain.config.ApiConfig;
import io.pivotal.dmfrey.eventStoreDemo.domain.config.RestConfig;
import io.pivotal.dmfrey.eventStoreDemo.domain.model.Board;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.net.URI;
import java.util.UUID;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith( SpringRunner.class )
@SpringBootTest( classes = ApiConfig.class )
@JsonTest
public class BoardServiceTests {

    private static final String BOARD_JSON = "{\"name\":\"My Board\",\"backlog\":[{\"storyUuid\":\"242500df-373e-4e70-90bc-3c8cd54c81d8\",\"name\":\"My Story 1\"}]}";

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private BoardService service;

    @MockBean
    private RestConfig.CommandClient commandClient;

    @MockBean
    private RestConfig.QueryClient queryClient;

    private UUID boardUuid = UUID.randomUUID();
    private UUID storyUuid = UUID.randomUUID();

    @Test
    public void testCreateBoard() throws Exception {

        when( this.commandClient.createBoard() ).thenReturn( ResponseEntity.created( URI.create( "http://localhost/boards/" + boardUuid.toString() ) ).build() );

        ResponseEntity response = this.service.createBoard();
        assertThat( response.getStatusCode() ).isEqualTo( HttpStatus.CREATED );
        assertThat( response.getHeaders() )
                .containsKey( HttpHeaders.LOCATION )
                .containsValue( singletonList( "http://localhost/boards/" + boardUuid.toString() ) );

        verify( this.commandClient, times( 1 ) ).createBoard();
        verifyNoMoreInteractions( this.commandClient );
        verifyZeroInteractions( this.queryClient );

    }

    @Test
    public void testRenameBoard() throws Exception {

        when( this.commandClient.renameBoard( any( UUID.class ), anyString() ) ).thenReturn( ResponseEntity.accepted().build() );

        ResponseEntity response = this.service.renameBoard( boardUuid, "My Board" );
        assertThat( response.getStatusCode() ).isEqualTo( HttpStatus.ACCEPTED );

        verify( this.commandClient, times( 1 ) ).renameBoard( boardUuid, "My Board" );
        verifyNoMoreInteractions( this.commandClient );
        verifyZeroInteractions( this.queryClient );

    }

    @Test
    public void testAddStory() throws Exception {

        when( this.commandClient.addStory( any( UUID.class ), anyString() ) ).thenReturn( ResponseEntity.created( URI.create( "http://localhost/boards/" + boardUuid.toString() + "/stories/" + storyUuid.toString() ) ).build() );

        ResponseEntity response = this.service.addStory( boardUuid, "My Story 1" );
        assertThat( response.getStatusCode() ).isEqualTo( HttpStatus.CREATED );

        verify( this.commandClient, times( 1 ) ).addStory( boardUuid, "My Story 1" );
        verifyNoMoreInteractions( this.commandClient );
        verifyZeroInteractions( this.queryClient );

    }

    @Test
    public void testUpdateStory() throws Exception {

        when( this.commandClient.updateStory( any( UUID.class ), any( UUID.class ), anyString() ) ).thenReturn( ResponseEntity.accepted().build() );

        ResponseEntity response = this.service.updateStory( boardUuid, storyUuid, "My Story Updated" );
        assertThat( response.getStatusCode() ).isEqualTo( HttpStatus.ACCEPTED );

        verify( this.commandClient, times( 1 ) ).updateStory( boardUuid, storyUuid, "My Story Updated" );
        verifyNoMoreInteractions( this.commandClient );
        verifyZeroInteractions( this.queryClient );

    }

    @Test
    public void testDeleteStory() throws Exception {

        UUID boardUuid = UUID.randomUUID();
        UUID storyUuid = UUID.randomUUID();
        when( this.commandClient.deleteStory( any( UUID.class ), any( UUID.class ) ) ).thenReturn( ResponseEntity.accepted().build() );

        ResponseEntity response = this.service.deleteStory( boardUuid, storyUuid );
        assertThat( response.getStatusCode() ).isEqualTo( HttpStatus.ACCEPTED );

        verify( this.commandClient, times( 1 ) ).deleteStory( boardUuid, storyUuid );
        verifyNoMoreInteractions( this.commandClient );
        verifyZeroInteractions( this.queryClient );

    }

    @Test
    public void testBoardStory() throws Exception {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType( MediaType.APPLICATION_JSON );

        Board requestedBoard = craeteBoard();

        when( this.queryClient.board( any( UUID.class ) ) ).thenReturn( new ResponseEntity<>( requestedBoard, headers, HttpStatus.OK ) );

        ResponseEntity<Board> response = this.service.board( boardUuid );

        Board expectedBoard = craeteBoard();

        assertThat( response.getStatusCode() ).isEqualTo( HttpStatus.OK );
        assertThat( response.getBody() ).isEqualTo( expectedBoard );

        verify( this.queryClient, times( 1 ) ).board( boardUuid );
        verifyZeroInteractions( this.commandClient );
        verifyNoMoreInteractions( this.queryClient );

    }

    private Board craeteBoard() throws IOException {

        return mapper.readValue( BOARD_JSON, Board.class );
    }

}
