package io.pivotal.dmfrey.eventStoreDemo.domain.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Configuration
@EnableFeignClients
public class RestConfig {

    @FeignClient( value = "command" )
    public interface CommandClient {

        @PostMapping( path = "/" )
        ResponseEntity createBoard();

        @PatchMapping( path = "/{boardUuid}" )
        ResponseEntity renameBoard( @PathVariable( "boardUuid" ) UUID boardUuid, @RequestParam( name = "name", required = true ) String name );

        @PostMapping( path = "/{boardUuid}/stories" )
        ResponseEntity addStory( @PathVariable( "boardUuid" ) UUID boardUuid, @RequestParam( name = "name", required = true ) String name );

        @PutMapping( path = "/{boardUuid}/stories/{storyUuid}" )
        ResponseEntity updateStory( @PathVariable( "boardUuid" ) UUID boardUuid, @PathVariable( "storyUuid" ) UUID storyUuid, @RequestParam( "name" ) String name );

        @DeleteMapping( path = "/{boardUuid}/stories/{storyUuid}" )
        ResponseEntity deleteStory( @PathVariable( "boardUuid" ) UUID boardUuid, @PathVariable( "storyUuid" ) UUID storyUuid );

    }

    @FeignClient( value = "query" )
    public interface QueryClient {

        @GetMapping( path = "/{boardUuid}" )
        ResponseEntity board( @PathVariable( "boardUuid" ) UUID boardId );

    }

}