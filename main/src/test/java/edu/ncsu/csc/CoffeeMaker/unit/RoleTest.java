package edu.ncsu.csc.CoffeeMaker.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import edu.ncsu.csc.CoffeeMaker.TestConfig;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.models.Role;
import edu.ncsu.csc.CoffeeMaker.services.RoleService;
import edu.ncsu.csc.CoffeeMaker.services.UserService;

/**
 * Tests the RoleTest object
 *
 * @author Nathan Tucker (nctucker)
 */
@ExtendWith ( SpringExtension.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )
public class RoleTest {

    @Autowired
    private RoleService rService;

    @Autowired
    private UserService uService;

    @BeforeEach
    public void setup () {
        uService.deleteAll();
        rService.deleteAll();
    }

    /**
     * Tests the creation of a role as well as the getters and setters
     */
    @Test
    @Transactional
    public void testRole () {

        final Role r1 = new Role();

        r1.setAddIngredients( true );
        r1.setAddIngredientType( true );
        r1.setAddStaffAccount( true );
        r1.setMakeCoffee( true );
        r1.setRoleName( "Manager" );

        assertNotNull( r1 );
        assertTrue( r1.getAddIngredients() );
        assertTrue( r1.getAddIngredientType() );
        assertTrue( r1.getAddStaffAccount() );
        assertTrue( r1.getMakeCoffee() );
        assertEquals( "Manager", r1.getRoleName() );

        assertEquals( r1.getAuthorities().get( 0 ).getAuthority(), "MAKE_COFFEE" );

        r1.setAddIngredients( false );
        r1.setAddIngredientType( false );
        r1.setAddStaffAccount( false );
        r1.setMakeCoffee( false );
        r1.setRoleName( "Customer" );

        assertNotNull( r1 );
        assertFalse( r1.getAddIngredients() );
        assertFalse( r1.getAddIngredientType() );
        assertFalse( r1.getAddStaffAccount() );
        assertFalse( r1.getMakeCoffee() );
        assertEquals( "Customer", r1.getRoleName() );

    }

    /**
     * Tests the saving of roles to the role service
     */
    @Test
    @Transactional
    public void testRoleService () {

        assertEquals( 0, rService.count() );

        final Role r1 = new Role();

        r1.setAddIngredients( true );
        r1.setAddIngredientType( true );
        r1.setAddStaffAccount( true );
        r1.setMakeCoffee( true );
        r1.setRoleName( "Manager" );

        rService.save( r1 );
        assertEquals( 1, rService.count() );

        final Role r1Check = rService.findAll().get( 0 );

        assertNotNull( r1Check );
        assertTrue( r1Check.getAddIngredients() );
        assertTrue( r1Check.getAddIngredientType() );
        assertTrue( r1Check.getAddStaffAccount() );
        assertTrue( r1Check.getMakeCoffee() );
        assertEquals( "Manager", r1Check.getRoleName() );

        final Role r1Check2 = rService.findById( r1Check.getRoleID() );

        assertNotNull( r1Check2 );
        assertTrue( r1Check2.getAddIngredients() );
        assertTrue( r1Check2.getAddIngredientType() );
        assertTrue( r1Check2.getAddStaffAccount() );
        assertTrue( r1Check2.getMakeCoffee() );
        assertEquals( "Manager", r1Check2.getRoleName() );

    }

    /**
     * Tests the equals method for role
     */
    @SuppressWarnings ( "unlikely-arg-type" )
    @Test
    @Transactional
    public void testEquals () {

        final Role r1 = new Role();

        r1.setAddIngredients( true );
        r1.setAddIngredientType( true );
        r1.setAddStaffAccount( true );
        r1.setMakeCoffee( true );
        r1.setRoleName( "Manager" );

        rService.save( r1 );

        assertEquals( 1, rService.count() );

        final Role r1Check = rService.findAll().get( 0 );

        assertEquals( r1, r1Check );

        final Role r2 = new Role();

        r2.setAddIngredients( false );
        r2.setAddIngredientType( true );
        r2.setAddStaffAccount( true );
        r2.setMakeCoffee( true );
        r2.setRoleName( "Manager" );

        assertFalse( r1.equals( r2 ) );

        final Recipe testRecipe = new Recipe();
        assertFalse( r1.equals( testRecipe ) );

        final Role r3 = new Role();

        r3.setAddIngredients( true );
        r3.setAddIngredientType( true );
        r3.setAddStaffAccount( true );
        r3.setMakeCoffee( true );
        r3.setRoleName( "Manager" );

        final Role r4 = new Role();

        r4.setAddIngredients( true );
        r4.setAddIngredientType( true );
        r4.setAddStaffAccount( true );
        r4.setMakeCoffee( true );
        r4.setRoleName( "Manager" );

        assertTrue( r4.equals( r3 ) );

    }

    /**
     * Tests the role id of the role
     */
    @Test
    @Transactional
    public void testRoleID () {

        final Role r1 = new Role();
        r1.setRoleID( Long.parseLong( "1" ) );
        assertEquals( Long.parseLong( "1" ), r1.getRoleID().longValue() );

    }
}
