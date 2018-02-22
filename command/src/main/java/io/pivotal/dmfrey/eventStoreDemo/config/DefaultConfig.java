package io.pivotal.dmfrey.eventStoreDemo.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;

//@Configuration
//@EnableAutoConfiguration( exclude = { EnableKafka.class, EnableKafkaStreams.class })
public class DefaultConfig {
}
