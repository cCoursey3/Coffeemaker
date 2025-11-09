package edu.ncsu.csc.CoffeeMaker.unit;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import edu.ncsu.csc.CoffeeMaker.TestConfig;
import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.services.IngredientService;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;
import edu.ncsu.csc.CoffeeMaker.services.RecipeToIngredientService;

@ExtendWith ( SpringExtension.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )
public class IngredientTest {
    @Autowired
    private RecipeService             rService;

    @Autowired
    private IngredientService         iService;

    @Autowired
    private RecipeToIngredientService rtiService;

    @BeforeEach
    public void setup () {
        // Clear out everything
        // Hibernate will throw an exception if you attempt
        // to remove an ingredient that's still referenced
        // by a RecipeToIngredient entry
        rtiService.deleteAll();
        rService.deleteAll();
        iService.deleteAll();
    }

    // Single-ingredient test
    @Test
    @Transactional
    public void testIngredient () {
        assertEquals( 0, iService.count() );

        final Ingredient i1 = new Ingredient();
        i1.setName( "Name" );
        i1.setAmount( 100 );
        iService.save( i1 );

        final Ingredient i1s = iService.findByName( "Name" );
        assertNotNull( i1s );
        assertEquals( "Name", i1s.getName() );
        assertEquals( 100, i1s.getAmount() );
        assertEquals( "Ingredient [id=" + i1s.getId() + ", name=Name, amount=100]", i1s.toString() );
        assertEquals( 1, iService.count() );
    }

    @Test
    @Transactional
    public void testMultipleIngredients () {
        for ( Integer i = 1; i <= 100; i++ ) {
            final Ingredient ing = new Ingredient();
            ing.setName( i.toString() );
            ing.setAmount( i );
            iService.save( ing );
            assertEquals( (long) i, iService.count() );
        }

        for ( Integer i = 1; i <= 100; i++ ) {
            final Ingredient ing = iService.findByName( i.toString() );
            assertEquals( i.toString(), ing.getName() );
            assertEquals( i, ing.getAmount() );
            iService.delete( ing );
            assertEquals( 100 - i, iService.count() );
            assertNull( iService.findByName( i.toString() ) );
        }
    }

    @Test
    @Transactional
    public void testUpdateIngredient () {
        final Ingredient i1 = new Ingredient();
        i1.setName( "Test" );
        i1.setAmount( 100 );
        iService.save( i1 );
        i1.setAmount( 90 );
        iService.save( i1 );
        assertEquals( 90, i1.getAmount() );

        Ingredient i1s = iService.findByName( "Test" );
        assertEquals( 90, i1s.getAmount() );
        i1s.setAmount( 80 );
        iService.save( i1s );

        i1s = iService.findByName( "Test" );
        assertEquals( 80, i1s.getAmount() );
    }
}
