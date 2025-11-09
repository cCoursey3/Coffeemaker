package edu.ncsu.csc.CoffeeMaker.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import edu.ncsu.csc.CoffeeMaker.TestConfig;
import edu.ncsu.csc.CoffeeMaker.models.Orders;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.models.Role;
import edu.ncsu.csc.CoffeeMaker.models.User;

@ExtendWith ( SpringExtension.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )
class UserTest {

    @Test
    @Transactional
    void testCreateUser () {
        final Role userRole = new Role();
        userRole.setRoleName( "Customer" );

        final User testUser = new User();

        assertNull( testUser.getId() );
        assertNull( testUser.getRole() );

        assertEquals( 0, testUser.getOrders().size() );

        testUser.setRole( userRole );
        assertNotNull( testUser.getRole() );
        assertEquals( userRole, testUser.getRole() );

        final User diffUser = new User();
        assertFalse( testUser.equals( diffUser ) );
        assertFalse( testUser.equals( null ) );
        assertTrue( testUser.equals( testUser ) );

        diffUser.setRole( userRole );
        assertTrue( testUser.equals( diffUser ) );
        assertEquals( testUser.hashCode(), diffUser.hashCode() );

        diffUser.setOrders( null );
        assertNull( diffUser.getOrders() );

        final Recipe testRecipe = new Recipe();
        assertFalse( diffUser.equals( testRecipe ) );

        testUser.setUsername( "Test" );
        assertEquals( "null,Test,Customer,[]", testUser.toString() );

    }

    @Test
    @Transactional
    void testOrderCoffee () {
        final Role userRole = new Role();
        userRole.setRoleName( "Customer" );

        final Recipe testRecipe = new Recipe();
        testRecipe.setName( "Test" );

        final User testUser = new User();
        testUser.setRole( userRole );

        assertEquals( new Orders( testRecipe, "Preparing" ), testUser.orderCoffee( testRecipe ) );
        assertEquals( 1, testUser.getOrders().size() );
    }
}
