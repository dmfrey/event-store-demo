package io.pivotal.dmfrey.eventStoreDemo.domain.service;

import io.pivotal.dmfrey.eventStoreDemo.domain.client.BoardClient;
import io.pivotal.dmfrey.eventStoreDemo.domain.model.Board;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
public class BoardService {

    private final BoardClient client;

    public BoardService( final BoardClient client ) {

        this.client = client;

    }

    public UUID createBoard() {
        log.debug( "createBoard : enter" );

        Board board = new Board( UUID.randomUUID() );
        this.client.save( board );

        return board.getBoardUuid();
    }

    public void renameBoard( final UUID boardUuid, final String name ) {
        log.debug( "renameBoard : enter" );

        Board board = this.client.find( boardUuid );
        board.renameBoard( name );
        this.client.save( board );

    }

    public UUID addStory( final UUID boardUuid, final String name ) {
        log.debug( "addStory : enter" );

        Board board = this.client.find( boardUuid );

        UUID storyUuid = UUID.randomUUID();
        board.addStory( storyUuid, name );

        this.client.save( board );

        return storyUuid;
    }

    public void updateStory( final UUID boardUuid, final UUID storyUuid, final String name ) {
        log.debug( "updateStory : enter" );

        Board board = this.client.find( boardUuid );
        board.updateStory( storyUuid, name );

        this.client.save( board );

    }

    public void deleteStory( final UUID boardUuid, final UUID storyUuid ) {
        log.debug( "deleteStory : enter" );

        Board board = this.client.find( boardUuid );
        board.deleteStory( storyUuid );

        this.client.save( board );

    }

}
