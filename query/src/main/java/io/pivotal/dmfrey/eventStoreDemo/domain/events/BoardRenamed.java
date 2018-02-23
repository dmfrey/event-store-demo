package io.pivotal.dmfrey.eventStoreDemo.domain.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@Data
@EqualsAndHashCode( callSuper = true )
@ToString( callSuper = true )
@JsonPropertyOrder({ "eventType", "boardUuid", "occurredOn", "name" })
public class BoardRenamed extends DomainEvent {

    private final String name;

    @JsonCreator
    public BoardRenamed(
            @JsonProperty( "name" ) final String name,
            @JsonProperty( "boardUuid" ) final UUID boardUuid,
            @JsonProperty( "occurredOn" ) final Instant when
    ) {
        super( boardUuid, when );

        this.name = name;

    }

    @Override
    @JsonIgnore
    public String eventType() {

        return this.getClass().getSimpleName();
    }

}
