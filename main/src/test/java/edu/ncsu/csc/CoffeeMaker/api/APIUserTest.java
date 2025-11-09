package edu.ncsu.csc.CoffeeMaker.api;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

import edu.ncsu.csc.CoffeeMaker.common.TestUtils;
import edu.ncsu.csc.CoffeeMaker.models.Role;
import edu.ncsu.csc.CoffeeMaker.models.User;
import edu.ncsu.csc.CoffeeMaker.services.UserService;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith ( SpringExtension.class )
public class APIUserTest {

    /**
     * MockMvc uses Spring's testing framework to handle requests to the REST
     * API
     */
    private MockMvc               mvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserService           userService;

    /**
     * Sets up the tests.
     */
    @BeforeEach
    public void setup () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();

        userService.deleteAll();

    }

    @Test
    @Transactional
    @WithMockUser ( authorities = { "MAKE_COFFEE", "ADD_INGREDIENTS", "ADD_INGREDIENTS_TYPE", "ADD_STAFF_ACCOUNT" } )
    public void testGetUserInvalid () {

        userService.deleteAll();

        try {

            final int status = mvc.perform( get( "/api/v1/users/dne" ) ).andExpect( status().isNotFound() ).andReturn()
                    .getResponse().getStatus();

            assertEquals( 404, status );
        }
        catch ( final Exception e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Test
    @Transactional
    @WithMockUser ( authorities = { "MAKE_COFFEE", "ADD_INGREDIENTS", "ADD_INGREDIENTS_TYPE", "ADD_STAFF_ACCOUNT" } )
    public void testGetUser () {

        userService.deleteAll();

        final User user1 = new User( "Tom", "123" );

        final Role role = new Role();

        role.setRoleName( "Customer" );

        user1.setRole( role );

        final String res;

        try {
            int status = mvc
                    .perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                            .content( TestUtils.asJsonString( user1 ) ) )
                    .andExpect( status().isOk() ).andReturn().getResponse().getStatus();

            assertEquals( 200, status );

            status = mvc.perform( get( "/api/v1/users/Tom" ) ).andExpect( status().isOk() ).andReturn().getResponse()
                    .getStatus();

            assertEquals( 200, status );

            res = mvc.perform( get( "/api/v1/users" ) ).andExpect( status().isOk() ).andReturn().getResponse()
                    .getContentAsString();

            assertTrue( res.contains( "Tom" ) );
        }
        catch ( final Exception e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Test
    @Transactional
    @WithMockUser ( authorities = { "MAKE_COFFEE", "ADD_INGREDIENTS", "ADD_INGREDIENTS_TYPE", "ADD_STAFF_ACCOUNT" } )
    public void testPutUser () {

        userService.deleteAll();

        final User user1 = new User( "user", "123" );

        final Role role = new Role();

        role.setRoleName( "Customer" );

        user1.setRole( role );

        final User user2 = new User( "NewUsername", "456" );

        try {

            int status = mvc
                    .perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                            .content( TestUtils.asJsonString( user1 ) ) )
                    .andExpect( status().isOk() ).andReturn().getResponse().getStatus();

            assertEquals( 200, status );

            status = mvc
                    .perform( put( "/api/v1/users/user" ).contentType( MediaType.APPLICATION_JSON )
                            .content( TestUtils.asJsonString( user2 ) ) )
                    .andExpect( status().isOk() ).andReturn().getResponse().getStatus();

            assertEquals( 200, status );

            status = mvc
                    .perform( put( "/api/v1/users/user" ).contentType( MediaType.APPLICATION_JSON )
                            .content( TestUtils.asJsonString( user2 ) ) )
                    .andExpect( status().is4xxClientError() ).andReturn().getResponse().getStatus();

            assertEquals( 404, status );

        }
        catch ( final Exception e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    @Transactional
    @WithMockUser ( authorities = { "MAKE_COFFEE", "ADD_INGREDIENTS", "ADD_INGREDIENTS_TYPE" } )
    public void testPutUserNonManager () {

        userService.deleteAll();

        final User user1 = new User( "user", "123" );

        final Role role = new Role();

        role.setRoleName( "Customer" );

        user1.setRole( role );

        try {

            final int status = mvc
                    .perform( put( "/api/v1/users/dne" ).contentType( MediaType.APPLICATION_JSON )
                            .content( TestUtils.asJsonString( user1 ) ) )
                    .andExpect( status().is4xxClientError() ).andReturn().getResponse().getStatus();
            fail();

        }
        catch ( final Exception e ) {
            // TODO Auto-generated catch block
        }
    }

    @Test
    @Transactional
    @WithMockUser ( authorities = { "MAKE_COFFEE", "ADD_INGREDIENTS", "ADD_INGREDIENTS_TYPE", "ADD_STAFF_ACCOUNT" } )
    public void testPostUserInvalid () {

        final User user2 = new User( "", "456" );
        final User user1 = new User( "self", "455" );
        final User user3 = new User( "NewName", "" );

        final User user4 = new User( "newName", "NewPsswd" );
        user4.setRole( null );

        try {
            final int status = mvc
                    .perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                            .content( user2.toJson() ) )
                    .andExpect( status().is4xxClientError() ).andReturn().getResponse().getStatus();
            assertEquals( 400, status );

            final int error2 = mvc
                    .perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                            .content( user1.toJson() ) )
                    .andExpect( status().is4xxClientError() ).andReturn().getResponse().getStatus();
            assertEquals( 400, error2 );

            final int error3 = mvc
                    .perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                            .content( user3.toJson() ) )
                    .andExpect( status().is4xxClientError() ).andReturn().getResponse().getStatus();
            assertEquals( 400, error3 );

            final int error4 = mvc
                    .perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                            .content( user4.toJson() ) )
                    .andExpect( status().is4xxClientError() ).andReturn().getResponse().getStatus();
            assertEquals( 400, error4 );

        }
        catch ( final Exception e ) {

        }

    }

    @Test
    @Transactional
    @WithMockUser ( authorities = { "MAKE_COFFEE", "ADD_INGREDIENTS", "ADD_INGREDIENTS_TYPE", "ADD_STAFF_ACCOUNT" } )
    public void testPostUser () {

        userService.deleteAll();

        Assertions.assertEquals( 0, userService.findAll().size(), "There should be no Users in the CoffeeMaker" );

        final User user1 = new User();

        userService.save( user1 );

        final User user2 = new User();

        try {
            mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON ).content( user2.toJson() ) )
                    .andExpect( status().is4xxClientError() );
        }
        catch ( final Exception e ) {
            System.out.println( "STACK" );
            e.printStackTrace();
            fail( "Failed to make request" );
        }

        Assertions.assertEquals( 1, userService.findAll().size(), "There should only one user in the CoffeeMaker" );
    }

    @Test
    @Transactional
    @WithMockUser ( authorities = { "MAKE_COFFEE", "ADD_INGREDIENTS", "ADD_INGREDIENTS_TYPE", "ADD_STAFF_ACCOUNT" } )
    public void testDeleteUser () {

        userService.deleteAll();

        final User user1 = new User( "Tom", "123" );

        userService.save( user1 );

        final User user2 = new User( "Ben", "321" );

        userService.save( user2 );

        Assertions.assertEquals( 2, userService.count() );

        try {
            mvc.perform( delete( "/api/v1/users/Tom" ) ).andExpect( status().isOk() ).andReturn().getResponse()
                    .getContentAsString();
        }
        catch ( final Exception e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            fail();
        }

        Assertions.assertEquals( 1, userService.count() );

        try {
            mvc.perform( delete( "/api/v1/users/Ben" ) ).andExpect( status().isOk() ).andReturn().getResponse()
                    .getContentAsString();
        }
        catch ( final Exception e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            fail();
        }

        Assertions.assertEquals( 0, userService.count() );

        try {
            mvc.perform( delete( "/api/v1/users/Tom" ) ).andExpect( status().is4xxClientError() ).andReturn()
                    .getResponse().getContentAsString();
        }
        catch ( final Exception e ) {

        }
        Assertions.assertEquals( 0, userService.count() );

    }

    @Test
    @Transactional
    @WithMockUser ( authorities = { "MAKE_COFFEE", "ADD_INGREDIENTS", "ADD_INGREDIENTS_TYPE" } )
    public void testGetSelf () {

        // Test getting the user method with no active user.
        try {
            mvc.perform( get( "/api/v1/users/self" ) ).andExpect( status().isNotFound() ).andReturn().getResponse()
                    .getStatus();
        }
        catch ( final Exception e ) {
            e.printStackTrace();
        }

    }

    @Test
    @Transactional
    @WithMockUser ( authorities = { "MAKE_COFFEE", "ADD_INGREDIENTS" } )
    public void testInvalidAuthorities () {

        userService.deleteAll();

        final User user1 = new User( "Tom", "123" );

        userService.save( user1 );

        final User user2 = new User( "Ben", "321" );

        userService.save( user2 );

        Assertions.assertEquals( 2, userService.count() );

        String res;

        try {
            res = mvc.perform( get( "/api/v1/users" ) ).andExpect( status().isOk() ).andReturn().getResponse()
                    .getContentAsString();

            assertEquals( res, "" );

            mvc.perform( get( "/api/v1/users/Tom" ) ).andExpect( status().isForbidden() );

            mvc.perform( get( "/api/v1/users/Ben" ) ).andExpect( status().isForbidden() );

            mvc.perform( delete( "/api/v1/users/Tom" ) ).andExpect( status().isForbidden() );

            mvc.perform( delete( "/api/v1/users/Ben" ) ).andExpect( status().isForbidden() );

        }
        catch ( final Exception e ) {

        }

    }

}
