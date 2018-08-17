//package io.pivotal.dmfrey.eventStoreDemo.contracts;
//
//import io.pivotal.dmfrey.eventStoreDemo.Application;
//import io.pivotal.dmfrey.eventStoreDemo.domain.client.BoardClient;
//import io.pivotal.dmfrey.eventStoreDemo.domain.model.Board;
//import org.apache.kafka.clients.consumer.Consumer;
//import org.apache.kafka.clients.consumer.ConsumerConfig;
//import org.junit.AfterClass;
//import org.junit.BeforeClass;
//import org.junit.ClassRule;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.WebApplicationType;
//import org.springframework.cloud.contract.verifier.messaging.amqp.ContractVerifierAmqpAutoConfiguration;
//import org.springframework.cloud.contract.verifier.messaging.amqp.RabbitMockConnectionFactoryAutoConfiguration;
//import org.springframework.cloud.contract.verifier.messaging.boot.AutoConfigureMessageVerifier;
//import org.springframework.cloud.contract.verifier.messaging.integration.ContractVerifierIntegrationConfiguration;
//import org.springframework.cloud.contract.verifier.messaging.noop.NoOpContractVerifierAutoConfiguration;
//import org.springframework.cloud.contract.verifier.messaging.stream.ContractVerifierStreamAutoConfiguration;
//import org.springframework.context.ConfigurableApplicationContext;
//import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
//import org.springframework.kafka.test.rule.KafkaEmbedded;
//import org.springframework.kafka.test.utils.KafkaTestUtils;
//
//import java.time.Instant;
//import java.util.Map;
//import java.util.UUID;
//
//import static io.pivotal.dmfrey.eventStoreDemo.domain.client.kafka.config.KafkaClientConfig.BOARD_EVENTS_SNAPSHOTS;
//
////@RunWith( SpringRunner.class )
////@SpringBootTest(
////        webEnvironment = NONE,
////        properties = {
////                "--spring.cloud.service-registry.auto-registration.enabled=false"
////        }
////)
////@ActiveProfiles( "kafka" )
//public abstract class KafkaBase {
//
//    private static String RECEIVER_TOPIC = "board-events";
//
//    private static final String KAFKA_BROKERS_PROPERTY = "spring.kafka.bootstrap-servers";
//
//    @ClassRule
//    public static KafkaEmbedded kafkaEmbedded = new KafkaEmbedded( 1, true, RECEIVER_TOPIC, BOARD_EVENTS_SNAPSHOTS );
//
//    @BeforeClass
//    public static void setup() {
//        System.setProperty( KAFKA_BROKERS_PROPERTY, kafkaEmbedded.getBrokersAsString() );
//    }
//
//    @AfterClass
//    public static void clean() {
//        System.clearProperty( KAFKA_BROKERS_PROPERTY );
//    }
//
//    private static Consumer<String, String> consumer;
//
//    @BeforeClass
//    public static void setUp() throws Exception {
//
//        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("command-board-events-group", "false", kafkaEmbedded);
//        consumerProps.put( ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest" );
//
//        DefaultKafkaConsumerFactory<String, String> cf = new DefaultKafkaConsumerFactory<>( consumerProps );
//
//        consumer = cf.createConsumer();
//
//        kafkaEmbedded.consumeFromAnEmbeddedTopic( consumer, RECEIVER_TOPIC );
//
//
//    }
//
//    @AfterClass
//    public static void tearDown() {
//
//        consumer.close();
//
//    }
//
//    private UUID boardUuid = UUID.fromString( "11111111-90ab-cdef-1234-567890abcdef" );
//    private Instant boardInstant = Instant.parse( "2018-08-16T16:45:32.123Z" );
//
//    private UUID storyUuid = UUID.fromString( "10240df9-4a1e-4fa4-bbd1-0bb33d764603" );
//
//    public void publishBoardInitialized() {
//
//        SpringApplication app =
//                new SpringApplication(
//                        Application.class, AutoConfigureMessageVerifier.class,
//                        ContractVerifierStreamAutoConfiguration.class, ContractVerifierIntegrationConfiguration.class,
//                        ContractVerifierAmqpAutoConfiguration.class, RabbitMockConnectionFactoryAutoConfiguration.class,
//                        NoOpContractVerifierAutoConfiguration.class
//                );
//        app.setWebApplicationType( WebApplicationType.NONE );
//        ConfigurableApplicationContext context = app.run("--server.port=0",
//                "--spring.cloud.service-registry.auto-registration.enabled=false",
//                "--spring.jmx.enabled=false",
//                "--spring.cloud.stream.bindings.input.destination=board-events",
//                "--spring.cloud.stream.bindings.output.binder=kafka",
//                "--spring.cloud.stream.bindings.output.destination=board-events",
//                "--spring.cloud.stream.kafka.streams.binder.configuration.commit.interval.ms=1000",
//                "--spring.cloud.stream.bindings.output.producer.headerMode=raw",
//                "--spring.cloud.stream.bindings.input.consumer.headerMode=raw",
//                "--spring.cloud.stream.kafka.streams.binder.brokers=" + kafkaEmbedded.getBrokersAsString(),
//                "--spring.cloud.stream.kafka.streams.binder.zkNodes=" + kafkaEmbedded.getZookeeperConnectionString(),
//                "--spring.profiles.active=kafka",
//                "--spring.jackson.serialization.write_dates_as_timestamps=false",
//                "--logger.level.io.pivotal.dmfrey=DEBUG");
//
//        BoardClient boardClient = context.getBean( BoardClient.class );
//        boardClient.save( new Board( boardUuid ) );
//
//    }
//
//}
