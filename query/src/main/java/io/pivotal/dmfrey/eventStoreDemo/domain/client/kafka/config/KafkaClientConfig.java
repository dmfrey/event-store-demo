package io.pivotal.dmfrey.eventStoreDemo.domain.client.kafka.config;

import io.pivotal.dmfrey.eventStoreDemo.domain.client.BoardClient;
import io.pivotal.dmfrey.eventStoreDemo.domain.client.kafka.service.KafkaBoardClient;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.stream.binder.kafka.streams.QueryableStoreRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Profile( "kafka" )
@Configuration
@EnableAutoConfiguration
public class KafkaClientConfig {

    public static final String BOARD_EVENTS_SNAPSHOTS = "query-board-snapshots";

    @Bean
    @Primary
    public BoardClient boardClient(
            final QueryableStoreRegistry queryableStoreRegistry
    ) {

        return new KafkaBoardClient( queryableStoreRegistry );
    }

}
