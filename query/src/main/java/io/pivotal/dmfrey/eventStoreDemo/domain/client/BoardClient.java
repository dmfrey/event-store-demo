package io.pivotal.dmfrey.eventStoreDemo.domain.client;

import io.pivotal.dmfrey.eventStoreDemo.domain.model.Board;

import java.util.UUID;

public interface BoardClient {

    Board find( final UUID boardUuid );

    void removeFromCache( final UUID boardUuid );

}
