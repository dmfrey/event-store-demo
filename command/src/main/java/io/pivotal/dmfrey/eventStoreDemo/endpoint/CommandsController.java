package io.pivotal.dmfrey.eventStoreDemo.endpoint;

import io.pivotal.dmfrey.eventStoreDemo.domain.service.BoardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

@RestController
@RequestMapping( "/boards" )
@Slf4j
public class CommandsController {

    private final BoardService service;

    public CommandsController( final BoardService service ) {

        this.service = service;

    }

    @PostMapping( "/" )
    public ResponseEntity createBoard( final UriComponentsBuilder uriComponentsBuilder ) {
        log.debug( "createBoard : enter" );

        UUID boardUuid = this.service.createBoard();

        return ResponseEntity
                .created( uriComponentsBuilder.path( "/boards/{boardUuid}" ).buildAndExpand( boardUuid ).toUri() )
                .build();
    }

    @PatchMapping( "/{boardUuid}" )
    public ResponseEntity renameBoard( @PathVariable( "boardUuid" ) UUID boardUuid, @RequestParam( "name" ) String name, final UriComponentsBuilder uriComponentsBuilder ) {
        log.debug( "renameBoard : enter" );

        this.service.renameBoard( boardUuid, name );

        return ResponseEntity
                .accepted()
                .build();
    }

    @PostMapping( "/{boardUuid}/stories" )
    public ResponseEntity addStoryToBoard( @PathVariable( "boardUuid" ) UUID boardUuid, @RequestParam( "name" ) String name, final UriComponentsBuilder uriComponentsBuilder ) {
        log.debug( "addStoryToBoard : enter" );

        UUID storyUuid = this.service.addStory( boardUuid, name );

        return ResponseEntity
                .created( uriComponentsBuilder.path( "/boards/{boardUuid}/stories/{storyUuid}" ).buildAndExpand( boardUuid, storyUuid ).toUri() )
                .build();
    }

    @PutMapping( "/{boardUuid}/stories/{storyUuid}" )
    public ResponseEntity updateStoryOnBoard( @PathVariable( "boardUuid" ) UUID boardUuid, @PathVariable( "storyUuid" ) UUID storyUuid, @RequestParam( "name" ) String name ) {
        log.debug( "updateStoryOnBoard : enter" );

        this.service.updateStory( boardUuid, storyUuid, name );

        return ResponseEntity
                .accepted()
                .build();
    }

    @DeleteMapping( "/{boardUuid}/stories/{storyUuid}" )
    public ResponseEntity removeStoryFromBoard( @PathVariable( "boardUuid" ) UUID boardUuid, @PathVariable( "storyUuid" ) UUID storyUuid ) {
        log.debug( "removeStoryFromBoard : enter" );

        this.service.deleteStory( boardUuid, storyUuid );

        return ResponseEntity
                .accepted()
                .build();
    }

}
