package edu.ncsu.csc.CoffeeMaker.runners;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import edu.ncsu.csc.CoffeeMaker.models.Role;
import edu.ncsu.csc.CoffeeMaker.models.User;
import edu.ncsu.csc.CoffeeMaker.services.RoleService;
import edu.ncsu.csc.CoffeeMaker.services.UserService;

/**
 * Ensure the data this application needs to run is present in the database
 */
@Component
public class AutofillRolesRunner implements ApplicationRunner {
    /**
     * UserService object that is autowired in by Spring to allow for
     * manipulating the User model
     */
    @Autowired
    private RoleService roleService;

    /**
     * User service for the autofill roles runner
     */
    @Autowired
    private UserService userService;

    /**
     * Autofill the role table with the default user roles
     */
    @Override
    public void run ( final ApplicationArguments args ) throws Exception {
        // Customer role
        if ( roleService.findByName( "Customer" ) == null ) {
            final Role customerRole = new Role();
            customerRole.setRoleName( "Customer" );
            customerRole.setAddIngredients( false );
            customerRole.setAddIngredientType( false );
            customerRole.setAddStaffAccount( false );
            customerRole.setMakeCoffee( true );
            roleService.save( customerRole );
        }

        // Staff role
        if ( roleService.findByName( "Staff" ) == null ) {
            final Role staffRole = new Role();
            staffRole.setRoleName( "Staff" );
            staffRole.setAddIngredients( true );
            staffRole.setAddIngredientType( true );
            staffRole.setAddStaffAccount( false );
            staffRole.setMakeCoffee( true );
            roleService.save( staffRole );
        }

        // Manager role
        if ( roleService.findByName( "Manager" ) == null ) {
            final Role managerRole = new Role();
            managerRole.setRoleName( "Manager" );
            managerRole.setAddIngredients( true );
            managerRole.setAddIngredientType( true );
            managerRole.setAddStaffAccount( true );
            managerRole.setMakeCoffee( true );
            roleService.save( managerRole );
        }

        // Default manager account
        if ( userService.findByName( "manager" ) == null ) {
            final User manager = new User();
            final BCryptPasswordEncoder pe = new BCryptPasswordEncoder();
            manager.setUsername( "manager" );
            manager.setPassword( pe.encode( "coffee" ) );
            manager.setRole( roleService.findByName( "Manager" ) );
            userService.save( manager );
        }

        // Default customer account
        if ( userService.findByName( "customer" ) == null ) {
            final User customer = new User();
            final BCryptPasswordEncoder pe = new BCryptPasswordEncoder();
            customer.setUsername( "customer" );
            customer.setPassword( pe.encode( "coffee" ) );
            customer.setRole( roleService.findByName( "Customer" ) );
            userService.save( customer );
        }

        // Default staff account
        if ( userService.findByName( "staff" ) == null ) {
            final User staff = new User();
            final BCryptPasswordEncoder pe = new BCryptPasswordEncoder();
            staff.setUsername( "staff" );
            staff.setPassword( pe.encode( "coffee" ) );
            staff.setRole( roleService.findByName( "Staff" ) );
            userService.save( staff );
        }
    }
}
