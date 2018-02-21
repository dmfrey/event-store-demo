package io.pivotal.dmfrey.eventStoreDemo.domain.model;

import io.pivotal.dmfrey.eventStoreDemo.domain.events.*;
import org.junit.Test;

import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class BoardTests {

    @Test
    public void testBoardCreateFrom() throws Exception {

        UUID boardUuid = UUID.randomUUID();
        UUID storyUuid = UUID.randomUUID();

        BoardInitialized boardInitialized = new BoardInitialized( boardUuid, Instant.now() );
        BoardRenamed boardRenamed = new BoardRenamed( "Test Board", boardUuid, Instant.now() );
        StoryAdded storyAdded = new StoryAdded( storyUuid, "Test Story", boardUuid, Instant.now() );

        Board board = Board.createFrom( boardUuid, Arrays.asList( boardInitialized, boardRenamed, storyAdded ) );
        assertThat( board ).isNotNull();
        assertThat( board.changes() ).hasSize( 3 )
                .containsSequence( boardInitialized, boardRenamed, storyAdded );
        assertThat( board.getBoardUuid() ).isEqualTo( boardUuid );
        assertThat( board.getName() ).isEqualTo( "Test Board" );
        assertThat( board.getStories() ).hasSize( 1 )
                .containsKey( storyUuid )
                .containsValue( createTestStory( "Test Story" ) );

        StoryUpdated storyUpdated = new StoryUpdated( storyUuid, "Test Story Updated", boardUuid, Instant.now() );

        board = Board.createFrom( boardUuid, Arrays.asList( boardInitialized, boardRenamed, storyAdded, storyUpdated ) );
        assertThat( board.changes() ).hasSize( 4 )
                .containsSequence( boardInitialized, boardRenamed, storyAdded, storyUpdated );
        assertThat( board.getStories() ).hasSize( 1 )
                .containsKey( storyUuid )
                .containsValue( createTestStory( "Test Story Updated" ) );


        StoryDeleted storyDeleted = new StoryDeleted( storyUuid, boardUuid, Instant.now() );

        board = Board.createFrom( boardUuid, Arrays.asList( boardInitialized, boardRenamed, storyAdded, storyUpdated, storyDeleted ) );
        assertThat( board.changes() ).hasSize( 5 )
                .containsSequence( boardInitialized, boardRenamed, storyAdded, storyUpdated, storyDeleted );
        assertThat( board.getStories() ).hasSize( 0 )
                .doesNotContainKey( storyUuid );

    }

    private Story createTestStory( final String name ) {

        Story story = new Story();
        story.setName( name );

        return story;
    }

}
