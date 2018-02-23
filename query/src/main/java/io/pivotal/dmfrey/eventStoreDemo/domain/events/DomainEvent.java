package io.pivotal.dmfrey.eventStoreDemo.domain.events;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

import static lombok.AccessLevel.NONE;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "eventType",
        defaultImpl = DomainEventIgnored.class
)
@JsonSubTypes({
        @JsonSubTypes.Type( value = BoardInitialized.class, name = "BoardInitialized" ),
        @JsonSubTypes.Type( value = BoardRenamed.class, name = "BoardRenamed" ),
        @JsonSubTypes.Type( value = StoryAdded.class, name = "StoryAdded" ),
        @JsonSubTypes.Type( value = StoryUpdated.class, name = "StoryUpdated" ),
        @JsonSubTypes.Type( value = StoryDeleted.class, name = "StoryDeleted" )
})
@Data
public abstract class DomainEvent {

    private final UUID boardUuid;

    @Getter( NONE )
    @JsonIgnore
    private final Instant when;

    DomainEvent( final UUID boardUuid, final Instant when ) {

        this.boardUuid = boardUuid;
        this.when = when;

    }

    @JsonProperty( "occurredOn" )
    public Instant occurredOn() {

        return when;
    }

    @JsonProperty( "eventType" )
    public abstract String eventType();

}
