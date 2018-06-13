package io.pivotal.dmfrey.eventStoreDemo.domain.client.kafka;

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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

@RunWith( SpringRunner.class )
@SpringBootTest( classes = { KafkaClientConfig.class, DomainEventSourceImpl.class },
        properties = {
                "--spring.cloud.service-registry.auto-registration.enabled=false",
                "--spring.cloud.stream.bindings.output.binder=kafka"
        }
)
@ActiveProfiles( "kafka" )
@DirtiesContext
@Slf4j
public class KafkaBoardClientTests {

    @Autowired
    BoardClient boardClient;

    @MockBean
    QueryableStoreRegistry queryableStoreRegistry;

    @Test
    public void testSave() throws Exception {

        UUID boardUuid = UUID.randomUUID();
        Board board = createTestBoard( boardUuid );

        this.boardClient.save( board );

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
