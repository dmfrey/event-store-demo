package io.pivotal.dmfrey.eventStoreDemo.domain.service;

import io.pivotal.dmfrey.eventStoreDemo.domain.client.BoardClient;
import io.pivotal.dmfrey.eventStoreDemo.domain.model.Board;
import io.pivotal.dmfrey.eventStoreDemo.domain.model.Story;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith( SpringRunner.class )
@SpringBootTest( classes = { BoardService.class },
        properties = {
            "--spring.cloud.service-registry.auto-registration.enabled=false"
        }
)
public class BoardServiceTests {

    @Autowired
    BoardService service;

    @MockBean
    BoardClient client;

    @Test
    public void testCreateBoard() throws Exception {

        UUID boardUuid = this.service.createBoard();
        assertThat( boardUuid ).isNotNull();

        verify( this.client, times( 1 ) ).save( any( Board.class ) );
        verifyNoMoreInteractions( this.client );

    }

    @Test
    public void testRenameBoard() throws Exception {

        UUID boardUuid = UUID.randomUUID();
        Board board = createTestBoard( boardUuid );

        when( this.client.find( any( UUID.class ) ) ).thenReturn( board );

        this.service.renameBoard( boardUuid, "Test Board" );
        assertThat( board.getName() ).isEqualTo( "Test Board" );
        assertThat( board.changes() ).hasSize( 2 );

        verify( this.client, times( 1 ) ).find( any( UUID.class ) );
        verify( this.client, times( 1 ) ).save( any( Board.class ) );
        verifyNoMoreInteractions( this.client );

    }

    @Test
    public void testAddStoryToBoard() throws Exception {

        UUID boardUuid = UUID.randomUUID();
        Board board = createTestBoard( boardUuid );

        when( this.client.find( any( UUID.class ) ) ).thenReturn( board );

        UUID storyUuid = this.service.addStory( boardUuid, "Test Story" );
        assertThat( storyUuid ).isNotNull();
        assertThat( board.getStories() ).hasSize( 1 )
                .containsKey( storyUuid )
                .containsValue( createTestStory( "Test Story" ) );
        assertThat( board.changes() ).hasSize( 2 );

        verify( this.client, times( 1 ) ).find( any( UUID.class ) );
        verify( this.client, times( 1 ) ).save( any( Board.class ) );
        verifyNoMoreInteractions( this.client );

    }

    @Test
    public void testUpdateStoryOnBoard() throws Exception {

        UUID boardUuid = UUID.randomUUID();
        UUID storyUuid = UUID.randomUUID();
        Board board = createTestBoard( boardUuid );
        board.addStory( storyUuid, "Test Story" );
        assertThat( board.getStories() ).hasSize( 1 )
                .containsKey( storyUuid )
                .containsValue( createTestStory( "Test Story" ) );

        when( this.client.find( any( UUID.class ) ) ).thenReturn( board );

        this.service.updateStory( boardUuid, storyUuid,"Test Story Updated" );
        assertThat( board.getStories() ).hasSize( 1 )
                .containsKey( storyUuid )
                .containsValue( createTestStory( "Test Story Updated" ) );
        assertThat( board.changes() ).hasSize( 3 );

        verify( this.client, times( 1 ) ).find( any( UUID.class ) );
        verify( this.client, times( 1 ) ).save( any( Board.class ) );
        verifyNoMoreInteractions( this.client );

    }

    @Test
    public void testDeleteStoryFromBoard() throws Exception {

        UUID boardUuid = UUID.randomUUID();
        UUID storyUuid = UUID.randomUUID();
        Board board = createTestBoard( boardUuid );
        board.addStory( storyUuid, "Test Story" );
        assertThat( board.getStories() ).hasSize( 1 )
                .containsKey( storyUuid )
                .containsValue( createTestStory( "Test Story" ) );

        when( this.client.find( any( UUID.class ) ) ).thenReturn( board );

        this.service.deleteStory( boardUuid, storyUuid );
        assertThat( board.getStories() ).hasSize( 0 )
                .doesNotContainKey( storyUuid );
        assertThat( board.changes() ).hasSize( 3 );

        verify( this.client, times( 1 ) ).find( any( UUID.class ) );
        verify( this.client, times( 1 ) ).save( any( Board.class ) );
        verifyNoMoreInteractions( this.client );

    }

    private Board createTestBoard( final UUID boardUuid ) {

        Board board = new Board( boardUuid );
        assertThat( board ).isNotNull();
        assertThat( board.getBoardUuid() ).isEqualTo( boardUuid );
        assertThat( board.getName() ).isEqualTo( "New Board" );
        assertThat( board.changes() ).hasSize( 1 );

        return board;
    }

    private Story createTestStory( final String name ) {

        Story story = new Story();
        story.setName( name );

        return story;
    }

}
