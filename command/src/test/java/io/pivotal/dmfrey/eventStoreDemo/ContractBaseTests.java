package io.pivotal.dmfrey.eventStoreDemo;

import io.pivotal.dmfrey.eventStoreDemo.domain.service.BoardService;
import io.pivotal.dmfrey.eventStoreDemo.endpoint.CommandsController;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.verifier.messaging.MessageVerifier;
import org.springframework.cloud.contract.verifier.messaging.boot.AutoConfigureMessageVerifier;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith( SpringRunner.class )
@SpringBootTest(
        properties = {
                "--spring.cloud.service-registry.auto-registration.enabled=false"
        }
)
@AutoConfigureMessageVerifier
public abstract class ContractBaseTests {

    @Autowired
    private MessageVerifier verifier;

    @Autowired
    private CommandsController controller;

    @MockBean
    private BoardService service;

    private UUID boardUuid = UUID.fromString( "11111111-90ab-cdef-1234-567890abcdef" );
    private UUID storyUuid = UUID.fromString( "10240df9-4a1e-4fa4-bbd1-0bb33d764603" );

    @Before
    public void setup() {

        when( this.service.createBoard() ).thenReturn( this.boardUuid );
        when( this.service.addStory( any( UUID.class ), anyString() ) ).thenReturn( this.storyUuid );

        RestAssuredMockMvc.standaloneSetup( this.controller );

        verifyNoMoreInteractions( this.service );

    }

}
