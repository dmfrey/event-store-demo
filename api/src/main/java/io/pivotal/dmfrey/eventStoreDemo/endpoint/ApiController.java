package io.pivotal.dmfrey.eventStoreDemo.endpoint;

import io.pivotal.dmfrey.eventStoreDemo.domain.model.Board;
import io.pivotal.dmfrey.eventStoreDemo.domain.service.BoardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

@RestController
@RequestMapping( "/boards" )
@Slf4j
public class ApiController {

    private final BoardService service;

    public ApiController( final BoardService service ) {

        this.service = service;

    }

    @PostMapping
    public ResponseEntity createBoard() {
        log.info( "createBoard : enter" );

        return this.service.createBoard();
    }

    @GetMapping( path = "/{boardUuid}" )
    public ResponseEntity<Board> board(@PathVariable( "boardUuid" ) UUID boardUuid ) {
        log.info( "board : enter" );

        ResponseEntity<Board> responseEntity = this.service.board( boardUuid );
        log.info( "board : responseEntity=" + responseEntity );

        log.info( "board : exit" );
        return responseEntity;
    }

    @PatchMapping( "/{boardUuid}" )
    public ResponseEntity renameBoard( @PathVariable( "boardUuid" ) UUID boardUuid, @RequestParam( "name" ) String name, final UriComponentsBuilder uriComponentsBuilder ) {
        log.info( "renameBoard : enter" );

        return this.service.renameBoard( boardUuid, name );
    }

    @PostMapping( "/{boardUuid}/stories" )
    public ResponseEntity addStoryToBoard( @PathVariable( "boardUuid" ) UUID boardUuid, @RequestParam( "name" ) String name, final UriComponentsBuilder uriComponentsBuilder ) {
        log.info( "addStoryToBoardBoard : enter" );

        return this.service.addStory( boardUuid, name );
    }

    @PutMapping( "/{boardUuid}/stories/{storyUuid}" )
    public ResponseEntity updateStoryOnBoard( @PathVariable( "boardUuid" ) UUID boardUuid, @PathVariable( "storyUuid" ) UUID storyUuid, @RequestParam( "name" ) String name ) {
        log.info( "updateStoryOnBoard : enter" );

        return this.service.updateStory( boardUuid, storyUuid, name );
    }

    @DeleteMapping( "/{boardUuid}/stories/{storyUuid}" )
    public ResponseEntity removeStoryFromBoard( @PathVariable( "boardUuid" ) UUID boardUuid, @PathVariable( "storyUuid" ) UUID storyUuid ) {
        log.info( "removeStoryFromBoard : enter" );

        return this.service.deleteStory( boardUuid, storyUuid );
    }

}
