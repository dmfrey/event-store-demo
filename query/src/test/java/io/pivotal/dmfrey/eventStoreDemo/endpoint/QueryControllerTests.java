package io.pivotal.dmfrey.eventStoreDemo.endpoint;

import io.pivotal.dmfrey.eventStoreDemo.domain.model.Board;
import io.pivotal.dmfrey.eventStoreDemo.domain.service.BoardService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith( SpringRunner.class )
@WebMvcTest( QueryController.class )
public class QueryControllerTests {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    BoardService service;

    @Test
    public void testBoard() throws Exception {

        Board board = createBoard();
        when( this.service.find( any( UUID.class ) ) ).thenReturn( board );

        this.mockMvc.perform( get( "/boards/{boardUuid}", board.getBoardUuid() ) )
                .andExpect( status().isOk() )
                .andDo( print() )
                .andExpect( jsonPath( "$.name", is( equalTo( board.getName() ) ) ) )
                .andExpect( jsonPath( "$.backlog", is( nullValue() ) ) );

        verify( this.service, times( 1 ) ).find( any( UUID.class ) );
        verifyNoMoreInteractions( this.service );

    }

    private Board createBoard() {

        Board board = new Board();
        board.setBoardUuid( UUID.fromString( "ff4795e1-2514-4f5a-90e2-cd33dfadfbf2" ) );

        return board;
    }

}
