package edu.ncsu.csc.CoffeeMaker.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * WebSecurityConfig java class, used for implementing web security.
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    /**
     * API base for web security.
     */
    private static final String API_BASE = "/api/v1/";

    @Override
    protected void configure ( final HttpSecurity http ) throws Exception {
        http.authorizeRequests()
                // Restrict the add recipe page
                .antMatchers( "/addrecipe*" ).hasAuthority( "ADD_INGREDIENTS_TYPE" )
                // Restrict the delete recipe page
                .antMatchers( "/deleterecipe*" ).hasAuthority( "ADD_INGREDIENTS_TYPE" )
                // Restrict the edit recipe page
                .antMatchers( "/editrecipe*" ).hasAuthority( "ADD_INGREDIENTS_TYPE" )
                // Restrict the inventory page
                .antMatchers( "/inventory*" ).hasAuthority( "ADD_INGREDIENTS" )
                // Restrict the make coffee page
                .antMatchers( "/makecoffee*" ).hasAuthority( "MAKE_COFFEE" )
                // Restrict the add ingredient page
                .antMatchers( "/addingredient*" ).hasAuthority( "ADD_INGREDIENTS_TYPE" )
                // Allow access to the user post method from an unauthenticated
                // users
                .antMatchers( HttpMethod.POST, API_BASE + "users" ).permitAll()
                // Default to pages being inaccessible to unauthenticated users
                .anyRequest().authenticated()
                // Authenticate using Http basic
                .and().httpBasic()
                // Use '/home' as the login page
                .and().formLogin().loginPage( "/home" ).permitAll()
                // Disable CSRF, this application is not set up for it
                .and().csrf().disable();
        http.logout( ( logout ) -> logout.logoutSuccessHandler( new HttpStatusReturningLogoutSuccessHandler() )
                .logoutRequestMatcher( new AntPathRequestMatcher( "/logout" ) ) );
    }

    @Override
    @Bean
    public UserDetailsService userDetailsService () {
        return new TableUserDetailsService();
    }

    /**
     * PasswordEncoder for coffeemaker.
     *
     * @return PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder () {
        return new BCryptPasswordEncoder();
    }

}
