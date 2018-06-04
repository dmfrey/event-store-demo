package io.pivotal.dmfrey.eventStoreDemo.domain.service;

import io.pivotal.dmfrey.eventStoreDemo.domain.client.BoardClient;
import io.pivotal.dmfrey.eventStoreDemo.domain.model.Board;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Instant;
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

    @MockBean( name = "boardKeyGenerator" )
    KeyGenerator boardKeyGenerator;

    @MockBean( name = "storyKeyGenerator" )
    KeyGenerator storyKeyGenerator;

    @MockBean
    TimestampGenerator timestampGenerator;

    @Test
    public void testCreateBoard() throws Exception {

        UUID boardUuid = UUID.randomUUID();
        when( this.boardKeyGenerator.generate() ).thenReturn( boardUuid );

        Instant now = Instant.now();
        when( this.timestampGenerator.generate() ).thenReturn( now );

        UUID createdUuid = this.service.createBoard();
        assertThat( createdUuid ).isEqualTo( boardUuid );

        Board expectedBoard = new Board( boardUuid );
        expectedBoard.initialize( now );

        verify( this.client, times( 1 ) ).save( expectedBoard );
        verifyNoMoreInteractions( this.client );

    }

    @Test
    public void testRenameBoard() throws Exception {

        UUID boardUuid = UUID.randomUUID();
        when( this.boardKeyGenerator.generate() ).thenReturn( boardUuid );

        Instant now = Instant.now();
        when( this.timestampGenerator.generate() ).thenReturn( now );

        Board requestedBoard = createTestBoard( boardUuid, now );
        when( this.client.find( any( UUID.class ) ) ).thenReturn( requestedBoard );

        this.service.renameBoard( boardUuid, "Test Board" );

        Board expectedBoard = createTestBoard( boardUuid, now );
        expectedBoard.renameBoard( "Test Board", now );

        verify( this.client, times( 1 ) ).find( boardUuid );
        verify( this.client, times( 1 ) ).save( expectedBoard );
        verifyNoMoreInteractions( this.client );

    }

    @Test
    public void testAddStoryToBoard() throws Exception {

        UUID boardUuid = UUID.randomUUID();
        when( this.boardKeyGenerator.generate() ).thenReturn( boardUuid );

        Instant now = Instant.now();
        when( this.timestampGenerator.generate() ).thenReturn( now );

        UUID storyUuid = UUID.randomUUID();
        when( this.storyKeyGenerator.generate() ).thenReturn( storyUuid );

        Board requestedBoard = createTestBoard( boardUuid, now );
        when( this.client.find( any( UUID.class ) ) ).thenReturn( requestedBoard );

        UUID createdUuid = this.service.addStory( boardUuid, "Test Story" );

        Board expectedBoard = createTestBoard( boardUuid, now );
        expectedBoard.addStory( storyUuid, "Test Story", now );

        assertThat( createdUuid ).isEqualTo( storyUuid );

        verify( this.client, times( 1 ) ).find( boardUuid );
        verify( this.client, times( 1 ) ).save( expectedBoard );
        verifyNoMoreInteractions( this.client );

    }

    @Test
    public void testUpdateStoryOnBoard() throws Exception {

        UUID boardUuid = UUID.randomUUID();
        when( this.boardKeyGenerator.generate() ).thenReturn( boardUuid );

        Instant now = Instant.now();
        when( this.timestampGenerator.generate() ).thenReturn( now );

        UUID storyUuid = UUID.randomUUID();
        when( this.storyKeyGenerator.generate() ).thenReturn( storyUuid );

        Board requestedBoard = createTestBoard( boardUuid, now );
        requestedBoard.addStory( storyUuid, "Test Story", now );
        when( this.client.find( any( UUID.class ) ) ).thenReturn( requestedBoard );

        this.service.updateStory( boardUuid, storyUuid,"Test Story Updated" );

        Board expectedBoard = createTestBoard( boardUuid, now );
        expectedBoard.addStory( storyUuid, "Test Story", now );
        expectedBoard.updateStory( storyUuid, "Test Story Updated", now );

        verify( this.client, times( 1 ) ).find( boardUuid );
        verify( this.client, times( 1 ) ).save( expectedBoard );
        verifyNoMoreInteractions( this.client );

    }

    @Test
    public void testDeleteStoryFromBoard() throws Exception {

        UUID boardUuid = UUID.randomUUID();
        when( this.boardKeyGenerator.generate() ).thenReturn( boardUuid );

        Instant now = Instant.now();
        when( this.timestampGenerator.generate() ).thenReturn( now );

        UUID storyUuid = UUID.randomUUID();
        when( this.storyKeyGenerator.generate() ).thenReturn( storyUuid );

        Board requestedBoard = createTestBoard( boardUuid, now );
        requestedBoard.addStory( storyUuid, "Test Story", now );
        when( this.client.find( any( UUID.class ) ) ).thenReturn( requestedBoard );

        this.service.deleteStory( boardUuid, storyUuid );

        Board expectedBoard = createTestBoard( boardUuid, now );
        expectedBoard.addStory( storyUuid, "Test Story", now );
        expectedBoard.deleteStory( storyUuid, now );

        verify( this.client, times( 1 ) ).find( boardUuid );
        verify( this.client, times( 1 ) ).save( expectedBoard );
        verifyNoMoreInteractions( this.client );

    }

    private Board createTestBoard( final UUID boardUuid, final Instant ts ) {

        Board board = new Board( boardUuid );
        board.initialize( ts );
        assertThat( board ).isNotNull();
        assertThat( board.getBoardUuid() ).isEqualTo( boardUuid );
        assertThat( board.getName() ).isEqualTo( "New Board" );
        assertThat( board.changes() ).hasSize( 1 );

        return board;
    }

}
