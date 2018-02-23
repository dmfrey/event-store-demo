package io.pivotal.dmfrey.eventStoreDemo.domain.config;

import io.pivotal.dmfrey.eventStoreDemo.domain.persistence.DomainEventsRepository;
import io.pivotal.dmfrey.eventStoreDemo.domain.service.DomainEventService;
import io.pivotal.dmfrey.eventStoreDemo.domain.service.NotificationPublisher;
import io.pivotal.dmfrey.eventStoreDemo.domain.service.converters.JsonStringToTupleConverter;
import io.pivotal.dmfrey.eventStoreDemo.domain.service.converters.TupleToJsonStringConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EventStoreConfig {

    @Bean
    public DomainEventService domainEventService(
            final DomainEventsRepository domainEventsRepository, final NotificationPublisher publisher,
            final TupleToJsonStringConverter tupleToJsonStringConverter, final JsonStringToTupleConverter jsonStringToTupleConverter
    ) {

        return new DomainEventService( domainEventsRepository, publisher, tupleToJsonStringConverter, jsonStringToTupleConverter );
    }

}
