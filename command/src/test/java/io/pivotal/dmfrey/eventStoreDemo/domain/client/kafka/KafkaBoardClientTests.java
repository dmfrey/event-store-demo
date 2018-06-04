package io.pivotal.dmfrey.eventStoreDemo.domain.client.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.pivotal.dmfrey.eventStoreDemo.domain.client.BoardClient;
import io.pivotal.dmfrey.eventStoreDemo.domain.client.kafka.config.KafkaClientConfig;
import io.pivotal.dmfrey.eventStoreDemo.domain.client.kafka.service.DomainEventSourceImpl;
import io.pivotal.dmfrey.eventStoreDemo.domain.model.Board;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.stream.binder.kafka.streams.QueryableStoreRegistry;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Instant;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith( SpringRunner.class )
@SpringBootTest( classes = { KafkaClientConfig.class, DomainEventSourceImpl.class },
        properties = {
            "--spring.cloud.service-registry.auto-registration.enabled=false"
        }
)
@ActiveProfiles( "kafka" )
@DirtiesContext
@Slf4j
public class KafkaBoardClientTests {

    @Autowired
    private Source source;

    @Autowired
    BoardClient boardClient;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    QueryableStoreRegistry queryableStoreRegistry;

    @Test
    public void testSave() throws Exception {

        UUID boardUuid = UUID.randomUUID();
        Instant now = Instant.now();
        Board board = createTestBoard( boardUuid, now );
        String event = mapper.writeValueAsString( board.changes().get( 0 ) );

        this.boardClient.save( board );

        verifyNoMoreInteractions( this.queryableStoreRegistry );

    }

    private Board createTestBoard( final UUID boardUuid, final Instant ts ) {

        Board board = new Board( boardUuid );
        board.initialize( ts );
        assertThat( board, is( not( nullValue() )) );
        assertThat( board.getBoardUuid(), is( equalTo( boardUuid ) ) );
        assertThat( board.getName(), is( equalTo( "New Board" ) ) );
        assertThat( board.changes(), hasSize( 1 ) );

        return board;
    }

}
