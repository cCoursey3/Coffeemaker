package edu.ncsu.csc.CoffeeMaker.models;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

/**
 * A class that allows a many-to-many mapping between Recipe and Ingredient
 * while keeping track of the ingredient count in each recipe.
 *
 * This guide was used when creating this class
 * https://www.javachinna.com/jpa-hibernate-many-to-many-association-extra-column
 */
@Entity
public class RecipeToIngredient extends DomainObject {
    /**
     * id of the RecipeToIngredient object
     */
    @EmbeddedId
    private RecipeToIngredientPK id;

    /**
     * The recipe stored by this object
     */
    @ManyToOne ( fetch = FetchType.EAGER )
    @MapsId ( "recipeID" )
    private Recipe               recipe;
    /**
     * The ingredient stored by this object
     */
    @ManyToOne ( fetch = FetchType.EAGER )
    @MapsId ( "ingredientID" )
    private Ingredient           ingredient;

    /**
     * Default empty RecipeToIngredient constructor
     */
    public RecipeToIngredient () {

    }

    /**
     * The default constructor for a RecipeToIngredient mapping.
     *
     * This constructor also handles creating the composite primary key object.
     * See: https://hellokoding.com/composite-primary-key-in-jpa-and-hibernate/
     *
     * @param recipe
     *            The recipe for this mapping
     * @param ingredient
     *            The ingredient for this mapping
     * @param count
     *            The amount of the given recipe in the given ingredient
     */
    public RecipeToIngredient ( final Recipe recipe, final Ingredient ingredient, final int count ) {
        setRecipe( recipe );
        setIngredient( ingredient );
        setCount( count );
        this.id = new RecipeToIngredientPK( ingredient.getId(), recipe.getId() );
    }

    /**
     * Amount of ingredient used by the recipe
     */
    private int count;

    @Override
    public RecipeToIngredientPK getId () {
        return id;
    }

    /**
     * Sets the id of the RecipeToIngredient object
     *
     * @param id
     *            the id to set
     */
    public void setId ( final RecipeToIngredientPK id ) {
        this.id = id;
    }

    /**
     * Gets the recipe stored by the RecipeToIngredient object
     *
     * @return the recipe
     */
    public Recipe getRecipe () {
        return recipe;
    }

    /**
     * sets the recipe
     *
     * @param recipe
     *            the recipe to set
     */
    public void setRecipe ( final Recipe recipe ) {
        this.recipe = recipe;
    }

    /**
     * Gets the ingredient stored by the RecipeToIngredient object
     *
     * @return the ingredient
     */
    public Ingredient getIngredient () {
        return ingredient;
    }

    /**
     * Sets the ingredient stored by the RecipeToIngredient object
     *
     * @param ingredient
     *            the ingredient to set
     */
    public void setIngredient ( final Ingredient ingredient ) {
        this.ingredient = ingredient;
    }

    /**
     * Returns the amount of the ingredient required by the recipe
     *
     * @return the amount of the ingredient required by the recipe
     */
    public int getCount () {
        return count;
    }

    /**
     * sets the amount of the ingredient required by the recipe
     *
     * @param count
     *            the amount to set
     */
    public void setCount ( final int count ) {
        this.count = count;
    }
}
