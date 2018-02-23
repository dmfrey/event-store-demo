package io.pivotal.dmfrey.eventStoreDemo.domain.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.cloud.stream.test.binder.MessageCollector;
import org.springframework.messaging.Message;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.tuple.Tuple;
import org.springframework.tuple.TupleBuilder;

import java.util.concurrent.BlockingQueue;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.cloud.stream.test.matcher.MessageQueueMatcher.receivesPayloadThat;

@RunWith( SpringRunner.class )
@SpringBootTest(
        properties = {
                "--spring.cloud.service-registry.auto-registration.enabled=false"
        }
)
@DirtiesContext
public class NotificationPublisherTests {

    private static final String BOARD_INITIALIZED_EVENT = "{\"eventType\":\"BoardInitialized\",\"boardUuid\":\"ff4795e1-2514-4f5a-90e2-cd33dfadfbf2\",\"occurredOn\":\"2018-02-23T03:49:52.313Z\"}";

    @Autowired
    private Source source;

    @Autowired
    private MessageCollector collector;

    @Autowired
    private NotificationPublisher notificationPublisher;

    @Test
    public void testSendNotification() throws Exception {

        BlockingQueue<Message<?>> messages = collector.forChannel( source.output() );

        Tuple event = TupleBuilder.fromString( BOARD_INITIALIZED_EVENT );
        this.notificationPublisher.sendNotification( event );

        assertThat( messages, receivesPayloadThat( is( BOARD_INITIALIZED_EVENT ) ) );

    }

}
