package io.pivotal.dmfrey.eventStoreDemo.domain.service;

import io.pivotal.dmfrey.eventStoreDemo.domain.model.Board;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;
import static org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties.StubsMode.LOCAL;

@RunWith( SpringRunner.class )
@SpringBootTest( webEnvironment = MOCK,
    properties = {
            "spring.cloud.service-registry.auto-registration.enabled=false",
            "stubrunner.idsToServiceIds.command=command",
            "stubrunner.idsToServiceIds.query=query"
    }
)
@AutoConfigureStubRunner(
        ids = {
                "io.pivotal.dmfrey:command:+:9080",
                "io.pivotal.dmfrey:query:+:9081"
        },
        stubsMode = LOCAL
)
@DirtiesContext
public class BoardServiceContractTests {

    @Autowired
    private BoardService service;

    static {

        System.setProperty( "eureka.client.enabled", "false" );
        System.setProperty( "spring.cloud.config.failFast", "false" );

    }

    @Test
    public void testCreateBoard() throws Exception {

        ResponseEntity response = this.service.createBoard();
        assertThat( response.getStatusCode() ).isEqualTo( HttpStatus.CREATED );
        assertThat( response.getHeaders() )
                .containsKey( HttpHeaders.LOCATION );

    }

    @Test
    public void testRenameBoard() throws Exception {

        UUID boardUuid = UUID.randomUUID();

        ResponseEntity response = this.service.renameBoard( boardUuid, "My Board" );
        assertThat( response.getStatusCode() ).isEqualTo( HttpStatus.ACCEPTED );

    }

    @Test
    public void testAddStory() throws Exception {

        UUID boardUuid = UUID.randomUUID();

        ResponseEntity response = this.service.addStory( boardUuid, "My Story 1" );
        assertThat( response.getStatusCode() ).isEqualTo( HttpStatus.CREATED );
        assertThat( response.getHeaders() )
                .containsKey( HttpHeaders.LOCATION );

    }

    @Test
    public void testUpdateStory() throws Exception {

        UUID boardUuid = UUID.randomUUID();
        UUID storyUuid = UUID.randomUUID();

        ResponseEntity response = this.service.updateStory( boardUuid, storyUuid, "My Story Updated" );
        assertThat( response.getStatusCode() ).isEqualTo( HttpStatus.ACCEPTED );

    }

    @Test
    public void testDeleteStory() throws Exception {

        UUID boardUuid = UUID.randomUUID();
        UUID storyUuid = UUID.randomUUID();

        ResponseEntity response = this.service.deleteStory( boardUuid, storyUuid );
        assertThat( response.getStatusCode() ).isEqualTo( HttpStatus.ACCEPTED );

    }

    @Test
    public void testBoardStory() throws Exception {

        UUID boardUuid = UUID.randomUUID();

        ResponseEntity<Board> response = this.service.board( boardUuid );
        assertThat( response.getStatusCode() ).isEqualTo( HttpStatus.OK );
        assertThat( response.getBody() ).isNotNull();

    }

}
