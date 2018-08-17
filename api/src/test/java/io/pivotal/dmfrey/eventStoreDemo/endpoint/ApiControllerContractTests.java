package io.pivotal.dmfrey.eventStoreDemo.endpoint;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties.StubsMode.LOCAL;
import static org.springframework.http.HttpHeaders.LOCATION;

@RunWith( SpringRunner.class )
@SpringBootTest(
        properties = {
                "stubrunner.idsToServiceIds.command=esd-command",
                "stubrunner.idsToServiceIds.query=esd-query",
                "spring.cloud.discovery.enabled=false",
                "spring.cloud.service-registry.auto-registration.enabled=false"
        }
)
@AutoConfigureStubRunner(
        ids = {
                "io.pivotal.dmfrey:command:+:stubs:8095",
                "io.pivotal.dmfrey:query:+:stubs:8096"
        },
//        stubsPerConsumer = true,
        stubsMode = LOCAL
)
@AutoConfigureWebTestClient
@DirtiesContext
public class ApiControllerContractTests {

    @Autowired
    private ApiController controller;

    private String boardUuid = "11111111-90ab-cdef-1234-567890abcdef";
    private String storyUuid = "10240df9-4a1e-4fa4-bbd1-0bb33d764603";

    @Test
    public void testCreateBoard() throws Exception {

        WebTestClient.bindToController( this.controller )
                .build()
                .post()
                .uri(  "/boards" )
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().exists( LOCATION );

    }

    @Test
    public void testRenameBoard() throws Exception {

        WebTestClient.bindToController( this.controller )
                .build()
                .patch()
                .uri(  "/boards/{boardUuid}?name={name}", boardUuid, "New Name" )
                .exchange()
                .expectStatus().isAccepted();

    }

    @Test
    public void testCreateStoryOnBoard() throws Exception {

        WebTestClient.bindToController( this.controller )
                .build()
                .post()
                .uri(  "/boards/{boardUuid}/stories?name={name}", boardUuid, "New Story Name" )
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().exists( LOCATION );

    }

    @Test
    public void testUpdateStoryOnBoard() throws Exception {

        WebTestClient.bindToController( this.controller )
                .build()
                .put()
                .uri(  "/boards/{boardUuid}/stories/{storyUuid}?name={name}", boardUuid, storyUuid, "Updated Story Name" )
                .exchange()
                .expectStatus().isAccepted();

    }

    @Test
    public void testDeleteStoryOnBoard() throws Exception {

        WebTestClient.bindToController( this.controller )
                .build()
                .delete()
                .uri(  "/boards/{boardUuid}/stories/{storyUuid}", boardUuid, storyUuid )
                .exchange()
                .expectStatus().isAccepted();

    }

    @Test
    public void testGetExistingBoard() throws Exception {

        WebTestClient.bindToController( this.controller )
                .build()
                .get()
                .uri(  "/boards/{boardUuid}", boardUuid )
                .accept( MediaType.APPLICATION_JSON )
                .exchange()
                .expectStatus().isOk();
//                .expectBody().jsonPath( "$.name", "New Board" );

    }

}
