package io.pivotal.dmfrey.eventStoreDemo.domain.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.pivotal.dmfrey.eventStoreDemo.domain.events.*;
import io.vavr.API;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.*;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.Predicates.instanceOf;
import static io.vavr.collection.Stream.ofAll;
import static lombok.AccessLevel.NONE;

@Data
public class Board {

    @Setter( NONE )
    private UUID boardUuid;

    @Setter( NONE )
    private String name = "New Board";

    @Getter( NONE )
    @Setter( NONE )
    private Map<UUID, Story> stories = new HashMap<>();

    @Getter( NONE )
    @Setter( NONE )
    private List<DomainEvent> changes = new ArrayList<>();

    public Board() { }

    public Board( final UUID boardUuid ) {

        boardInitialized( new BoardInitialized( boardUuid, Instant.now() ) );

    }

    private Board boardInitialized( final BoardInitialized event ) {

        flushChanges();
        this.boardUuid = event.getBoardUuid();
        this.changes.add( event );

        return this;
    }

    public void renameBoard( final String name ) {

        boardRenamed( new BoardRenamed( name, this.boardUuid, Instant.now() ) );

    }

    private Board boardRenamed( final BoardRenamed event ) {

        this.name = event.getName();
        this.changes.add( event );

        return this;
    }

    public String getName() {

        return this.name;
    }

    public void addStory( final UUID storyUuid, final String name ) {

        storyAdded( new StoryAdded( storyUuid, name, this.boardUuid, Instant.now() ) );

    }

    private Board storyAdded( final StoryAdded event ) {

        this.stories.put( event.getStoryUuid(), event.getStory() );
        this.changes.add( event );

        return this;
    }

    public void updateStory( final UUID storyUuid, final String name ) {

        storyUpdated( new StoryUpdated( storyUuid, name, this.boardUuid, Instant.now() ) );

    }

    private Board storyUpdated( final StoryUpdated event ) {

        this.stories.replace( event.getStoryUuid(), event.getStory() );
        this.changes.add( event );

        return this;
    }

    public void deleteStory( final UUID storyUuid ) {

        storyDeleted( new StoryDeleted( storyUuid, this.boardUuid, Instant.now() ) );

    }

    private Board storyDeleted( final StoryDeleted event ) {

        this.stories.remove( event.getStoryUuid() );
        this.changes.add( event );

        return this;
    }

    public Map<UUID, Story> getStories() {

        return ImmutableMap.copyOf( this.stories );
    }

    public List<DomainEvent> changes() {

        return ImmutableList.copyOf( this.changes );
    }

    public void flushChanges() {

        this.changes.clear();

    }

    // Builder Methods
    public static Board createFrom( final UUID boardUuid, final Collection<DomainEvent> domainEvents ) {

        return ofAll( domainEvents ).foldLeft( new Board( boardUuid ), Board::handleEvent );
    }

    public Board handleEvent( final DomainEvent domainEvent ) {

        return API.Match( domainEvent ).of(
                Case( $( instanceOf( BoardInitialized.class ) ), this::boardInitialized ),
                Case( $( instanceOf( BoardRenamed.class ) ), this::boardRenamed ),
                Case( $( instanceOf( StoryAdded.class ) ), this::storyAdded ),
                Case( $( instanceOf( StoryUpdated.class ) ), this::storyUpdated ),
                Case( $( instanceOf( StoryDeleted.class ) ), this::storyDeleted ),
                Case( $(), this )
        );
    }

}
