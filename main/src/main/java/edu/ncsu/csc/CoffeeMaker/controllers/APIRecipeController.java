package edu.ncsu.csc.CoffeeMaker.controllers;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.models.RecipeToIngredient;
import edu.ncsu.csc.CoffeeMaker.security.SecurityUtil;
import edu.ncsu.csc.CoffeeMaker.services.IngredientService;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;

/**
 * This is the controller that holds the REST endpoints that handle CRUD
 * operations for Recipes.
 *
 * Spring will automatically convert all of the ResponseEntity and List results
 * to JSON
 *
 * @author Kai Presler-Marshall
 * @author Michelle Lemons
 *
 */
@SuppressWarnings ( { "unchecked", "rawtypes" } )
@RestController
public class APIRecipeController extends APIController {

    /**
     * RecipeService object, to be autowired in by Spring to allow for
     * manipulating the Recipe model
     */
    @Autowired
    private RecipeService     service;

    /**
     * IngredientService object used for creating new recipes
     */
    @Autowired
    private IngredientService iService;

    /**
     * REST API method to provide GET access to all recipes in the system
     *
     * @return JSON representation of all recipes in the system
     */
    @GetMapping ( BASE_PATH + "/recipes" )
    public ResponseEntity getRecipes () {
        return new ResponseEntity( recipesToJSON( service.findAll() ), HttpStatus.OK );
    }

    /**
     * REST API method to provide GET access to a specific recipe, as indicated
     * by the path variable provided (the name of the recipe desired)
     *
     * @param name
     *            recipe name
     * @return response to the request
     */
    @GetMapping ( BASE_PATH + "/recipes/{name}" )
    public ResponseEntity getRecipe ( @PathVariable final String name ) {
        final Recipe recipe = service.findByName( name );
        return null == recipe
                ? new ResponseEntity( errorResponse( "No recipe found with name " + name ), HttpStatus.NOT_FOUND )
                : new ResponseEntity( recipe.toJson(), HttpStatus.OK );
    }

    /**
     * REST API method to provide POST access to the Recipe model. This is used
     * to create a new Recipe by automatically converting the JSON RequestBody
     * provided to a Recipe object. Invalid JSON will fail.
     *
     * @param recipe
     *            The valid Recipe to be saved.
     * @param principal
     *            principal for recipe
     * @return ResponseEntity indicating success if the Recipe could be saved to
     *         the inventory, or an error if it could not be
     */
    @PostMapping ( BASE_PATH + "/recipes" )
    public ResponseEntity createRecipe ( @RequestBody final Recipe recipe, final Principal principal ) {
        if ( !SecurityUtil.hasAuthority( "ADD_INGREDIENTS_TYPE" ) ) {
            return new ResponseEntity( HttpStatus.FORBIDDEN );
        }

        if ( null != service.findByName( recipe.getName() ) ) {
            return new ResponseEntity( errorResponse( "Recipe with the name " + recipe.getName() + " already exists" ),
                    HttpStatus.CONFLICT );
        }

        if ( recipe.getName().length() == 0 ) {
            return new ResponseEntity( errorResponse( "Recipes must have a name" ), HttpStatus.BAD_REQUEST );
        }

        if ( service.findAll().size() >= 3 ) {
            return new ResponseEntity(
                    errorResponse( "Insufficient space in recipe book for recipe " + recipe.getName() ),
                    HttpStatus.INSUFFICIENT_STORAGE );
        }

        try {
            saveAPIRecipeToDB( recipe );
        }
        catch ( final IllegalArgumentException e ) {
            return new ResponseEntity( errorResponse( "Recipe with unknown ingredient given" ),
                    HttpStatus.BAD_REQUEST );
        }
        return new ResponseEntity( successResponse( recipe.getName() + " successfully created" ), HttpStatus.OK );
    }

    /**
     * REST API method to allow deleting a Recipe from the CoffeeMaker's
     * Inventory, by making a DELETE request to the API endpoint and indicating
     * the recipe to delete (as a path variable)
     *
     * @param name
     *            The name of the Recipe to delete
     * @param principal
     *            principal for recipe
     * @return Success if the recipe could be deleted; an error if the recipe
     *         does not exist
     */
    @DeleteMapping ( BASE_PATH + "/recipes/{name}" )
    public ResponseEntity deleteRecipe ( @PathVariable final String name, final Principal principal ) {
        if ( !SecurityUtil.hasAuthority( "ADD_INGREDIENTS_TYPE" ) ) {
            return new ResponseEntity( HttpStatus.FORBIDDEN );
        }

        final Recipe recipe = service.findByName( name );
        if ( null == recipe ) {
            return new ResponseEntity( errorResponse( "No recipe found for name " + name ), HttpStatus.NOT_FOUND );
        }
        service.delete( recipe );

        return new ResponseEntity( successResponse( name + " was deleted successfully" ), HttpStatus.OK );
    }

    /**
     * Edit a recipe
     *
     * @param name
     *            the name of the recipe to edit
     * @param recipe
     *            the new recipe
     * @param principal
     *            pricipal for recipe
     * @return a response entity
     */
    @PatchMapping ( BASE_PATH + "/recipes/{name}" )
    public ResponseEntity editRecipe ( @PathVariable final String name, @RequestBody final Recipe recipe,
            final Principal principal ) {
        if ( !SecurityUtil.hasAuthority( "ADD_INGREDIENTS_TYPE" ) ) {
            return new ResponseEntity( HttpStatus.FORBIDDEN );
        }

        final Recipe oldRecipe = service.findByName( name );
        if ( oldRecipe == null ) {
            return new ResponseEntity( errorResponse( "No recipe found for name " + name ), HttpStatus.NOT_FOUND );
        }
        service.delete( oldRecipe );
        try {
            saveAPIRecipeToDB( recipe );
        }
        catch ( final IllegalArgumentException e ) {
            service.save( oldRecipe );
            return new ResponseEntity( errorResponse( "Recipe with unknown ingredient given" ),
                    HttpStatus.BAD_REQUEST );
        }
        return new ResponseEntity( successResponse( name + " was updated successfully" ), HttpStatus.OK );
    }

    /**
     * Save a Recipe object from the API to the database. This method handles
     * connecting all ingredient objects in the JSON to the correct ingredients
     * in the database.
     *
     * @param apiRecipe
     *            The recipe object from the API controller
     */
    private void saveAPIRecipeToDB ( final Recipe apiRecipe ) {
        final Recipe newRecipe = new Recipe();
        newRecipe.setName( apiRecipe.getName() );
        newRecipe.setPrice( apiRecipe.getPrice() );
        service.save( newRecipe );
        for ( final RecipeToIngredient rti : apiRecipe.getRecipeToIngredients() ) {
            final Ingredient i = iService.findByName( rti.getIngredient().getName() );
            if ( i == null ) {
                service.delete( newRecipe );
                throw new IllegalArgumentException( "Attempted to create a recipe with the nonexistant ingredient "
                        + rti.getIngredient().getName() );
            }
            newRecipe.addIngredient( i, rti.getCount() );
        }
        service.save( newRecipe );
    }

    /**
     * Convert a list of recipes to JSON with the Recipe.toJson() method
     *
     * @param recipes
     *            the list of recipes to convert
     * @return a JSON string for the recipes
     */
    private String recipesToJSON ( final List<Recipe> recipes ) {
        final StringBuilder sb = new StringBuilder( "[ " );
        for ( final Recipe r : recipes ) {
            sb.append( r.toJson() );
            sb.append( ", " );
        }
        sb.delete( sb.length() - 2, sb.length() );
        sb.append( " ]" );
        return sb.toString();
    }
}
