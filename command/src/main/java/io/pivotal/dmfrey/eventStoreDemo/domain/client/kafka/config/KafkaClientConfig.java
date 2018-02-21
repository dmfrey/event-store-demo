package io.pivotal.dmfrey.eventStoreDemo.domain.client.kafka.config;

import io.pivotal.dmfrey.eventStoreDemo.domain.client.BoardClient;
import io.pivotal.dmfrey.eventStoreDemo.domain.client.kafka.service.DomainEventSource;
import io.pivotal.dmfrey.eventStoreDemo.domain.client.kafka.service.DomainEventSourceImpl;
import io.pivotal.dmfrey.eventStoreDemo.domain.client.kafka.service.KafkaBoardClient;
import org.apache.kafka.streams.KafkaStreams;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.stream.binder.kafka.streams.QueryableStoreRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.StreamsBuilderFactoryBean;

@Profile( "kafka" )
@Configuration
@EnableAutoConfiguration
public class KafkaClientConfig {

    @Bean
    @Primary
    public BoardClient boardClient(
            final DomainEventSource domainEventSource,
            final QueryableStoreRegistry queryableStoreRegistry
    ) {

        return new KafkaBoardClient( domainEventSource, queryableStoreRegistry );
    }

}
