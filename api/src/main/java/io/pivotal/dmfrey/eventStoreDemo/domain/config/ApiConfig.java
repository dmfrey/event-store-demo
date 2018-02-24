package io.pivotal.dmfrey.eventStoreDemo.domain.config;

import io.pivotal.dmfrey.eventStoreDemo.domain.service.BoardService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiConfig {

    @Bean
    public BoardService boardService( final RestConfig.CommandClient commandClient, final RestConfig.QueryClient queryClient ) {

        return new BoardService( commandClient, queryClient );
    }

}
