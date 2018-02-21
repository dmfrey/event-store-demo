package io.pivotal.dmfrey.eventStoreDemo.domain.client.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.pivotal.dmfrey.eventStoreDemo.domain.client.BoardClient;
import io.pivotal.dmfrey.eventStoreDemo.domain.client.kafka.config.KafkaClientConfig;
import io.pivotal.dmfrey.eventStoreDemo.domain.client.kafka.config.KafkaConfig;
import io.pivotal.dmfrey.eventStoreDemo.domain.client.kafka.service.DomainEventSink;
import io.pivotal.dmfrey.eventStoreDemo.domain.client.kafka.service.DomainEventSource;
import io.pivotal.dmfrey.eventStoreDemo.domain.client.kafka.service.DomainEventSourceImpl;
import io.pivotal.dmfrey.eventStoreDemo.domain.events.BoardInitialized;
import io.pivotal.dmfrey.eventStoreDemo.domain.events.DomainEvents;
import io.pivotal.dmfrey.eventStoreDemo.domain.model.Board;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.test.rule.KafkaEmbedded;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

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
public class KafkaBoardClientEmbeddedKafkaTests {

    private static String RECEIVER_TOPIC = "board-events";

    private KafkaTemplate<String, String> template;

    @Autowired
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @ClassRule
    public static KafkaEmbedded embeddedKafka = new KafkaEmbedded( 1, true, RECEIVER_TOPIC);

    @Before
    public void setUp() throws Exception {

        // set up the Kafka producer properties
        Map<String, Object> senderProperties = KafkaTestUtils.senderProps( embeddedKafka.getBrokersAsString() );

        // create a Kafka producer factory
        ProducerFactory<String, String> producerFactory = new DefaultKafkaProducerFactory<>( senderProperties );

        // create a Kafka template
        template = new KafkaTemplate<>( producerFactory );
        // set the default topic to send to
        template.setDefaultTopic( RECEIVER_TOPIC );

        // wait until the partitions are assigned
        for( MessageListenerContainer messageListenerContainer : kafkaListenerEndpointRegistry.getListenerContainers() ) {

            ContainerTestUtils.waitForAssignment( messageListenerContainer, embeddedKafka.getPartitionsPerTopic() );

        }

    }

    @Autowired
    BoardClient boardClient;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    DomainEventSource domainEventSource;

    @Test
    public void testFind() throws Exception {

        UUID boardUuid = UUID.randomUUID();
        BoardInitialized boardInitialized = createTestBoardInitializedEvent( boardUuid );
        String event = mapper.writeValueAsString( boardInitialized );
        this.template.sendDefault( event );

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

    private BoardInitialized createTestBoardInitializedEvent( final UUID boardUuid ) {

        return new BoardInitialized( boardUuid, Instant.now() );
    }

}
