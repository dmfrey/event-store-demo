package io.pivotal.dmfrey.eventStoreDemo.contracts;

import io.pivotal.dmfrey.eventStoreDemo.domain.model.Board;
import io.pivotal.dmfrey.eventStoreDemo.domain.service.BoardService;
import io.pivotal.dmfrey.eventStoreDemo.endpoint.QueryController;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

@RunWith( SpringRunner.class )
@SpringBootTest(
        webEnvironment = NONE,
        classes = QueryController.class,
        properties = {
                "--spring.cloud.service-registry.auto-registration.enabled=false"
        }
)
public abstract class ApiBase {

    @Autowired
    private QueryController controller;

    @MockBean
    private BoardService service;

    private UUID boardUuid = UUID.fromString( "11111111-90ab-cdef-1234-567890abcdef" );
    private UUID storyUuid = UUID.fromString( "10240df9-4a1e-4fa4-bbd1-0bb33d764603" );

    @Before
    public void setup() {

        when( this.service.find( any( UUID.class ) ) ).thenReturn( new Board( boardUuid ) );

        RestAssuredMockMvc.standaloneSetup( this.controller );

        verifyNoMoreInteractions( this.service );

    }

}
