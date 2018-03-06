package io.pivotal.dmfrey.eventStoreDemo.domain.service;

import io.pivotal.dmfrey.eventStoreDemo.domain.config.RestConfig;
import io.pivotal.dmfrey.eventStoreDemo.domain.model.Board;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public class BoardService {

    private final RestConfig.CommandClient commandClient;
    private final RestConfig.QueryClient queryClient;

    public BoardService( final RestConfig.CommandClient commandClient, final RestConfig.QueryClient queryClient ) {

        this.commandClient = commandClient;
        this.queryClient = queryClient;

    }

    public ResponseEntity createBoard() {

        return this.commandClient.createBoard();
    }

    public ResponseEntity renameBoard( final UUID boardUuid, final String name ) {

        return this.commandClient.renameBoard( boardUuid, name );
    }

    public ResponseEntity addStory( final UUID boardUuid, final String name ) {

        return this.commandClient.addStory( boardUuid, name );
    }

    public ResponseEntity updateStory( final UUID boardUuid, final UUID storyUuid, final String name ) {

        return this.commandClient.updateStory( boardUuid, storyUuid, name );
    }

    public ResponseEntity deleteStory( final UUID boardUuid, final UUID storyUuid ) {

        return this.commandClient.deleteStory( boardUuid, storyUuid );
    }

    public ResponseEntity<Board> board( final UUID boardUuid ) {

        return this.queryClient.board( boardUuid );
    }

}
