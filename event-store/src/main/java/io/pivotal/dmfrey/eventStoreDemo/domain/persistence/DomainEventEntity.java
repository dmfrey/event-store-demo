package io.pivotal.dmfrey.eventStoreDemo.domain.persistence;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity( name = "domainEvent" )
@Table( name = "domain_event" )
@Data
@EqualsAndHashCode( exclude = { "id", "occurredOn" })
@JsonIgnoreProperties( ignoreUnknown = true )
public class DomainEventEntity {

    @Id
    private String id;

    @Column( columnDefinition = "TIMESTAMP" )
    private LocalDateTime occurredOn;

    @Lob
    private String data;

    @Column( name = "board_uuid" )
    private String boardUuid;

}
