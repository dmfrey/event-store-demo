package io.pivotal.dmfrey.eventStoreDemo.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile( "default" )
@Configuration
@EnableAutoConfiguration( exclude = {MongoDataAutoConfiguration.class } )
public class DefaultConfig {
}
