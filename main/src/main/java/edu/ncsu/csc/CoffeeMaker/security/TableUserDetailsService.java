package edu.ncsu.csc.CoffeeMaker.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import edu.ncsu.csc.CoffeeMaker.models.User;
import edu.ncsu.csc.CoffeeMaker.services.UserService;

/**
 * TableUserDetailsService class
 */
public class TableUserDetailsService implements UserDetailsService {
    /**
     * User service for functionality.
     */
    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername ( final String username ) throws UsernameNotFoundException {
        final User user = userService.findByName( username );
        if ( user == null ) {
            throw new UsernameNotFoundException( username + " is not a valid user." );
        }

        return user;
    }

}
