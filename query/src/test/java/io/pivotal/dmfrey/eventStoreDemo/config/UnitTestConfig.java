package io.pivotal.dmfrey.eventStoreDemo.config;

import io.pivotal.dmfrey.eventStoreDemo.domain.client.BoardClient;
import io.pivotal.dmfrey.eventStoreDemo.domain.model.Board;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.util.UUID;

@TestConfiguration
public class UnitTestConfig {

    @Bean
    @Primary
    public BoardClient boardClient() {

        return new BoardClient() {

            @Override
            public Board find( UUID boardUuid ) {

                throw new UnsupportedOperationException( "client call not implemented yet" );
            }

            @Override
            public void removeFromCache(UUID boardUuid) {

                throw new UnsupportedOperationException( "client call not implemented yet" );
            }

        };

    }

}
