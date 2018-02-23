package io.pivotal.dmfrey.eventStoreDemo.domain.persistence;

import io.pivotal.dmfrey.eventStoreDemo.domain.model.DomainEvents;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static javax.persistence.CascadeType.ALL;

@Entity( name = "domainEvents" )
@Table( name = "domain_events" )
@Data
@EqualsAndHashCode( exclude = { "domainEvents" })
public class DomainEventsEntity {

    @Id
    private String boardUuid;

    @OneToMany( cascade = ALL )
    @JoinColumn( name = "board_uuid" )
    @OrderBy( "occurredOn ASC" )
    private Set<DomainEventEntity> domainEvents;

    public DomainEventsEntity() {

        this.domainEvents = new LinkedHashSet<>();

    }

    public DomainEventsEntity( final String boardUuid ) {
        this();

        this.boardUuid = boardUuid;

    }

    public DomainEvents toModel() {

        DomainEvents model = new DomainEvents();
        model.setBoardUuid( boardUuid );
        model.setDomainEvents( domainEvents.stream()
                .map( DomainEventEntity::getData )
                .collect( toList() ) );

        return model;
    }

}
