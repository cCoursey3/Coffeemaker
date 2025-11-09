package edu.ncsu.csc.CoffeeMaker.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import edu.ncsu.csc.CoffeeMaker.models.User;
import edu.ncsu.csc.CoffeeMaker.services.UserService;

/**
 * A collection of security-related functions
 *
 * @author Deci Horine
 */
public class SecurityUtil {
    /**
     * Check if the current requestee has a certain authority. This guide was
     * used during development:
     * https://www.baeldung.com/get-user-in-spring-security
     *
     * @param authority
     *            the authority to check
     * @return true if that user has the given authority
     */
    public static boolean hasAuthority ( final String authority ) {
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        for ( final GrantedAuthority a : auth.getAuthorities() ) {
            if ( authority.equals( a.getAuthority() ) ) {
                return true;
            }
        }

        return false;
    }

    /**
     * Check the security context for the current user and find the matching
     * user *from the database*. This is important, since the SecurityContext
     * user and the database user are seperate objects that do not sync.
     *
     * @param userService
     *            a UserService for this method to use
     * @return the logged-in user, or null if a user can't be found
     */
    public static User getCurrentUser ( final UserService userService ) {
        final Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if ( principal instanceof User ) {
            final User rqUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return userService.findById( rqUser.getId() );
        }
        // Force unit tests to operate as manager
        else {
            return userService.findByName( "manager" );
        }
    }
}
