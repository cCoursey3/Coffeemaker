package edu.ncsu.csc.CoffeeMaker.api;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith ( SpringExtension.class )
public class MappingTest {
    /**
     * MockMvc uses Spring's testing framework to handle requests to the REST
     * API
     */
    private MockMvc               mvc;

    @Autowired
    private WebApplicationContext context;

    /**
     * Sets up the tests.
     */
    @BeforeEach
    public void setup () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();
    }

    @Test
    public void testIndex () {
        String res;
        try {
            res = mvc.perform( get( "/" ) ).andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();
            assertTrue( res.contains( "CoffeeMaker" ) );

            res = mvc.perform( get( "/index" ) ).andExpect( status().isOk() ).andReturn().getResponse()
                    .getContentAsString();
            assertTrue( res.contains( "CoffeeMaker" ) );
        }
        catch ( final Exception e ) {
            e.printStackTrace();
            fail( "Failed to make request" );
        }
    }

    @Test
    public void testRecipe () {
        String res;
        try {
            res = mvc.perform( get( "/addrecipe" ) ).andExpect( status().isOk() ).andReturn().getResponse()
                    .getContentAsString();
            assertTrue( res.contains( "Add Recipe" ) );

            res = mvc.perform( get( "/addrecipe.html" ) ).andExpect( status().isOk() ).andReturn().getResponse()
                    .getContentAsString();
            assertTrue( res.contains( "Add Recipe" ) );
        }
        catch ( final Exception e ) {
            e.printStackTrace();
            fail( "Failed to make request" );
        }
    }

    @Test
    public void testDeleteRecipe () {
        String res;
        try {
            res = mvc.perform( get( "/deleterecipe" ) ).andExpect( status().isOk() ).andReturn().getResponse()
                    .getContentAsString();
            assertTrue( res.contains( "Delete Recipes" ) );

            res = mvc.perform( get( "/deleterecipe.html" ) ).andExpect( status().isOk() ).andReturn().getResponse()
                    .getContentAsString();
            assertTrue( res.contains( "Delete Recipes" ) );
        }
        catch ( final Exception e ) {
            e.printStackTrace();
            fail( "Failed to make request" );
        }
    }

    @Test
    public void testInventory () {
        String res;
        try {
            res = mvc.perform( get( "/inventory" ) ).andExpect( status().isOk() ).andReturn().getResponse()
                    .getContentAsString();
            assertTrue( res.contains( "Inventory" ) );

            res = mvc.perform( get( "/inventory.html" ) ).andExpect( status().isOk() ).andReturn().getResponse()
                    .getContentAsString();
            assertTrue( res.contains( "Inventory" ) );
        }
        catch ( final Exception e ) {
            e.printStackTrace();
            fail( "Failed to make request" );
        }
    }

    @Test
    public void testMakeCoffee () {
        String res;
        try {
            res = mvc.perform( get( "/makecoffee" ) ).andExpect( status().isOk() ).andReturn().getResponse()
                    .getContentAsString();
            assertTrue( res.contains( "Make Coffee" ) );

            res = mvc.perform( get( "/makecoffee.html" ) ).andExpect( status().isOk() ).andReturn().getResponse()
                    .getContentAsString();
            assertTrue( res.contains( "Make Coffee" ) );
        }
        catch ( final Exception e ) {
            e.printStackTrace();
            fail( "Failed to make request" );
        }
    }

    @Test
    public void testCreateStaff () {
        String res;
        try {
            res = mvc.perform( get( "/createStaff" ) ).andExpect( status().isOk() ).andReturn().getResponse()
                    .getContentAsString();
            assertTrue( res.contains( "Create Staff" ) );

            res = mvc.perform( get( "/createStaff.html" ) ).andExpect( status().isOk() ).andReturn().getResponse()
                    .getContentAsString();
            assertTrue( res.contains( "Create Staff" ) );
        }
        catch ( final Exception e ) {
            e.printStackTrace();
            fail( "Failed to make request" );
        }
    }
}
