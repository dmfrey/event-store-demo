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

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith( SpringRunner.class )
//@SpringBootTest(
//        properties = {
//                "spring.cloud.service-registry.auto-registration.enabled=false"
//        }
//)
@WebMvcTest( QueryController.class )
//@AutoConfigureMockMvc
public class QueryControllerTests {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    BoardService service;

    private UUID boardUuid = UUID.fromString( "ff4795e1-2514-4f5a-90e2-cd33dfadfbf2" );

    @Test
    public void testBoard() throws Exception {

        Board board = createBoard();
        when( this.service.find( any( UUID.class ) ) ).thenReturn( board );

        this.mockMvc.perform( get( "/boards/{boardUuid}", board.getBoardUuid() ) )
                .andExpect( status().isOk() )
                .andDo( print() )
                .andExpect( jsonPath( "$.name", is( board.getName() ) ) )
                .andExpect( jsonPath( "$.backlog", is( notNullValue() ) ) );

        verify( this.service, times( 1 ) ).find( boardUuid );
        verifyNoMoreInteractions( this.service );

    }

    private Board createBoard() {

        return new Board( boardUuid );
    }

}
