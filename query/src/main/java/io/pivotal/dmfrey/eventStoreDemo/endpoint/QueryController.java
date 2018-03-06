package io.pivotal.dmfrey.eventStoreDemo.endpoint;

import io.pivotal.dmfrey.eventStoreDemo.domain.model.Board;
import io.pivotal.dmfrey.eventStoreDemo.domain.service.BoardService;
import io.pivotal.dmfrey.eventStoreDemo.endpoint.model.BoardModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@Slf4j
public class QueryController {

    private final BoardService service;

    public QueryController( final BoardService service ) {

        this.service = service;

    }

    @GetMapping( "/boards/{boardUuid}" )
    public ResponseEntity board( @PathVariable( "boardUuid" ) UUID boardUuid ) {
        log.debug( "board : enter" );

        Board board = this.service.find( boardUuid );
        log.debug( "board : board=" + board.toString() );

        return ResponseEntity
                .ok( BoardModel.fromBoard( board ) );
    }

}
