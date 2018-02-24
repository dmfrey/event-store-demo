package io.pivotal.dmfrey.eventStoreDemo.domain.client.kafka.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.pivotal.dmfrey.eventStoreDemo.Application;
import io.pivotal.dmfrey.eventStoreDemo.domain.client.BoardClient;
import io.pivotal.dmfrey.eventStoreDemo.domain.events.BoardInitialized;
import io.pivotal.dmfrey.eventStoreDemo.domain.model.Board;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.rule.KafkaEmbedded;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import static io.pivotal.dmfrey.eventStoreDemo.domain.client.kafka.config.KafkaClientConfig.BOARD_EVENTS_SNAPSHOTS;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

@Slf4j
public class KafkaBoardClientEmbeddedKafkaTests {

    private static String RECEIVER_TOPIC = "board-events";

    @ClassRule
    public static KafkaEmbedded embeddedKafka = new KafkaEmbedded( 1, true, RECEIVER_TOPIC, BOARD_EVENTS_SNAPSHOTS );

    private static Consumer<String, String> consumer;

    @BeforeClass
    public static void setUp() throws Exception {

        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("query-board-events-group", "false", embeddedKafka );
        consumerProps.put( ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest" );

        DefaultKafkaConsumerFactory<String, String> cf = new DefaultKafkaConsumerFactory<>( consumerProps );

        consumer = cf.createConsumer();

        embeddedKafka.consumeFromAnEmbeddedTopic( consumer, RECEIVER_TOPIC );

    }

    @AfterClass
    public static void tearDown() {

        consumer.close();

    }

    @Test
    public void testFind() throws Exception {

        log.debug( "testFind : --spring.cloud.stream.kafka.streams.binder.brokers=" + embeddedKafka.getBrokersAsString() );
        log.debug( "testFind : --spring.cloud.stream.kafka.streams.binder.zkNodes=" + embeddedKafka.getZookeeperConnectionString() );

        SpringApplication app = new SpringApplication( Application.class );
        app.setWebApplicationType( WebApplicationType.NONE );
        ConfigurableApplicationContext context = app.run("--server.port=0",
                "--spring.cloud.service-registry.auto-registration.enabled=false",
                "--spring.jmx.enabled=false",
                "--spring.cloud.stream.bindings.input.destination=board-events",
                "--spring.cloud.stream.bindings.output.destination=board-events",
                "--spring.cloud.stream.kafka.streams.binder.configuration.commit.interval.ms=1000",
                "--spring.cloud.stream.bindings.output.producer.headerMode=raw",
                "--spring.cloud.stream.bindings.input.consumer.headerMode=raw",
                "--spring.cloud.stream.kafka.streams.binder.brokers=" + embeddedKafka.getBrokersAsString(),
                "--spring.cloud.stream.kafka.streams.binder.zkNodes=" + embeddedKafka.getZookeeperConnectionString(),
                "--spring.profiles.active=kafka",
                "--spring.jackson.serialization.write_dates_as_timestamps=false",
                "--logger.level.io.pivotal.dmfrey=DEBUG");
        try {

            receiveAndValidateBoard( context );

        } finally {

            context.close();

        }

    }

    private void receiveAndValidateBoard( ConfigurableApplicationContext context ) throws Exception {

        Map<String, Object> senderProps = KafkaTestUtils.producerProps( embeddedKafka );
        DefaultKafkaProducerFactory<String, String> pf = new DefaultKafkaProducerFactory<>( senderProps );
        KafkaTemplate<String, String> template = new KafkaTemplate<>( pf, true );
        template.setDefaultTopic( RECEIVER_TOPIC );

        ObjectMapper mapper = context.getBean( ObjectMapper.class );
        BoardClient boardClient = context.getBean( BoardClient.class );

        UUID boardUuid = UUID.randomUUID();
        BoardInitialized boardInitialized = createTestBoardInitializedEvent( boardUuid );
        String event = mapper.writeValueAsString( boardInitialized );
        template.sendDefault( event );

        Thread.sleep( 1000 );

        Board board = boardClient.find( boardUuid );
        assertThat( board, is( notNullValue() ) );
        assertThat( board.getBoardUuid(), is( equalTo( boardUuid ) ) );
        assertThat( board.getName(), is( equalTo( "New Board" ) ) );
        assertThat( board.getStories().isEmpty(), is( equalTo( true ) ) );

    }

    private BoardInitialized createTestBoardInitializedEvent( final UUID boardUuid ) {

        return new BoardInitialized( boardUuid, Instant.now() );
    }

}
