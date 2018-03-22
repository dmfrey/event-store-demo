package io.pivotal.dmfrey.eventStoreDemo;

import io.pivotal.dmfrey.eventStoreDemo.domain.service.BoardService;
import io.pivotal.dmfrey.eventStoreDemo.endpoint.ApiController;
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
    private ApiController controller;

    @MockBean
    private BoardService service;

    private UUID boardUuid = UUID.fromString( "12345678-90ab-cdef-1234-567890abcdef" );

    @Before
    public void setup() {

//        when( this.service.createBoard() ).thenReturn( this.boardUuid );

        RestAssuredMockMvc.standaloneSetup( this.controller );

    }

}
