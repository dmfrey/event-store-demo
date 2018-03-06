package io.pivotal.dmfrey.eventStoreDemo.domain.service;

import io.pivotal.dmfrey.eventStoreDemo.domain.client.BoardClient;
import io.pivotal.dmfrey.eventStoreDemo.domain.model.Board;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.UUID;

@Slf4j
public class BoardService {

    private final BoardClient client;

    public BoardService( final BoardClient client ) {

        this.client = client;

    }

//    @Cacheable( "boards" )
    public Board find( final UUID boardUuid ) {
        log.debug( "find : enter" );

        Board board = this.client.find( boardUuid );
        log.debug( "find : board=" + board );

        log.debug( "find : exit" );
        return board;
    }

//    @CacheEvict( value = "boards", key = "#boardUuid" )
    public void uncacheTarget( final UUID boardUuid ) {
        log.debug( "uncacheTarget : enter" );

        this.client.removeFromCache( boardUuid );

        log.debug( "uncacheTarget : exit" );
    }

}
