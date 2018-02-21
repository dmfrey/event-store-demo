package io.pivotal.dmfrey.eventStoreDemo.domain.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.pivotal.dmfrey.eventStoreDemo.domain.model.Story;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@Data
@EqualsAndHashCode( callSuper = true )
@ToString( callSuper = true )
@JsonPropertyOrder({ "eventType", "boardUuid", "occurredOn", "storyUuid" })
public class StoryDeleted extends DomainEvent {

    private final UUID storyUuid;

    @JsonCreator
    public StoryDeleted(
            @JsonProperty( "storyUuid" ) final UUID storyUuid,
            @JsonProperty( "boardUuid" ) final UUID boardUuid,
            @JsonProperty( "occurredOn" ) final Instant when
    ) {
        super( boardUuid, when );

        this.storyUuid = storyUuid;

    }

    @Override
    @JsonIgnore
    public String eventType() {

        return this.getClass().getSimpleName();
    }

}
