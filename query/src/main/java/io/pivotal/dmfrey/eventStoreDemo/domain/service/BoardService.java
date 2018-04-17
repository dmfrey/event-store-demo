package io.pivotal.dmfrey.eventStoreDemo.domain.service;

import io.pivotal.dmfrey.eventStoreDemo.domain.client.BoardClient;
import io.pivotal.dmfrey.eventStoreDemo.domain.model.Board;

import java.util.UUID;

public class BoardService {

    private final BoardClient client;

    public BoardService( final BoardClient client ) {

        this.client = client;

    }

//    @Cacheable( "boards" )
    public Board find( final UUID boardUuid ) {

        Board board = this.client.find( boardUuid );

        return board;
    }

//    @CacheEvict( value = "boards", key = "#boardUuid" )
    public void uncacheTarget( final UUID boardUuid ) {

        this.client.removeFromCache( boardUuid );

    }

}
