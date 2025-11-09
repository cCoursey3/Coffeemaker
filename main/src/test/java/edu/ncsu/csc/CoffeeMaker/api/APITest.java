package edu.ncsu.csc.CoffeeMaker.api;

import static org.hamcrest.number.OrderingComparison.greaterThanOrEqualTo;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import edu.ncsu.csc.CoffeeMaker.common.TestUtils;
import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.services.IngredientService;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;

@ExtendWith ( SpringExtension.class )
@SpringBootTest
@AutoConfigureMockMvc

public class APITest {
    /**
     * MockMvc uses Spring's testing framework to handle requests to the REST
     * API
     */
    private MockMvc               mvc;

    @Autowired
    private WebApplicationContext context;

    /**
     * Used to delete all recipes before adding others to ensure a stable
     * testing condition.
     */
    @Autowired
    private RecipeService         service;

    @Autowired
    private IngredientService     ingrService;

    /**
     * Sets up the tests.
     */
    @BeforeEach
    public void setup () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();
        service.deleteAll();
        ingrService.deleteAll();
    }

    @Transactional
    @Test
    @WithMockUser ( authorities = { "MAKE_COFFEE", "ADD_INGREDIENTS", "ADD_INGREDIENTS_TYPE" } )
    public void testRecipes () {
        try {
            ingrService.save( new Ingredient( "Coffee", 0 ) );
            ingrService.save( new Ingredient( "Milk", 0 ) );
            ingrService.save( new Ingredient( "Tea", 0 ) );

            // Make a mocha recipe for testing
            final String recipe = mvc.perform( get( "/api/v1/recipes" ) ).andDo( print() ).andExpect( status().isOk() )
                    .andReturn().getResponse().getContentAsString();
            // System.out.println( recipe );

            /* Figure out if the recipe we want is present */
            if ( !recipe.contains( "Mocha" ) ) {
                final Recipe r = new Recipe();
                r.addIngredient( new Ingredient( "Coffee", 0 ), 1 );
                r.addIngredient( new Ingredient( "Milk", 0 ), 1 );
                r.addIngredient( new Ingredient( "Tea", 0 ), 1 );
                r.setPrice( 10 );
                r.setName( "Mocha" );
                mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON ).content( r.toJson() ) )
                        .andExpect( status().isOk() );
            }

            // Test getRecipes by checking for the newly created Mocha recipe
            final String recipes = mvc.perform( get( "/api/v1/recipes" ) ).andDo( print() ).andExpect( status().isOk() )
                    .andReturn().getResponse().getContentAsString();
            assertTrue( recipes.contains( "Mocha" ) );

            // Test adding ingredients to the inventory
            mvc.perform( put( "/api/v1/ingredients/Coffee" ).contentType( MediaType.APPLICATION_JSON ).content( "3" ) )
                    .andExpect( status().isOk() );
            mvc.perform( put( "/api/v1/ingredients/Milk" ).contentType( MediaType.APPLICATION_JSON ).content( "3" ) )
                    .andExpect( status().isOk() );
            mvc.perform( put( "/api/v1/ingredients/Tea" ).contentType( MediaType.APPLICATION_JSON ).content( "1" ) )
                    .andExpect( status().isOk() );

            // Make sure the inventory contains the added values
            // Citation: These tests were written using information from
            // https://www.baeldung.com/java-junit-hamcrest-guide
            // System.out.println(mvc.perform( get( "/api/v1/ingredients/Coffee"
            // ) ).andReturn().getResponse().getContentAsString() );
            mvc.perform( get( "/api/v1/ingredients/Coffee" ) ).andDo( print() ).andExpect( status().isOk() )
                    .andExpect( jsonPath( "$.amount" ).value( greaterThanOrEqualTo( 3 ) ) );
            mvc.perform( get( "/api/v1/ingredients/Milk" ) ).andDo( print() ).andExpect( status().isOk() )
                    .andExpect( jsonPath( "$.amount" ).value( greaterThanOrEqualTo( 3 ) ) );
            mvc.perform( get( "/api/v1/ingredients/Tea" ) ).andDo( print() ).andExpect( status().isOk() )
                    .andExpect( jsonPath( "$.amount" ).value( greaterThanOrEqualTo( 1 ) ) );

            // Test making a mocha
            mvc.perform( post( "/api/v1/makecoffee/Mocha" ).contentType( MediaType.APPLICATION_JSON )
                    .content( TestUtils.asJsonString( 100 ) ) ).andExpect( status().isOk() )
                    .andExpect( jsonPath( "$.message" ).value( 90 ) );

            // Test an invalid request (not enough money)
            mvc.perform( post( "/api/v1/makecoffee/Mocha" ).contentType( MediaType.APPLICATION_JSON )
                    .content( TestUtils.asJsonString( 2 ) ) ).andExpect( status().is4xxClientError() )
                    .andExpect( jsonPath( "$.message" ).value( "Not enough money paid" ) );
        }
        catch ( final Exception e ) {
            e.printStackTrace();
            fail( "Failed to run tests" );
        }

    }
}
