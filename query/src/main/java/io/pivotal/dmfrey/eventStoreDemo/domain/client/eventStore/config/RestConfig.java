package io.pivotal.dmfrey.eventStoreDemo.domain.client.eventStore.config;

import io.pivotal.dmfrey.eventStoreDemo.domain.events.DomainEvents;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@Profile( "event-store" )
@Configuration
@EnableFeignClients
public class RestConfig {

    @FeignClient( value = "event-store", fallback = HystrixFallbackEventStoreClient.class )
    public interface EventStoreClient {

        @GetMapping( path = "/{boardUuid}" )
        DomainEvents getDomainEventsForBoardUuid( @PathVariable( "boardUuid" ) UUID boardId );

    }

    @Component
    static class HystrixFallbackEventStoreClient implements EventStoreClient {

        @Override
        public DomainEvents getDomainEventsForBoardUuid( final UUID boardUuid) {

            return new DomainEvents();
        }

    }

}