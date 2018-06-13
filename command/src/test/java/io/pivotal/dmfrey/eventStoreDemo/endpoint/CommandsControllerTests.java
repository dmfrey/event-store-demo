package io.pivotal.dmfrey.eventStoreDemo.endpoint;

import io.pivotal.dmfrey.eventStoreDemo.domain.service.BoardService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith( SpringRunner.class )
@WebMvcTest( value = CommandsController.class, secure = false )
public class CommandsControllerTests {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    BoardService service;

    @Test
    public void testCreateBoard() throws Exception {

        UUID boardUuid = UUID.randomUUID();
        when( this.service.createBoard() ).thenReturn( boardUuid );

        this.mockMvc.perform( post( "/boards/" ).param( "name", "Test Board" ) )
                .andDo( print() )
                .andExpect( status().isCreated() )
                .andExpect( header().string( HttpHeaders.LOCATION, is( equalTo( "http://localhost/boards/" + boardUuid.toString() ) ) ) );

        verify( this.service, times( 1 ) ).createBoard();

    }

    @Test
    public void testRenameBoard() throws Exception {

        UUID boardUuid = UUID.randomUUID();

        this.mockMvc.perform( patch( "/boards/{boardUuid}", boardUuid ).param( "name", "Test Board" ) )
                .andDo( print() )
                .andExpect( status().isAccepted() );;

        verify( this.service, times( 1 ) ).renameBoard( any( UUID.class ), anyString() );

    }

    @Test
    public void testCreateStoryOnBoard() throws Exception {

        UUID boardUuid = UUID.randomUUID();
        UUID storyUuid = UUID.randomUUID();
        when( this.service.addStory( any( UUID.class ), anyString() ) ).thenReturn( storyUuid );

        this.mockMvc.perform( post( "/boards/{boardUuid}/stories", boardUuid ).param( "name", "Test Story" ) )
                .andDo( print() )
                .andExpect( status().isCreated() )
                .andExpect( header().string( HttpHeaders.LOCATION, is( equalTo( "http://localhost/boards/" + boardUuid.toString() + "/stories/" + storyUuid.toString() ) ) ) );

        verify( this.service, times( 1 ) ).addStory( any( UUID.class ), anyString() );

    }

    @Test
    public void testUpdateStoryOnBoard() throws Exception {

        UUID boardUuid = UUID.randomUUID();
        UUID storyUuid = UUID.randomUUID();

        this.mockMvc.perform( put( "/boards/{boardUuid}/stories/{storyUuid}", boardUuid, storyUuid ).param( "name", "Test Story Updated" ) )
                .andDo( print() )
                .andExpect( status().isAccepted() );

        verify( this.service, times( 1 ) ).updateStory( any( UUID.class ), any( UUID.class ), anyString() );

    }

    @Test
    public void testDeleteStoryOnBoard() throws Exception {

        UUID boardUuid = UUID.randomUUID();
        UUID storyUuid = UUID.randomUUID();

        this.mockMvc.perform( delete( "/boards/{boardUuid}/stories/{storyUuid}", boardUuid, storyUuid ) )
                .andDo( print() )
                .andExpect( status().isAccepted() );

        verify( this.service, times( 1 ) ).deleteStory( any( UUID.class ), any( UUID.class ) );

    }

}
