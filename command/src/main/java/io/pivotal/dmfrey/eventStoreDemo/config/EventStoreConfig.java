package io.pivotal.dmfrey.eventStoreDemo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile( "event-store" )
@Configuration
public class EventStoreConfig {

}
