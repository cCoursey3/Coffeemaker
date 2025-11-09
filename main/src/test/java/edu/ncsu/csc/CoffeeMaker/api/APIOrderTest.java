package edu.ncsu.csc.CoffeeMaker.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import edu.ncsu.csc.CoffeeMaker.common.TestUtils;
import edu.ncsu.csc.CoffeeMaker.models.Orders;
import edu.ncsu.csc.CoffeeMaker.services.OrdersService;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith ( SpringExtension.class )
public class APIOrderTest {
    /**
     * MockMvc uses Spring's testing framework to handle requests to the REST
     * API
     */
    private MockMvc               mvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private OrdersService         orderService;

    /**
     * Sets up the tests.
     */
    @BeforeEach
    public void setup () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();
        orderService.deleteAll();
    }

    @Test
    @Transactional
    public void testGetOrderNotFound () {
        try {
            final int errNum = mvc.perform( get( "/api/v1/orders/0" ) ).andExpect( status().is4xxClientError() )
                    .andReturn().getResponse().getStatus();
            assertEquals( errNum, 404 );
        }
        catch ( final Exception e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    @Transactional
    public void testGetOrder () {
        final Orders order1 = new Orders( null, "preparing" );
        final Orders order2 = new Orders( null, "finished" );

        try {
            int status = mvc
                    .perform( post( "/api/v1/orders" ).contentType( MediaType.APPLICATION_JSON )
                            .content( TestUtils.asJsonString( order1 ) ) )
                    .andExpect( status().isOk() ).andReturn().getResponse().getStatus();
            assertEquals( 200, status );
            final Long id = orderService.findAll().get( 0 ).getId();
            status = mvc.perform( get( "/api/v1/orders/" + id ) ).andExpect( status().isOk() ).andReturn().getResponse()
                    .getStatus();
            assertEquals( 200, status );

            status = mvc
                    .perform( post( "/api/v1/orders" ).contentType( MediaType.APPLICATION_JSON )
                            .content( TestUtils.asJsonString( order2 ) ) )
                    .andExpect( status().isOk() ).andReturn().getResponse().getStatus();
            assertEquals( 200, status );

            status = mvc.perform( get( "/api/v1/orders" ) ).andExpect( status().isOk() ).andReturn().getResponse()
                    .getStatus();
            assertEquals( 200, status );
        }
        catch ( final Exception e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    @Transactional
    public void testCreateOrder () {
        final Orders order1 = new Orders( null, "preparing" );
        final Orders order2 = new Orders( null, "finished" );

        try {
            int status = mvc
                    .perform( put( "/api/v1/orders/0" ).contentType( MediaType.APPLICATION_JSON )
                            .content( TestUtils.asJsonString( order2 ) ) )
                    .andExpect( status().is4xxClientError() ).andReturn().getResponse().getStatus();
            assertEquals( 404, status );
            status = mvc
                    .perform( post( "/api/v1/orders" ).contentType( MediaType.APPLICATION_JSON )
                            .content( TestUtils.asJsonString( order1 ) ) )
                    .andExpect( status().isOk() ).andReturn().getResponse().getStatus();
            assertEquals( 200, status );
            status = mvc
                    .perform( put( "/api/v1/orders/0" ).contentType( MediaType.APPLICATION_JSON )
                            .content( TestUtils.asJsonString( null ) ) )
                    .andExpect( status().is4xxClientError() ).andReturn().getResponse().getStatus();
            assertEquals( 400, status );

            final Long id = orderService.findAll().get( 0 ).getId();
            status = mvc
                    .perform( put( "/api/v1/orders/" + id.toString() ).contentType( MediaType.APPLICATION_JSON )
                            .content( TestUtils.asJsonString( order2 ) ) )
                    .andExpect( status().isOk() ).andReturn().getResponse().getStatus();
            assertEquals( 200, status );
        }
        catch ( final Exception e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    @Transactional
    public void testDeleteOrder () {
        final Orders order1 = new Orders( null, "preparing" );

        try {

            final int status = mvc.perform( delete( "/api/v1/orders/0" ) ).andExpect( status().is4xxClientError() )
                    .andReturn().getResponse().getStatus();
            assertEquals( 404, status );

            orderService.save( order1 );
            final Long id = orderService.findAll().get( 0 ).getId();
            mvc.perform( delete( "/api/v1/orders/" + id ) ).andExpect( status().isOk() ).andReturn().getResponse()
                    .getContentAsString();

        }
        catch ( final Exception e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
