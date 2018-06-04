package io.pivotal.dmfrey.eventStoreDemo.domain.service;

import io.pivotal.dmfrey.eventStoreDemo.domain.client.BoardClient;
import io.pivotal.dmfrey.eventStoreDemo.domain.model.Board;

import java.util.UUID;

public class BoardService {

    private final BoardClient client;
    private final KeyGenerator boardKeyGenerator;
    private final KeyGenerator storyKeyGenerator;
    private final TimestampGenerator timestampGenerator;

    public BoardService( final BoardClient client, final KeyGenerator boardKeyGenerator,
                         final KeyGenerator storyKeyGenerator, final TimestampGenerator timestampGenerator ) {

        this.client = client;
        this.boardKeyGenerator = boardKeyGenerator;
        this.storyKeyGenerator = storyKeyGenerator;
        this.timestampGenerator = timestampGenerator;

    }

    public UUID createBoard() {

        Board board = new Board( this.boardKeyGenerator.generate() );
        board.initialize( this.timestampGenerator.generate() );

        this.client.save( board );

        return board.getBoardUuid();
    }

    public void renameBoard( final UUID boardUuid, final String name ) {

        Board board = this.client.find( boardUuid );
        board.renameBoard( name, this.timestampGenerator.generate() );

        this.client.save( board );

    }

    public UUID addStory( final UUID boardUuid, final String name ) {

        Board board = this.client.find( boardUuid );

        UUID storyUuid = this.storyKeyGenerator.generate();
        board.addStory( storyUuid, name, this.timestampGenerator.generate() );

        this.client.save( board );

        return storyUuid;
    }

    public void updateStory( final UUID boardUuid, final UUID storyUuid, final String name ) {

        Board board = this.client.find( boardUuid );
        board.updateStory( storyUuid, name, this.timestampGenerator.generate() );

        this.client.save( board );

    }

    public void deleteStory( final UUID boardUuid, final UUID storyUuid ) {

        Board board = this.client.find( boardUuid );
        board.deleteStory( storyUuid, this.timestampGenerator.generate() );

        this.client.save( board );

    }

}
