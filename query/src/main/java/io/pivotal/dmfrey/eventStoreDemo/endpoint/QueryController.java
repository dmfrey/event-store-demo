package io.pivotal.dmfrey.eventStoreDemo.endpoint;

import io.pivotal.dmfrey.eventStoreDemo.domain.model.Board;
import io.pivotal.dmfrey.eventStoreDemo.domain.service.BoardService;
import io.pivotal.dmfrey.eventStoreDemo.endpoint.model.BoardModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class QueryController {

    private final BoardService service;

    public QueryController( final BoardService service ) {

        this.service = service;

    }

    @GetMapping( "/boards/{boardUuid}" )
    public ResponseEntity board( @PathVariable( "boardUuid" ) UUID boardUuid ) {

        Board board = this.service.find( boardUuid );

        return ResponseEntity
                .ok( BoardModel.fromBoard( board ) );
    }

}
