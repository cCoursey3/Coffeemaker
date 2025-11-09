package edu.ncsu.csc.CoffeeMaker.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Orders;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.models.RecipeToIngredient;
import edu.ncsu.csc.CoffeeMaker.security.SecurityUtil;
import edu.ncsu.csc.CoffeeMaker.services.IngredientService;
import edu.ncsu.csc.CoffeeMaker.services.OrdersService;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;
import edu.ncsu.csc.CoffeeMaker.services.UserService;

/**
 *
 * The APICoffeeController is responsible for making coffee when a user submits
 * a request to do so.
 *
 * Spring will automatically convert all of the ResponseEntity and List results
 * to JSON
 *
 * @author Kai Presler-Marshall
 *
 */
@SuppressWarnings ( { "unchecked", "rawtypes" } )
@RestController
public class APICoffeeController extends APIController {

    /**
     * InventoryService object, to be autowired in by Spring to allow for
     * manipulating the Inventory model
     */
    @Autowired
    private IngredientService ingrService;

    /** UserService object, used for interactions with userservice. **/
    @Autowired
    private UserService       userService;

    /**
     * RecipeService object, to be autowired in by Spring to allow for
     * manipulating the Recipe model
     */
    @Autowired
    private RecipeService     recipeService;

    /**
     * OrdersService object, to be autowired in by Spring to allow for
     * manipulating the Orders model
     */
    @Autowired
    private OrdersService     ordersService;

    /**
     * REST API method to make coffee by completing a POST request with the ID
     * of the recipe as the path variable and the amount that has been paid as
     * the body of the response
     *
     * @param name
     *            recipe name
     * @param amtPaid
     *            amount paid
     * @return The change the customer is due if successful
     */
    @PostMapping ( BASE_PATH + "/makecoffee/{name}" )
    public ResponseEntity makeCoffee ( @PathVariable final String name, @RequestBody final int amtPaid ) {
        final Recipe recipe = recipeService.findByName( name );
        final var user = SecurityUtil.getCurrentUser( userService );
        if ( recipe == null ) {
            return new ResponseEntity( errorResponse( "No recipe selected" ), HttpStatus.NOT_FOUND );
        }
        else if ( amtPaid < recipe.getPrice() ) {
            return new ResponseEntity( errorResponse( "Not enough money paid" ), HttpStatus.CONFLICT );
        }
        if ( !checkIngredients( recipe ) ) {
            return new ResponseEntity( errorResponse( "Not enough ingredients in inventory" ), HttpStatus.BAD_REQUEST );
        }
        final int change = makeCoffee( recipe, amtPaid );

        final Orders order = user.orderCoffee( recipe );
        order.setAmountPaid( amtPaid );
        order.setAmountChange( change );
        ordersService.save( order );

        return new ResponseEntity<String>( successResponse( String.valueOf( change ) ), HttpStatus.OK );

    }

    /**
     * Ensure that there's enough of each ingredient in the inventory to make a
     * recipe
     *
     * @param toPurchase
     *            the recipe to check
     * @return true if there's enough ingredients to make a recipe
     */
    public boolean checkIngredients ( final Recipe toPurchase ) {
        final List<RecipeToIngredient> ingrList = toPurchase.getRecipeToIngredients();
        for ( final RecipeToIngredient rti : ingrList ) {
            if ( rti.getCount() > rti.getIngredient().getAmount() ) {
                return false;
            }
        }
        return true;
    }

    /**
     * Helper method to make coffee
     *
     * @param toPurchase
     *            recipe that we want to make
     * @param amtPaid
     *            money that the user has given the machine
     * @return change if there was enough money to make the coffee, throws
     *         exceptions if not
     */
    public int makeCoffee ( final Recipe toPurchase, final int amtPaid ) {
        int change = amtPaid;
        final List<RecipeToIngredient> ingrList = toPurchase.getRecipeToIngredients();
        for ( final RecipeToIngredient rti : ingrList ) {
            final Ingredient ingredient = ingrService.findByName( rti.getIngredient().getName() );
            if ( ingredient.getAmount() >= rti.getCount() ) {
                ingredient.setAmount( ingredient.getAmount() - rti.getCount() );
                ingrService.save( ingredient );
            }
            else {
                return change;
            }
        }
        change = amtPaid - toPurchase.getPrice();
        return change;
    }
}
