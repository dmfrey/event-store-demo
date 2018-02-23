package io.pivotal.dmfrey.eventStoreDemo.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import io.pivotal.dmfrey.eventStoreDemo.domain.events.*;
import io.vavr.API;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.*;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.Predicates.instanceOf;
import static io.vavr.collection.Stream.ofAll;
import static lombok.AccessLevel.NONE;

@Data
@Slf4j
@JsonIgnoreProperties( ignoreUnknown = true )
public class Board {

    private UUID boardUuid;
    private String name;
    private Map<UUID, Story> stories = new HashMap<>();

    public Board() { }

    public Board( final UUID boardUuid ) {

        boardInitialized( new BoardInitialized( boardUuid, Instant.now() ) );

    }

    private Board boardInitialized( final BoardInitialized event ) {
        log.debug( "boardInitialized : event=" + event );

        this.boardUuid = event.getBoardUuid();

        return this;
    }

    private Board boardRenamed( final BoardRenamed event ) {
        log.debug( "boardRenamed : event=" + event );

        this.name = event.getName();

        return this;
    }

    private Board storyAdded( final StoryAdded event ) {
        log.debug( "storyAdded : event=" + event );

        this.stories.put( event.getStoryUuid(), event.getStory() );

        return this;
    }

    private Board storyUpdated( final StoryUpdated event ) {
        log.debug( "storyUpdated : event=" + event );

        this.stories.replace( event.getStoryUuid(), event.getStory() );

        return this;
    }

    private Board storyDeleted( final StoryDeleted event ) {
        log.debug( "storyDeleted : event=" + event );

        this.stories.remove( event.getStoryUuid() );

        return this;
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
