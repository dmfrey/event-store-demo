package io.pivotal.dmfrey.eventStoreDemo.domain.events;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class DomainEvents {

    private UUID boardUuid;
    private List<DomainEvent> domainEvents = new ArrayList<>();

}
