package edu.ncsu.csc.CoffeeMaker.api;

import static org.junit.Assert.fail;
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
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import edu.ncsu.csc.CoffeeMaker.common.TestUtils;
import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.services.IngredientService;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith ( SpringExtension.class )
public class APIIngredientTest {
    /**
     * MockMvc uses Spring's testing framework to handle requests to the REST
     * API
     */
    private MockMvc               mvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private IngredientService     ingrService;

    /**
     * Sets up the tests.
     */
    @BeforeEach
    public void setup () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();
        ingrService.deleteAll();
    }

    @Test
    @Transactional
    @WithMockUser ( authorities = { "ADD_INGREDIENTS", "ADD_INGREDIENTS_TYPE", "MAKE_COFFEE", "ADD_STAFF_ACCOUNT" } )
    public void testGetIngredientNotFound () {
        try {
            final int errNum = mvc.perform( get( "/api/v1/ingredients/MOCHA" ) )
                    .andExpect( status().is4xxClientError() ).andReturn().getResponse().getStatus();
            assertEquals( 404, errNum );
        }
        catch ( final Exception e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            fail();
        }
    }

    @Test
    @Transactional
    @WithMockUser ( authorities = { "ADD_INGREDIENTS", "ADD_INGREDIENTS_TYPE", "MAKE_COFFEE", "ADD_STAFF_ACCOUNT" } )
    public void testGetIngredient () {
        final Ingredient ingr1 = new Ingredient( "Coffee", 1 );
        try {
            int status = mvc
                    .perform( post( "/api/v1/ingredients" ).contentType( MediaType.APPLICATION_JSON )
                            .content( TestUtils.asJsonString( ingr1 ) ) )
                    .andExpect( status().isOk() ).andReturn().getResponse().getStatus();
            assertEquals( 200, status );
            status = mvc.perform( get( "/api/v1/ingredients/COFFEE" ) ).andExpect( status().isOk() ).andReturn()
                    .getResponse().getStatus();
            assertEquals( 200, status );
        }
        catch ( final Exception e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            fail();
        }
    }

    @Test
    @Transactional
    @WithMockUser ( authorities = { "ADD_INGREDIENTS", "ADD_INGREDIENTS_TYPE", "MAKE_COFFEE", "ADD_STAFF_ACCOUNT" } )
    public void testCreateIngredient () {
        final Ingredient ingr1 = new Ingredient( "Coffee", 1 );
        try {
            int status = mvc
                    .perform( put( "/api/v1/ingredients/dne" ).contentType( MediaType.APPLICATION_JSON )
                            .content( TestUtils.asJsonString( 60 ) ) )
                    .andExpect( status().is4xxClientError() ).andReturn().getResponse().getStatus();
            assertEquals( 404, status );

            status = mvc
                    .perform( post( "/api/v1/ingredients" ).contentType( MediaType.APPLICATION_JSON )
                            .content( TestUtils.asJsonString( ingr1 ) ) )
                    .andExpect( status().isOk() ).andReturn().getResponse().getStatus();
            assertEquals( 200, status );
            status = mvc
                    .perform( put( "/api/v1/ingredients/dne" ).contentType( MediaType.APPLICATION_JSON )
                            .content( TestUtils.asJsonString( -60 ) ) )
                    .andExpect( status().is4xxClientError() ).andReturn().getResponse().getStatus();
            assertEquals( 400, status );
            status = mvc
                    .perform( put( "/api/v1/ingredients/Coffee" ).contentType( MediaType.APPLICATION_JSON )
                            .content( TestUtils.asJsonString( 60 ) ) )
                    .andExpect( status().isOk() ).andReturn().getResponse().getStatus();
            assertEquals( 200, status );

        }
        catch ( final Exception e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            fail();
        }
    }

    @Test
    @Transactional
    @WithMockUser ( authorities = { "ADD_INGREDIENTS", "ADD_INGREDIENTS_TYPE", "MAKE_COFFEE", "ADD_STAFF_ACCOUNT" } )
    public void testCreateIngredientError () {
        final Ingredient ingr1 = new Ingredient( "Coffee", 1 );
        try {
            mvc.perform( post( "/api/v1/ingredients" ).contentType( MediaType.APPLICATION_JSON )
                    .content( TestUtils.asJsonString( ingr1 ) ) );
            final MockHttpServletResponse err = mvc
                    .perform( post( "/api/v1/ingredients" ).contentType( MediaType.APPLICATION_JSON )
                            .content( TestUtils.asJsonString( ingr1 ) ) )
                    .andExpect( status().is4xxClientError() ).andReturn().getResponse();
            assertEquals( 409, err.getStatus() );
        }
        catch ( final Exception e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            fail();
        }
    }

    @Test
    @Transactional
    @WithMockUser ( authorities = { "ADD_INGREDIENTS", "ADD_INGREDIENTS_TYPE", "MAKE_COFFEE", "ADD_STAFF_ACCOUNT" } )
    public void testDeleteIngredient () {
        try {
            mvc.perform( delete( "/api/v1/ingredients/COFFEE" ) ).andExpect( status().is4xxClientError() ).andReturn()
                    .getResponse().getContentAsString();
            final Ingredient ingr1 = new Ingredient( "Coffee", 1 );
            ingrService.save( ingr1 );
            mvc.perform( delete( "/api/v1/ingredients/COFFEE" ) ).andExpect( status().isOk() ).andReturn().getResponse()
                    .getContentAsString();

        }
        catch ( final Exception e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            fail();
        }
    }

    @Test
    @Transactional
    @WithMockUser ( authorities = { "ADD_INGREDIENTS", "MAKE_COFFEE", "ADD_STAFF_ACCOUNT" } )
    public void testDeleteIngredientNoAuth () {
        try {
            final int status = mvc.perform( delete( "/api/v1/ingredients/COFFEE" ) )
                    .andExpect( status().is4xxClientError() ).andReturn().getResponse().getStatus();
            assertEquals( 403, status );

        }
        catch ( final Exception e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            fail();
        }
    }
}
