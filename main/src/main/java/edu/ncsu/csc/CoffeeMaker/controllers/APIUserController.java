package edu.ncsu.csc.CoffeeMaker.controllers;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.CoffeeMaker.models.Role;
import edu.ncsu.csc.CoffeeMaker.models.User;
import edu.ncsu.csc.CoffeeMaker.security.SecurityUtil;
import edu.ncsu.csc.CoffeeMaker.services.RoleService;
import edu.ncsu.csc.CoffeeMaker.services.UserService;

/**
 * This is the controller that holds the REST endpoints that handle CRUD
 * operations for Users.
 *
 * @author Cole Sanders
 */
@SuppressWarnings ( { "unchecked", "rawtypes" } )
@RestController
public class APIUserController extends APIController {

    /**
     * IngredientService object that is autowired in by Spring to allow for
     * manipulating the Ingredient model
     */
    @Autowired
    private UserService userService;

    /**
     * Role service to control interactions with roles.
     */
    @Autowired
    private RoleService roleService;

    /**
     * REST API method that allows GET access to all users.
     *
     * @param principal
     *            principal for user
     * @return response to the request.
     */
    @GetMapping ( BASE_PATH + "users" )
    public List<User> getUsers ( final Principal principal ) {
        if ( !SecurityUtil.hasAuthority( "ADD_STAFF_ACCOUNT" ) ) {
            return null;
        }

        return userService.findAll();
    }

    /**
     * REST API method that allows GET access to users by user name.
     *
     * @param name
     *            users user name
     * @param principal
     *            principal for user
     * @return response to the request.
     */
    @GetMapping ( BASE_PATH + "users/{name}" )
    public ResponseEntity getUser ( @PathVariable final String name, final Principal principal ) {
        if ( !SecurityUtil.hasAuthority( "ADD_STAFF_ACCOUNT" ) ) {
            return new ResponseEntity( HttpStatus.FORBIDDEN );
        }

        final User us = userService.findByName( name );
        if ( null == us ) {
            return new ResponseEntity( HttpStatus.NOT_FOUND );
        }
        return new ResponseEntity( us.toJson(), HttpStatus.OK );
    }

    /**
     * Get the currently logged in user. This resource was used during
     * development: https://www.baeldung.com/get-user-in-spring-security
     *
     * @param principal
     *            autofilled by spring
     * @return a response entity with the user or a 404
     */
    @GetMapping ( BASE_PATH + "users/self" )
    public ResponseEntity getSelf ( final Principal principal ) {
        final var u = SecurityUtil.getCurrentUser( userService );
        if ( u == null ) {
            return new ResponseEntity( HttpStatus.NOT_FOUND );
        }
        return new ResponseEntity( u.toJson(), HttpStatus.OK );
    }

    /**
     * REST API method that allows POST access to users. Creates a new user by
     * converting the JSON RequestBody to an user.
     *
     * @param user
     *            the user json
     * @param principal
     *            autofilled by spring
     * @return the response to the request, containing the newly updated user
     *         and a success status code if creation was successful.
     */
    @PostMapping ( BASE_PATH + "/users" )
    public ResponseEntity createUser ( @RequestBody final User user, final Principal principal ) {

        if ( null != userService.findByName( user.getUsername() ) ) {
            return new ResponseEntity( errorResponse( "User " + user.getUsername() + " already exists" ),
                    HttpStatus.CONFLICT );
        }
        if ( user.getUsername().length() == 0 ) {
            return new ResponseEntity( errorResponse( "User cannot have an empty username" ), HttpStatus.BAD_REQUEST );
        }

        // Needed to prevent a mismapping of endpoints for Spring Security
        if ( user.getUsername().equals( "self" ) ) {
            return new ResponseEntity( errorResponse( "User cannot have username of 'self'" ), HttpStatus.BAD_REQUEST );
        }

        if ( user.getPassword().length() == 0 ) {
            return new ResponseEntity( errorResponse( "User cannot have an empty password" ), HttpStatus.BAD_REQUEST );
        }
        if ( user.getRole() == null || !setUserRole( user ) ) {
            return new ResponseEntity( errorResponse( "User must have valid a role" ), HttpStatus.BAD_REQUEST );
        }
        // Only managers are allowed to create accounts with roles above
        // customer
        if ( !SecurityUtil.hasAuthority( "ADD_STAFF_ACCOUNT" ) && !user.getRole().getRoleName().equals( "Customer" ) ) {
            return new ResponseEntity( "You do not have permission to create a staff account", HttpStatus.FORBIDDEN );
        }

        // Hash the user's password
        final String plaintext = user.getPassword();
        final BCryptPasswordEncoder pe = new BCryptPasswordEncoder();
        user.setPassword( pe.encode( plaintext ) );

        userService.save( user );
        return new ResponseEntity( user, HttpStatus.OK );

    }

    /**
     * REST API method that allows DELETE access to the User model. Deletes the
     * user with a matching name if one is found
     *
     * @param name
     *            the user name of the user to delete
     * @param principal
     *            autofilled by spring
     * @return the response to the request
     */
    @DeleteMapping ( BASE_PATH + "/users/{name}" )
    public ResponseEntity deleteUser ( @PathVariable final String name, final Principal principal ) {
        if ( !SecurityUtil.hasAuthority( "ADD_STAFF_ACCOUNT" ) ) {
            return new ResponseEntity( HttpStatus.FORBIDDEN );
        }

        final User user = userService.findByName( name );
        if ( null == user ) {
            return new ResponseEntity( HttpStatus.NOT_FOUND );
        }
        userService.delete( user );
        return new ResponseEntity( HttpStatus.OK );
    }

    /**
     * REST API method to provide PUT access to all users.
     *
     * @param name
     *            the name of the user to update
     * @param user
     *            the JSON of the new user information
     * @param principal
     *            autofilled by spring
     * @return the response to the request
     */
    @PutMapping ( BASE_PATH + "/users/{name}" )
    public ResponseEntity updateUser ( @PathVariable final String name, @RequestBody final User user,
            final Principal principal ) {
        if ( !SecurityUtil.hasAuthority( "ADD_STAFF_ACCOUNT" ) ) {
            final boolean isSameUser = ( (User) ( (UsernamePasswordAuthenticationToken) principal ).getPrincipal() )
                    .getUsername().equals( name );
            if ( !isSameUser ) {
                return new ResponseEntity( HttpStatus.FORBIDDEN );
            }
        }

        final User oldUser = userService.findByName( name );
        if ( null == oldUser ) {
            return new ResponseEntity( HttpStatus.NOT_FOUND );
        }

        oldUser.setUsername( user.getUsername() );
        oldUser.setPassword( user.getPassword() );

        userService.save( user );
        return new ResponseEntity( HttpStatus.OK );
    }

    /**
     * Replace a user's current role object with the role of the same name from
     * the database
     *
     * @param user
     *            the user to edit
     * @return true if a matching role from the database was found
     */
    private boolean setUserRole ( final User user ) {
        final Role role = roleService.findByName( user.getRole().getRoleName() );
        if ( role == null ) {
            return false;
        }

        user.setRole( role );
        return true;
    }
}
