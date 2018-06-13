package io.pivotal.dmfrey.eventStoreDemo.config;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.cloud.config.java.AbstractCloudConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class RabbitConfig {


    @Configuration
    @Profile("cloud")
    public static class CloudRabbitConfig extends AbstractCloudConfig {

        @Bean
        public ConnectionFactory rabbitConnectionFactory() {

            return connectionFactory().rabbitConnectionFactory();
        }

    }

}
