package edu.ncsu.csc.CoffeeMaker.controllers;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.security.SecurityUtil;
import edu.ncsu.csc.CoffeeMaker.services.IngredientService;

/**
 * This is the controller that holds the REST endpoints that handle CRUD
 * operations for Ingredients
 *
 * @author Ethan Godwin
 */
@SuppressWarnings ( { "unchecked", "rawtypes" } )
@RestController
public class APIIngredientController extends APIController {

    /**
     * IngredientService object that is autowired in by Spring to allow for
     * manipulating the Ingredient model
     */
    @Autowired
    private IngredientService ingredientService;

    /**
     * REST API method that allows GET access to recipes by name
     *
     * @param name
     *            recipe name
     * @return response to the request
     */
    @GetMapping ( BASE_PATH + "ingredients/{name}" )
    public ResponseEntity getIngredient ( @PathVariable final String name ) {
        final Ingredient ingr = ingredientService.findByName( name );
        if ( ingr == null ) {
            return new ResponseEntity( HttpStatus.NOT_FOUND );
        }
        return new ResponseEntity( ingr.toJson(), HttpStatus.OK );
    }

    /**
     * REST API method that allows POST access to the Ingredients model. Creates
     * a new Ingredient by converting the JSON RequestBody to an Ingredient.
     *
     * @param ingredient
     *            the Ingredient json
     * @param principal
     *            Principle for ingredient
     * @return the response to the request, containing the newly updated
     *         ingredient and a success status code if creation was successful.
     */
    @PostMapping ( BASE_PATH + "/ingredients" )
    public ResponseEntity createIngredient ( @RequestBody final Ingredient ingredient, final Principal principal ) {
        if ( !SecurityUtil.hasAuthority( "ADD_INGREDIENTS_TYPE" ) ) {
            return new ResponseEntity( HttpStatus.FORBIDDEN );
        }

        if ( null != ingredientService.findByName( ingredient.getName() ) ) {
            return new ResponseEntity( errorResponse( "Ingredient " + ingredient.getName() + " already exists" ),
                    HttpStatus.CONFLICT );
        }
        if ( ingredient.getAmount() < 1 ) {
            return new ResponseEntity( errorResponse( "\"Amount\" must be a number greater than zero" ),
                    HttpStatus.BAD_REQUEST );
        }
        if ( ingredient.getName().length() == 0 ) {
            return new ResponseEntity( errorResponse( "Ingredients cannot have an empty name" ),
                    HttpStatus.BAD_REQUEST );
        }
        ingredientService.save( ingredient );
        return new ResponseEntity( ingredient, HttpStatus.OK );

    }

    /**
     * REST API method that allows DELETE access to the Ingredients model.
     * Deletes the ingredient with a matching name if one is found
     *
     * @param name
     *            the name of the ingredient to delete
     * @param principal
     *            principal for ingredient.
     * @return the response to the request
     */
    @DeleteMapping ( BASE_PATH + "/ingredients/{name}" )
    public ResponseEntity deleteIngredient ( @PathVariable final String name, final Principal principal ) {
        if ( !SecurityUtil.hasAuthority( "ADD_INGREDIENTS_TYPE" ) ) {
            return new ResponseEntity( HttpStatus.FORBIDDEN );
        }

        final Ingredient ingredient = ingredientService.findByName( name );
        if ( null == ingredient ) {
            return new ResponseEntity( HttpStatus.NOT_FOUND );
        }
        ingredientService.delete( ingredient );
        return new ResponseEntity( HttpStatus.OK );
    }

    /**
     * REST API method to provide GET access to all ingredients in the system
     *
     * @return JSON representation of all ingredients in the system
     */
    @GetMapping ( BASE_PATH + "/ingredients" )
    public List<Ingredient> getIngredients () {
        return ingredientService.findAll();
    }

    /**
     * REST API method to provide PUT access to all ingredients in the system
     * and allows the ingredient count of a specific ingredient to be updated by
     * using the name PathVariable to find the desired ingredient and the
     * RequestBody integer count to set the ingredient count
     *
     * @param name
     *            the name of the ingredient to update
     * @param count
     *            the integer to set the count to
     * @param principal
     *            principal for ingredient.
     * @return the response to the request
     */
    @PutMapping ( BASE_PATH + "/ingredients/{name}" )
    public ResponseEntity updateCount ( @PathVariable final String name, @RequestBody final int count,
            final Principal principal ) {
        if ( !SecurityUtil.hasAuthority( "ADD_INGREDIENTS" ) ) {
            return new ResponseEntity( HttpStatus.FORBIDDEN );
        }

        if ( count < 0 ) {
            return new ResponseEntity( HttpStatus.BAD_REQUEST );
        }
        final Ingredient ingredient = ingredientService.findByName( name );
        if ( null == ingredient ) {
            return new ResponseEntity( HttpStatus.NOT_FOUND );
        }
        ingredient.setAmount( count );
        ingredientService.save( ingredient );
        return new ResponseEntity( HttpStatus.OK );
    }
}
