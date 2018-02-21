package io.pivotal.dmfrey.eventStoreDemo.domain.config;

import io.pivotal.dmfrey.eventStoreDemo.domain.client.BoardClient;
import io.pivotal.dmfrey.eventStoreDemo.domain.model.Board;
import io.pivotal.dmfrey.eventStoreDemo.domain.service.BoardService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
public class CommandConfig {

    @Bean
    public BoardService boardService( final BoardClient boardClient ) {

        return new BoardService( boardClient );
    }

}
