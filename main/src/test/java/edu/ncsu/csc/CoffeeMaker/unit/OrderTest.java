package edu.ncsu.csc.CoffeeMaker.unit;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import edu.ncsu.csc.CoffeeMaker.models.Orders;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.models.User;
import edu.ncsu.csc.CoffeeMaker.services.OrdersService;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;

@ExtendWith ( SpringExtension.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )
class OrdersTest {

    @Autowired
    private RecipeService rService;

    @Autowired
    private OrdersService oService;

    @BeforeEach
    public void setup () {
        // Clear out everything
        // Hibernate will throw an exception if you attempt
        // to remove an ingredient that's still referenced
        // by a RecipeToIngredient entry
        rService.deleteAll();
        oService.deleteAll();

        final Recipe r1 = new Recipe();
        r1.setName( "Coffee" );
        r1.setPrice( 2 );
        rService.save( r1 );
        r1.addIngredient( new Ingredient( "Coffee", 100 ), 2 );
        r1.addIngredient( new Ingredient( "Milk", 100 ), 1 );
        rService.save( r1 );

        final Recipe r2 = new Recipe();
        r2.setName( "Latte" );
        r2.setPrice( 4 );
        rService.save( r2 );
        r2.addIngredient( new Ingredient( "Coffee", 100 ), 2 );
        r2.addIngredient( new Ingredient( "Milk", 100 ), 1 );
        r2.addIngredient( new Ingredient( "Chocolate", 100 ), 3 );
        rService.save( r2 );

    }

    @Test
    @Transactional
    void testOrders () {
        final Recipe coffee = rService.findByName( "Coffee" );
        final Orders o = new Orders( coffee, "Preparing your order" );
        assertEquals( coffee, o.getRecipe() );
        assertEquals( "Preparing your order", o.getStatus() );

        assertEquals( o.toString(), o.toString() );

    }

    @Test
    @Transactional
    void testUpdateOrders () {
        final Orders o = new Orders();
        assertNull( o.getRecipe() );
        assertNull( o.getStatus() );

        o.setRecipe( rService.findByName( "Latte" ) );
        o.setStatus( "Preparing your order" );

        assertNotNull( o.getRecipe() );
        assertNotNull( o.getStatus() );

    }

    @Test
    @Transactional
    void testOrderUser () {
        final Orders o = new Orders();
        assertNull( o.getRecipe() );
        assertNull( o.getStatus() );

        o.setRecipe( rService.findByName( "Latte" ) );
        o.setStatus( "Preparing your order" );
        final User user1 = new User( "Tom", "123" );
        o.setUser( user1 );
        assertEquals( user1, o.getUser() );
        o.setAmountChange( (float) 2.00 );
        assertEquals( (float) 2.00, o.getAmountChange() );

        final Recipe r = o.getRecipe();
        final float price = r.getPrice();
        o.setAmountPaid( price );
        assertEquals( price, o.getAmountPaid() );
    }

    @Test
    @Transactional
    void testEqualsAndHash () {
        final Orders o = new Orders();

        o.setRecipe( rService.findByName( "Latte" ) );
        o.setStatus( "Preparing your order" );
        final Recipe r = o.getRecipe();

        final Orders o2 = new Orders();
        o2.setRecipe( rService.findByName( "Mocha" ) );
        o2.setStatus( "Preparing your order" );

        assertFalse( o2.equals( o ) );

        assertFalse( o2.equals( null ) );
        final Orders o3 = new Orders();
        o3.setRecipe( rService.findByName( "Mocha" ) );
        o3.setStatus( "Preparing your order" );
        assertTrue( o2.equals( o3 ) );
        assertTrue( o2.equals( o2 ) );
        assertFalse( o2.equals( r ) );

        assertFalse( o2.hashCode() == o.hashCode() );
    }

}
