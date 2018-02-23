package io.pivotal.dmfrey.eventStoreDemo.endpoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.pivotal.dmfrey.eventStoreDemo.domain.model.DomainEvents;
import io.pivotal.dmfrey.eventStoreDemo.domain.service.DomainEventService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.tuple.Tuple;

import static java.util.Collections.singletonList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith( SpringRunner.class )
@WebMvcTest( EventStoreController.class )
public class EventStoreControllerTests {

    private static final String BOARD_INITIALIZED_EVENT = "{\"eventType\":\"BoardInitialized\",\"boardUuid\":\"ff4795e1-2514-4f5a-90e2-cd33dfadfbf2\",\"occurredOn\":\"2018-02-23T03:49:52.313Z\"}";

    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mockMvc;

    @MockBean
    DomainEventService service;

    @Test
    public void testSaveEvents() throws Exception {

        this.mockMvc.perform( post( "/" ).content( BOARD_INITIALIZED_EVENT ) )
                .andDo( print() )
                .andExpect( status().isAccepted() );

        verify( this.service, times( 1 ) ).processDomainEvent( any( Tuple.class ) );

    }

    @Test
    public void testDomainEvents() throws Exception {

        DomainEvents domainEvents = createDomainEvents();
        String domainEventsJson = mapper.writeValueAsString( domainEvents );

        when( this.service.getDomainEvents( anyString() ) ).thenReturn( domainEvents );

        this.mockMvc.perform( get( "/{boardUuid}", domainEvents.getBoardUuid() ) )
                .andDo( print() )
                .andExpect( status().isOk() )
                .andExpect( content().json( domainEventsJson ) );

        verify( this.service, times( 1 ) ).getDomainEvents( anyString() );

    }

    private DomainEvents createDomainEvents() {

        DomainEvents domainEvents = new DomainEvents();
        domainEvents.setBoardUuid( "ff4795e1-2514-4f5a-90e2-cd33dfadfbf2" );
        domainEvents.setDomainEvents( singletonList( BOARD_INITIALIZED_EVENT ) );

        return domainEvents;
    }

}
