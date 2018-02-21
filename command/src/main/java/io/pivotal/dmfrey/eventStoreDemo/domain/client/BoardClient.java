package io.pivotal.dmfrey.eventStoreDemo.domain.client;

import io.pivotal.dmfrey.eventStoreDemo.domain.model.Board;

import java.util.UUID;

public interface BoardClient {

    void save( final Board board );

    Board find( final UUID boardUuid );

}
