package edu.ncsu.csc.CoffeeMaker.models;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Embeddable;

/**
 * This class exists to allow for a composite primary key for the
 * RecipeToIngredient table
 *
 * See "Creating Embeddable Type" in
 * https://www.javachinna.com/jpa-hibernate-many-to-many-association-extra-column/
 * for further information.
 */

@Embeddable
public class RecipeToIngredientPK implements Serializable {
    /**
     * UID of RecipeToIngredientPK object
     */
    private static final long serialVersionUID = 1L;
    /**
     * Id of the ingredient
     */
    private Long              ingredientID;
    /**
     * Id of the recipe
     */
    private Long              recipeID;

    /**
     * Constructor for RecipeToIngredientPK
     *
     * @param ingredientID
     *            the id of the ingredient stored by this object
     * @param recipeID
     *            the id of the recipe stored by this object
     */
    public RecipeToIngredientPK ( final Long ingredientID, final Long recipeID ) {
        this.ingredientID = ingredientID;
        this.recipeID = recipeID;
    }

    /**
     * default empty constructor
     */
    public RecipeToIngredientPK () {

    }

    /**
     * Gets the ingredient id
     *
     * @return ingredient id
     */
    public Long getIngredientID () {
        return ingredientID;
    }

    /**
     * Sets the ingredient id
     *
     * @param ingredientID
     *            id to set
     */
    public void setIngredientID ( final Long ingredientID ) {
        this.ingredientID = ingredientID;
    }

    /**
     * Gets the recipe id
     *
     * @return recipe id
     */
    public Long getRecipeID () {
        return recipeID;
    }

    /**
     * Sets the recipe id
     *
     * @param recipeID
     *            id to set
     */
    public void setRecipeID ( final Long recipeID ) {
        this.recipeID = recipeID;
    }

    @Override
    public int hashCode () {
        return Objects.hash( ingredientID, recipeID );
    }

    @Override
    public boolean equals ( final Object obj ) {
        if ( this == obj ) {
            return true;
        }
        if ( obj == null ) {
            return false;
        }
        if ( getClass() != obj.getClass() ) {
            return false;
        }
        final RecipeToIngredientPK other = (RecipeToIngredientPK) obj;
        return Objects.equals( ingredientID, other.ingredientID ) && Objects.equals( recipeID, other.recipeID );
    }

}
