package io.pivotal.dmfrey.eventStoreDemo.domain.client.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.pivotal.dmfrey.eventStoreDemo.Application;
import io.pivotal.dmfrey.eventStoreDemo.domain.client.BoardClient;
import io.pivotal.dmfrey.eventStoreDemo.domain.events.BoardInitialized;
import io.pivotal.dmfrey.eventStoreDemo.domain.model.Board;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.junit.*;
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
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

//@Ignore
public class KafkaBoardClientEmbeddedKafkaTests {

    private static String RECEIVER_TOPIC = "board-events";

    private static final String KAFKA_BROKERS_PROPERTY = "spring.kafka.bootstrap-servers";

    @ClassRule
    public static KafkaEmbedded kafkaEmbedded = new KafkaEmbedded( 1, true, RECEIVER_TOPIC, BOARD_EVENTS_SNAPSHOTS );

    @BeforeClass
    public static void setup() {
        System.setProperty( KAFKA_BROKERS_PROPERTY, kafkaEmbedded.getBrokersAsString() );
    }

    @AfterClass
    public static void clean() {
        System.clearProperty( KAFKA_BROKERS_PROPERTY );
    }

    private static Consumer<String, String> consumer;

    @BeforeClass
    public static void setUp() throws Exception {

        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("command-board-events-group", "false", kafkaEmbedded);
        consumerProps.put( ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest" );

        DefaultKafkaConsumerFactory<String, String> cf = new DefaultKafkaConsumerFactory<>( consumerProps );

        consumer = cf.createConsumer();

        kafkaEmbedded.consumeFromAnEmbeddedTopic( consumer, RECEIVER_TOPIC );


    }

    @AfterClass
    public static void tearDown() {

        consumer.close();

    }

    @Test
    public void testFind() throws Exception {

//        log.debug( "testFind : --spring.cloud.stream.kafka.streams.binder.brokers=" + kafkaEmbedded.getBrokersAsString() );
//        log.debug( "testFind : --spring.cloud.stream.kafka.streams.binder.zkNodes=" + kafkaEmbedded.getZookeeperConnectionString() );

        SpringApplication app = new SpringApplication( Application.class );
        app.setWebApplicationType( WebApplicationType.NONE );
        ConfigurableApplicationContext context = app.run("--server.port=0",
                "--spring.cloud.service-registry.auto-registration.enabled=false",
                "--spring.jmx.enabled=false",
                "--spring.cloud.stream.bindings.input.destination=board-events",
                "--spring.cloud.stream.bindings.output.binder=kafka",
                "--spring.cloud.stream.bindings.output.destination=board-events",
                "--spring.cloud.stream.kafka.streams.binder.configuration.commit.interval.ms=1000",
                "--spring.cloud.stream.bindings.output.producer.headerMode=raw",
                "--spring.cloud.stream.bindings.input.consumer.headerMode=raw",
                "--spring.cloud.stream.kafka.streams.binder.brokers=" + kafkaEmbedded.getBrokersAsString(),
                "--spring.cloud.stream.kafka.streams.binder.zkNodes=" + kafkaEmbedded.getZookeeperConnectionString(),
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

        Map<String, Object> senderProps = KafkaTestUtils.producerProps(kafkaEmbedded);
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
        assertThat( board.changes(), hasSize( 0 ) );

    }

    private BoardInitialized createTestBoardInitializedEvent( final UUID boardUuid ) {

        return new BoardInitialized( boardUuid, Instant.now() );
    }

}
