package io.pivotal.dmfrey.eventStoreDemo.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.UUID;

@Data
@JsonIgnoreProperties( ignoreUnknown = true )
public class Story {

    private UUID storyUuid;
    private String name;

}
