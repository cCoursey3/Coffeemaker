package edu.ncsu.csc.CoffeeMaker.api;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Assertions;
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

import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.services.IngredientService;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith ( SpringExtension.class )
public class APIRecipeTest {

    /**
     * MockMvc uses Spring's testing framework to handle requests to the REST
     * API
     */
    private MockMvc               mvc;

    @Autowired
    private WebApplicationContext context;

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

    @Test
    @Transactional
    @WithMockUser ( authorities = { "ADD_INGREDIENTS_TYPE" } )
    public void ensureRecipe () throws Exception {
        service.deleteAll();

        ingrService.save( new Ingredient( "Coffee", 3 ) );
        ingrService.save( new Ingredient( "Milk", 4 ) );
        ingrService.save( new Ingredient( "Tea", 5 ) );

        final Recipe r = new Recipe();

        r.addIngredient( ingrService.findByName( "Coffee" ), 1 );
        r.addIngredient( ingrService.findByName( "Milk" ), 1 );
        r.addIngredient( ingrService.findByName( "Tea" ), 1 );

        r.setPrice( 10 );
        r.setName( "Mocha" );

        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON ).content( r.toJson() ) )
                .andExpect( status().isOk() );

    }

    @Test
    @Transactional
    @WithMockUser ( authorities = { "ADD_INGREDIENTS_TYPE" } )
    public void testGetRecipe () {
        // Create testing recipes
        final Recipe r1 = createRecipe( "Coffee", 50, 123, 1, 1 );
        service.save( r1 );
        final Recipe r2 = createRecipe( "Mocha", 50, 456, 1, 1 );
        service.save( r2 );
        final Recipe r3 = createRecipe( "Latte", 60, 789, 2, 2 );
        service.save( r3 );

        String res;
        try {
            // Try getting Coffee
            res = mvc.perform( get( "/api/v1/recipes/Coffee" ) ).andExpect( status().isOk() ).andReturn().getResponse()
                    .getContentAsString();
            assertTrue( res.contains( "123" ) );

            // Try getting a Mocha
            res = mvc.perform( get( "/api/v1/recipes/Mocha" ) ).andExpect( status().isOk() ).andReturn().getResponse()
                    .getContentAsString();
            assertTrue( res.contains( "456" ) );

            // Try getting a Latte
            res = mvc.perform( get( "/api/v1/recipes/Latte" ) ).andExpect( status().isOk() ).andReturn().getResponse()
                    .getContentAsString();
            assertTrue( res.contains( "789" ) );

            // Try getting Tea (doesn't exist)
            res = mvc.perform( get( "/api/v1/recipes/Tea" ) ).andExpect( status().is4xxClientError() ).andReturn()
                    .getResponse().getContentAsString();
            assertTrue( res.contains( "No recipe found with name Tea" ) );
        }
        catch ( final Exception e ) {
            System.out.println( "STACK" );
            e.printStackTrace();
            fail( "Failed to make request" );
        }
    }

    @Test
    @Transactional
    @WithMockUser ( authorities = { "ADD_INGREDIENTS_TYPE" } )
    public void testRecipeAPI () throws Exception {

        ingrService.save( new Ingredient( "Coffee", 3 ) );
        ingrService.save( new Ingredient( "Milk", 4 ) );
        ingrService.save( new Ingredient( "Tea", 5 ) );

        final Recipe recipe = new Recipe();
        recipe.setName( "Delicious Not-Coffee" );
        recipe.addIngredient( new Ingredient( "Coffee", 1 ), 1 );
        recipe.addIngredient( new Ingredient( "Milk", 20 ), 10 );
        recipe.addIngredient( new Ingredient( "Tea", 10 ), 5 );

        recipe.setPrice( 5 );

        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON ).content( recipe.toJson() ) );

        Assertions.assertEquals( 1, (int) service.count() );

    }

    @Test
    @Transactional
    @WithMockUser ( authorities = { "ADD_INGREDIENTS_TYPE" } )
    public void testAddRecipe2 () throws Exception {

        /* Tests a recipe with a duplicate name to make sure it's rejected */

        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );
        final String name = "Coffee";
        final Recipe r1 = createRecipe( name, 50, 3, 1, 1 );

        service.save( r1 );

        final Recipe r2 = createRecipe( name, 50, 3, 1, 1 );
        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON ).content( r2.toJson() ) )
                .andExpect( status().is4xxClientError() );

        Assertions.assertEquals( 1, service.findAll().size(), "There should only one recipe in the CoffeeMaker" );
    }

    @Test
    @Transactional
    @WithMockUser ( authorities = { "ADD_INGREDIENTS_TYPE" } )
    public void testAddRecipe15 () throws Exception {

        /* Tests to make sure that our cap of 3 recipes is enforced */

        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        final Recipe r1 = createRecipe( "Coffee", 50, 3, 1, 1 );
        service.save( r1 );
        final Recipe r2 = createRecipe( "Mocha", 50, 3, 1, 1 );
        service.save( r2 );
        final Recipe r3 = createRecipe( "Latte", 60, 3, 2, 2 );
        service.save( r3 );

        Assertions.assertEquals( 3, service.count(),
                "Creating three recipes should result in three recipes in the database" );

        final Recipe r4 = createRecipe( "Hot Chocolate", 75, 0, 2, 1 );

        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON ).content( r4.toJson() ) )
                .andExpect( status().isInsufficientStorage() );

        Assertions.assertEquals( 3, service.count(), "Creating a fourth recipe should not get saved" );
    }

    @Test
    @Transactional
    @WithMockUser ( authorities = { "ADD_INGREDIENTS_TYPE" } )
    public void testDeleteRecipe () throws Exception {
        // Start by creating three recipes and ensuring they're all in the
        // database
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        final Recipe r1 = createRecipe( "Coffee", 50, 3, 1, 1 );
        service.save( r1 );
        final Recipe r2 = createRecipe( "Mocha", 50, 3, 1, 1 );
        service.save( r2 );
        final Recipe r3 = createRecipe( "Latte", 60, 3, 2, 2 );
        service.save( r3 );

        Assertions.assertEquals( 3, service.count(),
                "Creating three recipes should result in three recipes in the database" );

        // Try to delete Coffee
        String res = mvc.perform( delete( "/api/v1/recipes/Coffee" ) ).andExpect( status().isOk() ).andReturn()
                .getResponse().getContentAsString();
        assertTrue( res.contains( "Coffee was deleted successfully" ) );
        Assertions.assertEquals( 2, service.count(),
                "Removing a recipe from the database should result in the number of recipes decreasing" );

        // Try to delete tea (which doesn't exist)
        res = mvc.perform( delete( "/api/v1/recipes/Tea" ) ).andExpect( status().is4xxClientError() ).andReturn()
                .getResponse().getContentAsString();
        assertTrue( res.contains( "No recipe found for name Tea" ) );
        Assertions.assertEquals( 2, service.count(),
                "Database recipe count decreased when removing nonexistant recipe" );

        // Try to delete Mocha
        res = mvc.perform( delete( "/api/v1/recipes/Mocha" ) ).andExpect( status().isOk() ).andReturn().getResponse()
                .getContentAsString();
        assertTrue( res.contains( "Mocha was deleted successfully" ) );
        Assertions.assertEquals( 1, service.count(),
                "Removing a recipe from the database should result in the number of recipes decreasing" );

        // Try to delete Mocha again
        res = mvc.perform( delete( "/api/v1/recipes/Mocha" ) ).andExpect( status().is4xxClientError() ).andReturn()
                .getResponse().getContentAsString();
        assertTrue( res.contains( "No recipe found for name Mocha" ) );
        Assertions.assertEquals( 1, service.count(),
                "Database recipe count decreased when removing nonexistant recipe" );

        // Try to delete Latte
        res = mvc.perform( delete( "/api/v1/recipes/Latte" ) ).andExpect( status().isOk() ).andReturn().getResponse()
                .getContentAsString();
        assertTrue( res.contains( "Latte was deleted successfully" ) );
        Assertions.assertEquals( 0, service.count(),
                "Removing a recipe from the database should result in the number of recipes decreasing" );
    }

    @Test
    @Transactional
    @WithMockUser ( authorities = { "ADD_INGREDIENTS_TYPE" } )
    public void testRecipeWithIngredients () {
        ingrService.save( new Ingredient( "Coffee", 3 ) );
        ingrService.save( new Ingredient( "Milk", 4 ) );
        ingrService.save( new Ingredient( "Tea", 5 ) );

        final Recipe toPost = createRecipe( "Chai", 3, 0, 2, 5 );
        try {
            mvc.perform(
                    post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON ).content( toPost.toJson() ) )
                    .andExpect( status().isOk() );
        }
        catch ( final Exception e ) {
            e.printStackTrace();
            fail( "Couldn't make request" );
        }

        final Recipe fromDB = service.findByName( "Chai" );
        assertNotNull( fromDB );
        assertEquals( "Chai", fromDB.getName() );
        assertEquals( 3, fromDB.getPrice() );
        assertEquals( 2, fromDB.getRecipeToIngredients().size() );
    }

    @Test
    @Transactional
    @WithMockUser ( authorities = { "ADD_INGREDIENTS_TYPE" } )
    public void testEditRecipe () {
        ingrService.save( new Ingredient( "Coffee", 3 ) );
        ingrService.save( new Ingredient( "Milk", 4 ) );
        ingrService.save( new Ingredient( "Tea", 5 ) );

        final Recipe toPost = createRecipe( "Chai", 10, 5, 5, 5 );
        try {
            mvc.perform(
                    post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON ).content( toPost.toJson() ) )
                    .andExpect( status().isOk() );
        }
        catch ( final Exception e ) {
            e.printStackTrace();
            fail( "Couldn't make request" );
        }

        final Recipe edited = createRecipe( "Chai", 3, 0, 2, 5 );
        try {
            mvc.perform( patch( "/api/v1/recipes/Chai" ).contentType( MediaType.APPLICATION_JSON )
                    .content( edited.toJson() ) ).andExpect( status().isOk() );
        }
        catch ( final Exception e ) {
            e.printStackTrace();
            fail( "Couldn't make request" );
        }

        final Recipe fromDB = service.findByName( "Chai" );
        assertNotNull( fromDB );
        assertEquals( "Chai", fromDB.getName() );
        assertEquals( 3, fromDB.getPrice() );
        assertEquals( 2, fromDB.getRecipeToIngredients().size() );
    }

    private Recipe createRecipe ( final String name, final Integer price, final Integer coffee, final Integer milk,
            final Integer tea ) {
        final Recipe recipe = new Recipe();
        recipe.setName( name );
        recipe.setPrice( price );

        recipe.addIngredient( new Ingredient( "Coffee", 0 ), coffee );
        recipe.addIngredient( new Ingredient( "Milk", 0 ), milk );
        recipe.addIngredient( new Ingredient( "Tea", 0 ), tea );
        return recipe;
    }

}
