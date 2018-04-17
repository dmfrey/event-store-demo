package io.pivotal.dmfrey.eventStoreDemo.domain.service;

import io.pivotal.dmfrey.eventStoreDemo.domain.client.BoardClient;
import io.pivotal.dmfrey.eventStoreDemo.domain.config.QueryConfig;
import io.pivotal.dmfrey.eventStoreDemo.domain.model.Board;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith( SpringRunner.class )
@SpringBootTest( classes = { QueryConfig.class } )
public class BoardServiceTests {

    @Autowired
    private BoardService service;

    @MockBean
    private BoardClient client;

    @Test
    public void testFind() throws Exception {

        Board board = createBoard();
        when( this.client.find( any( UUID.class ) ) ).thenReturn( board );

        Board found = this.service.find( board.getBoardUuid() );
        assertThat( found ).isNotNull();
        assertThat( found.getBoardUuid() ).isEqualTo( board.getBoardUuid() );
        assertThat( found.getName() ).isEqualTo( found.getName() );
        assertThat( found.getStories() ).hasSize( 0 );

        verify( this.client, times( 1 ) ).find( any( UUID.class ) );
        verifyNoMoreInteractions( this.client );

    }

    @Test( expected = IllegalArgumentException.class )
    public void testFindNotFound() throws Exception {

        when( this.client.find( any( UUID.class ) ) ).thenThrow( new IllegalArgumentException() );

        this.service.find( UUID.randomUUID() );

        verify( this.client, times( 1 ) ).find( any( UUID.class ) );
        verifyNoMoreInteractions( this.client );

    }

    @Test
    public void testRemoveFromCache() throws Exception {

        this.service.uncacheTarget( UUID.randomUUID() );

        verify( this.client ).removeFromCache( any( UUID.class ) );
        verifyNoMoreInteractions( this.client );

    }

    private Board createBoard() {

        return new Board( UUID.fromString( "ff4795e1-2514-4f5a-90e2-cd33dfadfbf2" ) );
    }

}
