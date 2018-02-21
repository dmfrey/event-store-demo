package io.pivotal.dmfrey.eventStoreDemo.domain.client.eventStore;

import io.pivotal.dmfrey.eventStoreDemo.domain.client.BoardClient;
import io.pivotal.dmfrey.eventStoreDemo.domain.client.eventStore.config.EventStoreClientConfig;
import io.pivotal.dmfrey.eventStoreDemo.domain.client.eventStore.config.RestConfig;
import io.pivotal.dmfrey.eventStoreDemo.domain.events.BoardInitialized;
import io.pivotal.dmfrey.eventStoreDemo.domain.events.DomainEvent;
import io.pivotal.dmfrey.eventStoreDemo.domain.events.DomainEvents;
import io.pivotal.dmfrey.eventStoreDemo.domain.model.Board;
import io.pivotal.dmfrey.eventStoreDemo.domain.model.Story;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith( SpringRunner.class )
@SpringBootTest( classes = { EventStoreClientConfig.class, RestConfig.class  } )
@ActiveProfiles( "event-store" )
public class EventStoreBoardClientTests {

    @Autowired
    BoardClient boardClient;

    @MockBean
    RestConfig.EventStoreClient eventStoreClient;

    @Test
    public void testSave() throws Exception {

        UUID boardUuid = UUID.randomUUID();
        Board board = createTestBoard( boardUuid );

        when( this.eventStoreClient.addNewDomainEvent( any( DomainEvent.class ) ) ).thenReturn( ResponseEntity.accepted().build() );

        this.boardClient.save( board );

        verify( this.eventStoreClient, times( 1 ) ).addNewDomainEvent( any( DomainEvent.class ) );

    }

    @Test( expected = IllegalStateException.class )
    public void testSaveNotAccepted() throws Exception {

        UUID boardUuid = UUID.randomUUID();
        Board board = createTestBoard( boardUuid );

        when( this.eventStoreClient.addNewDomainEvent( any( DomainEvent.class ) ) ).thenReturn( ResponseEntity.unprocessableEntity().build() );

        this.boardClient.save( board );

        verify( this.eventStoreClient, times( 1 ) ).addNewDomainEvent( any( DomainEvent.class ) );

    }

    @Test
    public void testFind() throws Exception {

        UUID boardUuid = UUID.randomUUID();
        DomainEvents domainEvents = new DomainEvents();
        domainEvents.setBoardUuid( boardUuid );
        domainEvents.getDomainEvents().add( createTestBoardInitializedEvent( boardUuid ) );

        when( this.eventStoreClient.getDomainEventsForBoardUuid( any( UUID.class ) ) ).thenReturn( domainEvents );

        Board board = this.boardClient.find( boardUuid );
        assertThat( board ).isNotNull();
        assertThat( board.getBoardUuid() ).isEqualTo( boardUuid );
        assertThat( board.getName() ).isEqualTo( "New Board" );
        assertThat( board.getStories() ).hasSize( 0 );
        assertThat( board.changes() ).hasSize( 0 );

        verify( this.eventStoreClient, times( 1 ) ).getDomainEventsForBoardUuid( any( UUID.class ) );

    }

    @Test( expected = IllegalArgumentException.class )
    public void testFindNotFound() throws Exception {

        UUID boardUuid = UUID.randomUUID();
        DomainEvents domainEvents = new DomainEvents();
        domainEvents.setBoardUuid( boardUuid );

        when( this.eventStoreClient.getDomainEventsForBoardUuid( any( UUID.class ) ) ).thenReturn( domainEvents );

        this.boardClient.find( boardUuid );

        verify( this.eventStoreClient, times( 1 ) ).addNewDomainEvent( any( DomainEvent.class ) );

    }

    private BoardInitialized createTestBoardInitializedEvent( final UUID boardUuid ) {

        return new BoardInitialized( boardUuid, Instant.now() );
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
