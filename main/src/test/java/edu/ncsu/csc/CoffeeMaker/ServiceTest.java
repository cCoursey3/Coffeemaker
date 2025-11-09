package edu.ncsu.csc.CoffeeMaker;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;

@ExtendWith ( SpringExtension.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )
public class ServiceTest {
    @Autowired
    private RecipeService rService;

    @BeforeEach
    public void setup () {
        rService.deleteAll();
    }

    @Test
    @Transactional
    public void serviceTest () {
        assertEquals( 0, rService.count() );
        assertEquals( 0, rService.findAll().size() );

        final Recipe r1 = new Recipe();
        r1.setName( "Name" );
        r1.setPrice( 1 );
        rService.save( r1 );

        assertEquals( 1, rService.count() );
        assertEquals( 1, rService.findAll().size() );
        assertEquals( "Name", rService.findAll().get( 0 ).getName() );
        assertEquals( r1.getName(), rService.findByName( "Name" ).getName() );
        assertEquals( r1.getName(), rService.findById( r1.getId() ).getName() );

        assertNull( rService.findById( null ) );
        assertNull( rService.findById( (long) -1 ) );

        final Recipe r2 = new Recipe();
        r2.setName( "2" );
        r2.setPrice( 2 );
        final Recipe r3 = new Recipe();
        r3.setName( "3" );
        r3.setPrice( 3 );

        final List<Recipe> rList = new ArrayList<>();
        rList.add( r2 );
        rList.add( r3 );
        rService.saveAll( rList );
        assertEquals( 3, rService.count() );
        rService.delete( r1 );
        assertEquals( 2, rService.count() );
        assertNull( rService.findByName( "Name" ) );
    }
}
