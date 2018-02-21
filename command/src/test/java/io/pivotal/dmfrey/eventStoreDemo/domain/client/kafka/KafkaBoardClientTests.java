package io.pivotal.dmfrey.eventStoreDemo.domain.client.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.pivotal.dmfrey.eventStoreDemo.domain.client.BoardClient;
import io.pivotal.dmfrey.eventStoreDemo.domain.client.kafka.config.KafkaClientConfig;
import io.pivotal.dmfrey.eventStoreDemo.domain.client.kafka.config.KafkaConfig;
import io.pivotal.dmfrey.eventStoreDemo.domain.client.kafka.service.DomainEventSink;
import io.pivotal.dmfrey.eventStoreDemo.domain.client.kafka.service.DomainEventSourceImpl;
import io.pivotal.dmfrey.eventStoreDemo.domain.events.BoardInitialized;
import io.pivotal.dmfrey.eventStoreDemo.domain.events.DomainEvents;
import io.pivotal.dmfrey.eventStoreDemo.domain.model.Board;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.internals.Sender;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.cloud.stream.test.binder.MessageCollector;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.listener.config.ContainerProperties;
import org.springframework.kafka.test.rule.KafkaEmbedded;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.springframework.cloud.stream.test.matcher.MessageQueueMatcher.receivesPayloadThat;

@RunWith( SpringRunner.class )
@SpringBootTest( classes = { KafkaConfig.class, KafkaClientConfig.class, DomainEventSourceImpl.class, DomainEventSink.class },
        webEnvironment= SpringBootTest.WebEnvironment.NONE,
        properties = {
            "spring.cloud.service-registry.auto-registration.enabled=false" //,
//            "spring.cloud.stream.bindings.input.consumer.useNativeDecoding=true",
//            "spring.cloud.stream.bindings.output.producer.useNativeEncoding=true",
//            "spring.cloud.stream.bindings.input.group=group",
//            "spring.cloud.stream.kafka.streams.binder.serdeError=sendToDlq",
//            "spring.cloud.stream.kafka.streams.binder.configuration.default.value.serde=" +
//                    "org.apache.kafka.common.serialization.Serdes$IntegerSerde"
        }
)
@ActiveProfiles( "kafka" )
@DirtiesContext
@Slf4j
public class KafkaBoardClientTests {

    private static String SENDER_TOPIC = "board-events";

    @Autowired
    private Sender sender;

    private KafkaMessageListenerContainer<String, String> container;

    private BlockingQueue<ConsumerRecord<String, String>> records;

    @ClassRule
    public static KafkaEmbedded embeddedKafka = new KafkaEmbedded( 1, true, SENDER_TOPIC );

    @Before
    public void setUp() throws Exception {

        // set up the Kafka consumer properties
        Map<String, Object> consumerProperties = KafkaTestUtils.consumerProps("sender", "false", embeddedKafka );

        // create a Kafka consumer factory
        DefaultKafkaConsumerFactory<String, String> consumerFactory = new DefaultKafkaConsumerFactory<>( consumerProperties );

        // set the topic that needs to be consumed
        ContainerProperties containerProperties = new ContainerProperties( SENDER_TOPIC );

        // create a Kafka MessageListenerContainer
        container = new KafkaMessageListenerContainer<>( consumerFactory, containerProperties );

        // create a thread safe queue to store the received message
        records = new LinkedBlockingQueue<>();

        // setup a Kafka message listener
        container.setupMessageListener( (MessageListener<String, String>) record -> {
            log.debug( "test-listener received message='{}'", record.toString() );
            records.add( record );
        });

        // start the container and underlying message listener
        container.start();

        // wait until the container has the required number of assigned partitions
        ContainerTestUtils.waitForAssignment( container, embeddedKafka.getPartitionsPerTopic() );

    }

    @After
    public void tearDown() {

        // stop the container
        container.stop();

    }

    @Autowired
    private Source source;

    @Autowired
    private MessageCollector collector;

    @Autowired
    BoardClient boardClient;

    @Autowired
    ObjectMapper mapper;

    @Test
    public void testSave() throws Exception {

        BlockingQueue<Message<?>> messages = collector.forChannel( source.output() );

        UUID boardUuid = UUID.randomUUID();
        Board board = createTestBoard( boardUuid );
        String event = mapper.writeValueAsString( board.changes().get( 0 ) );

        this.boardClient.save( board );

        assertThat( messages, receivesPayloadThat( is( event ) ) );

    }

    @Test
    public void testFind() throws Exception {

        UUID boardUuid = UUID.randomUUID();
        BoardInitialized boardInitialized = createTestBoardInitializedEvent( boardUuid );
        String event = mapper.writeValueAsString( boardInitialized );
        this.source.output().send( new GenericMessage<>( event ) );

        DomainEvents domainEvents = new DomainEvents();
        domainEvents.setBoardUuid( boardUuid );
        domainEvents.getDomainEvents().add( createTestBoardInitializedEvent( boardUuid ) );

        Board board = this.boardClient.find( boardUuid );
        assertThat( board, is( notNullValue() ) );
        assertThat( board.getBoardUuid(), is( equalTo( boardUuid ) ) );
        assertThat( board.getName(), is( equalTo( "New Board" ) ) );
        assertThat( board.getStories().isEmpty(), is( equalTo( true ) ) );
        assertThat( board.changes(), hasSize( 0 ) );

    }

    @Test( expected = IllegalArgumentException.class )
    @Ignore
    public void testFindNotFound() throws Exception {

        UUID boardUuid = UUID.randomUUID();
        DomainEvents domainEvents = new DomainEvents();
        domainEvents.setBoardUuid( boardUuid );

//        when( this.eventStoreClient.getDomainEventsForBoardUuid( any( UUID.class ) ) ).thenReturn( domainEvents );

        this.boardClient.find( boardUuid );

//        verify( this.eventStoreClient, times( 1 ) ).addNewDomainEvent( any( DomainEvent.class ) );

    }

    private BoardInitialized createTestBoardInitializedEvent( final UUID boardUuid ) {

        return new BoardInitialized( boardUuid, Instant.now() );
    }

    private Board createTestBoard( final UUID boardUuid ) {

        Board board = new Board( boardUuid );
        assertThat( board, is( not( nullValue() )) );
        assertThat( board.getBoardUuid(), is( equalTo( boardUuid ) ) );
        assertThat( board.getName(), is( equalTo( "New Board" ) ) );
        assertThat( board.changes(), hasSize( 1 ) );

        return board;
    }

}
