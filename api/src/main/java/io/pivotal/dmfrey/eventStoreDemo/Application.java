package io.pivotal.dmfrey.eventStoreDemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;

@SpringBootApplication
@EnableCircuitBreaker
public class Application {

    public static void main( String[] args ) {

        SpringApplication.run( Application.class, args );

    }

}
