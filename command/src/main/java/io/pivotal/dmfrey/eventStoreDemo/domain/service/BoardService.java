package io.pivotal.dmfrey.eventStoreDemo.domain.service;

import io.pivotal.dmfrey.eventStoreDemo.domain.client.BoardClient;
import io.pivotal.dmfrey.eventStoreDemo.domain.model.Board;

import java.util.UUID;

public class BoardService {

    private final BoardClient client;

    public BoardService( final BoardClient client ) {

        this.client = client;

    }

    public UUID createBoard() {

        Board board = new Board( UUID.randomUUID() );
        this.client.save( board );

        return board.getBoardUuid();
    }

    public void renameBoard( final UUID boardUuid, final String name ) {

        Board board = this.client.find( boardUuid );
        board.renameBoard( name );
        this.client.save( board );

    }

    public UUID addStory( final UUID boardUuid, final String name ) {

        Board board = this.client.find( boardUuid );

        UUID storyUuid = UUID.randomUUID();
        board.addStory( storyUuid, name );

        this.client.save( board );

        return storyUuid;
    }

    public void updateStory( final UUID boardUuid, final UUID storyUuid, final String name ) {

        Board board = this.client.find( boardUuid );
        board.updateStory( storyUuid, name );

        this.client.save( board );

    }

    public void deleteStory( final UUID boardUuid, final UUID storyUuid ) {

        Board board = this.client.find( boardUuid );
        board.deleteStory( storyUuid );

        this.client.save( board );

    }

}
