package io.pivotal.dmfrey.eventStoreDemo.domain.config;

import io.pivotal.dmfrey.eventStoreDemo.domain.model.Board;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Configuration
@EnableFeignClients
public class RestConfig {

    @FeignClient( value = "esd-command" )
    public interface CommandClient {

        @PostMapping( path = "/boards/" )
        ResponseEntity createBoard();

        @PatchMapping( path = "/boards/{boardUuid}" )
        ResponseEntity renameBoard( @PathVariable( "boardUuid" ) UUID boardUuid, @RequestParam( name = "name", required = true ) String name );

        @PostMapping( path = "/boards/{boardUuid}/stories" )
        ResponseEntity addStory( @PathVariable( "boardUuid" ) UUID boardUuid, @RequestParam( name = "name", required = true ) String name );

        @PutMapping( path = "/boards/{boardUuid}/stories/{storyUuid}" )
        ResponseEntity updateStory( @PathVariable( "boardUuid" ) UUID boardUuid, @PathVariable( "storyUuid" ) UUID storyUuid, @RequestParam( "name" ) String name );

        @DeleteMapping( path = "/boards/{boardUuid}/stories/{storyUuid}" )
        ResponseEntity deleteStory( @PathVariable( "boardUuid" ) UUID boardUuid, @PathVariable( "storyUuid" ) UUID storyUuid );

    }

    @FeignClient( value = "esd-query" )
    public interface QueryClient {

        @GetMapping( path = "/boards/{boardUuid}" )
        ResponseEntity<Board> board( @PathVariable( "boardUuid" ) UUID boardId );

    }

}