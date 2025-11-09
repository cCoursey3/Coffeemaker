package edu.ncsu.csc.CoffeeMaker.runners;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.ApplicationArguments;

import edu.ncsu.csc.CoffeeMaker.models.Role;
import edu.ncsu.csc.CoffeeMaker.models.User;
import edu.ncsu.csc.CoffeeMaker.services.RoleService;
import edu.ncsu.csc.CoffeeMaker.services.UserService;

@ExtendWith ( MockitoExtension.class )
public class AutofillRolesRunnerTest {

    @Mock
    private RoleService         roleService;

    @Mock
    private UserService         userService;

    @InjectMocks
    private AutofillRolesRunner autofillRolesRunner;

    @Test
    public void testRun_createsRolesAndUsersWhenTheyDoNotExist () throws Exception {
        // Arrange
        when( roleService.findByName( anyString() ) ).thenReturn( null );
        when( userService.findByName( anyString() ) ).thenReturn( null );

        final ApplicationArguments args = mock( ApplicationArguments.class );

        // Act
        autofillRolesRunner.run( args );

        // Assert - Check that roles were created
        verify( roleService, times( 3 ) ).save( any( Role.class ) );

        // Assert - Check that users were created
        verify( userService, times( 3 ) ).save( any( User.class ) );
    }

}
