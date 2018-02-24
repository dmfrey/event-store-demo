package io.pivotal.dmfrey.eventStoreDemo.endpoint;

import io.pivotal.dmfrey.eventStoreDemo.domain.service.BoardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

@RestController
@RequestMapping( "/boards" )
public class ApiController {

    private final BoardService service;

    public ApiController( final BoardService service ) {

        this.service = service;

    }

    @PostMapping
    public ResponseEntity createBoard() {

        return this.service.createBoard();
    }

    @GetMapping( "/{boardUuid}" )
    public ResponseEntity board( @PathVariable( "boardUuid" ) UUID boardUuid ) {

        return this.service.board( boardUuid );
    }

    @PatchMapping( "/{boardUuid}" )
    public ResponseEntity renameBoard( @PathVariable( "boardUuid" ) UUID boardUuid, @RequestParam( "name" ) String name, final UriComponentsBuilder uriComponentsBuilder ) {

        return this.service.renameBoard( boardUuid, name );
    }

    @PostMapping( "/{boardUuid}/stories" )
    public ResponseEntity addStoryToBoard( @PathVariable( "boardUuid" ) UUID boardUuid, @RequestParam( "name" ) String name, final UriComponentsBuilder uriComponentsBuilder ) {

        return this.service.addStory( boardUuid, name );
    }

    @PutMapping( "/{boardUuid}/stories/{storyUuid}" )
    public ResponseEntity updateStoryOnBoard( @PathVariable( "boardUuid" ) UUID boardUuid, @PathVariable( "storyUuid" ) UUID storyUuid, @RequestParam( "name" ) String name ) {

        return this.service.updateStory( boardUuid, storyUuid, name );
    }

    @DeleteMapping( "/{boardUuid}/stories/{storyUuid}" )
    public ResponseEntity removeStoryFromBoard( @PathVariable( "boardUuid" ) UUID boardUuid, @PathVariable( "storyUuid" ) UUID storyUuid ) {

        return this.service.deleteStory( boardUuid, storyUuid );
    }

}
