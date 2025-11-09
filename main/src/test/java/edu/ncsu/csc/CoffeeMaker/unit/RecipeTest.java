package edu.ncsu.csc.CoffeeMaker.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import edu.ncsu.csc.CoffeeMaker.TestConfig;
import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.models.RecipeToIngredient;
import edu.ncsu.csc.CoffeeMaker.models.RecipeToIngredientPK;
import edu.ncsu.csc.CoffeeMaker.services.IngredientService;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;
import edu.ncsu.csc.CoffeeMaker.services.RecipeToIngredientService;

@ExtendWith ( SpringExtension.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )
public class RecipeTest {

    @Autowired
    private RecipeService             rService;

    @Autowired
    private IngredientService         iService;

    @Autowired
    private RecipeToIngredientService rtiService;

    @Test
    @Transactional
    public void testAddRecipe () {
        rService.deleteAll();
        iService.deleteAll();
        rtiService.deleteAll();
        final Ingredient i1 = new Ingredient( "Chocolate", 100 );
        final Ingredient i2 = new Ingredient( "Milk", 100 );
        final Ingredient i3 = new Ingredient( "Sugar", 100 );
        final Ingredient i4 = new Ingredient( "Coffee", 100 );
        final Ingredient i5 = new Ingredient( "Tea", 100 );
        iService.save( i1 );
        iService.save( i2 );
        iService.save( i3 );
        iService.save( i4 );
        iService.save( i5 );

        final Ingredient chocolate = iService.findByName( "Chocolate" );
        final Ingredient milk = iService.findByName( "Milk" );
        final Ingredient sugar = iService.findByName( "Sugar" );
        final Ingredient coffee = iService.findByName( "Coffee" );
        final Ingredient tea = iService.findByName( "Tea" );

        final Recipe r1 = new Recipe();
        r1.setName( "Coffee" );
        r1.setPrice( 2 );
        rService.save( r1 );
        r1.addIngredient( coffee, 2 );
        r1.addIngredient( milk, 1 );
        rService.save( r1 );

        final Recipe r1s = rService.findByName( "Coffee" );
        assertNotNull( r1s );
        assertEquals( "Coffee", r1s.getName() );
        assertEquals( 2, (int) r1s.getPrice() );
        assertEquals( 2, r1s.getRecipeToIngredients().size() );
        assertEquals( 2, rtiService.count() );
        for ( final RecipeToIngredient rti : r1s.getRecipeToIngredients() ) {
            if ( rti.getIngredient().getName().equals( "Coffee" ) ) {
                assertEquals( 2, rti.getCount() );
            }
            else if ( rti.getIngredient().getName().equals( "Milk" ) ) {
                assertEquals( 1, rti.getCount() );
            }
            else {
                fail( "Incorrect ingredient in recipe" );
            }
        }

        // Ensure cascading deletes work
        rService.delete( r1 );
        assertEquals( 0, rtiService.count() );

        // Ensure multiple ingredients in a single recipe works as-expected
        final Recipe r2 = new Recipe();
        r2.setName( "Everything" );
        r2.setPrice( 999 );
        rService.save( r2 );
        r2.addIngredient( tea, 1 );
        r2.addIngredient( coffee, 1 );
        r2.addIngredient( sugar, 1 );
        r2.addIngredient( milk, 1 );
        r2.addIngredient( chocolate, 1 );
        rService.save( r2 );
        assertEquals( 5, r2.getRecipeToIngredients().size() );
        assertEquals( "Recipe [name=Everything, price=999]", r2.toString() );

        // Test equals
        assertTrue( r2.equals( r2 ) );
        assertFalse( r2.equals( r1 ) );
        assertFalse( r2.equals( null ) );
        assertNotEquals( r1.hashCode(), r2.hashCode() );
    }

    @Test
    @Transactional
    public void testRecipeToIngredientMappings () {
        final Recipe r1 = new Recipe();
        r1.setName( "Recipe" );
        r1.setPrice( 100 );
        rService.save( r1 );

        final Ingredient i1 = new Ingredient( "Ingredient", 100 );
        iService.save( i1 );

        assertEquals( 0, r1.getRecipeToIngredients().size() );
        r1.addIngredient( i1, 10 );
        rService.save( r1 );

        assertEquals( 1, r1.getRecipeToIngredients().size() );
        assertEquals( r1, r1.getRecipeToIngredients().get( 0 ).getRecipe() );
        assertEquals( i1, r1.getRecipeToIngredients().get( 0 ).getIngredient() );
        assertEquals( r1.getId(), r1.getRecipeToIngredients().get( 0 ).getId().getRecipeID() );
        assertEquals( i1.getId(), r1.getRecipeToIngredients().get( 0 ).getId().getIngredientID() );

        final RecipeToIngredientPK rtipk = new RecipeToIngredientPK( i1.getId(), r1.getId() );
        assertTrue( rtipk.equals( rtipk ) );
        assertTrue( rtipk.equals( r1.getRecipeToIngredients().get( 0 ).getId() ) );
        assertFalse( rtipk.equals( null ) );
        assertEquals( rtipk.hashCode(), r1.getRecipeToIngredients().get( 0 ).getId().hashCode() );
    }

    @Test
    @Transactional
    public void testEquals () {
        final Recipe r1 = new Recipe();
        final Ingredient i1 = new Ingredient( "Ingredient", 100 );
        assertFalse( r1.equals( i1 ) );

        final Recipe r2 = new Recipe();
        assertTrue( r1.equals( r2 ) );
    }
}
