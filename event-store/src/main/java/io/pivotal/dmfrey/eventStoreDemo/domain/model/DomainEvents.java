package io.pivotal.dmfrey.eventStoreDemo.domain.model;

import com.fasterxml.jackson.annotation.JsonRawValue;
import lombok.Data;

import java.util.List;


@Data
public class DomainEvents {

    private String boardUuid;

    @JsonRawValue
    private List<String> domainEvents;

}
