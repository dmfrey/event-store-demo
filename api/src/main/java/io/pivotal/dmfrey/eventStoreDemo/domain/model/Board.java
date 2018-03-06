package io.pivotal.dmfrey.eventStoreDemo.domain.model;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;

@Data
@Slf4j
public class Board {

    private String name;
    private Collection<Story> backlog = new ArrayList<>();

}
