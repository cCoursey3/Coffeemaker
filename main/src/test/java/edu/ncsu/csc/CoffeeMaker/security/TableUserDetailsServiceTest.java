package edu.ncsu.csc.CoffeeMaker.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import edu.ncsu.csc.CoffeeMaker.models.User;
import edu.ncsu.csc.CoffeeMaker.services.UserService;

@ExtendWith ( MockitoExtension.class )
public class TableUserDetailsServiceTest {

    @Mock
    private UserService             userService;

    @InjectMocks
    private TableUserDetailsService userDetailsService;

    @Test
    public void testLoadUserByUsername_UserExists_ReturnsUser () {
        // Arrange
        final String username = "testuser";
        final User mockUser = new User();
        mockUser.setUsername( username );
        when( userService.findByName( username ) ).thenReturn( mockUser );

        // Act
        final UserDetails userDetails = userDetailsService.loadUserByUsername( username );

        // Assert
        assertNotNull( userDetails );
        assertEquals( username, userDetails.getUsername() );
        verify( userService ).findByName( username );
    }
}
