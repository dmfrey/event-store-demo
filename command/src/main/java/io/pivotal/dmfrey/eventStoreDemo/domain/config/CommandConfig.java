package io.pivotal.dmfrey.eventStoreDemo.domain.config;

import io.pivotal.dmfrey.eventStoreDemo.domain.client.BoardClient;
import io.pivotal.dmfrey.eventStoreDemo.domain.service.BoardService;
import io.pivotal.dmfrey.eventStoreDemo.domain.service.KeyGenerator;
import io.pivotal.dmfrey.eventStoreDemo.domain.service.TimestampGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommandConfig {

    @Bean
    public BoardService boardService( final BoardClient boardClient, final KeyGenerator boardKeyGenerator,
                                      final KeyGenerator storyKeyGenerator, final TimestampGenerator timestampGenerator ) {

        return new BoardService( boardClient, boardKeyGenerator, storyKeyGenerator, timestampGenerator );
    }

    @Bean
    public KeyGenerator boardKeyGenerator() {

        return new KeyGenerator();
    }

    @Bean
    public KeyGenerator storyKeyGenerator() {

        return new KeyGenerator();
    }

    @Bean
    public TimestampGenerator timestampGenerator() {

        return new TimestampGenerator();
    }

}
