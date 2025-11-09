package edu.ncsu.csc.CoffeeMaker.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.Min;

/**
 * Recipe for the coffee maker. Recipe is tied to the database using Hibernate
 * libraries. See RecipeRepository and RecipeService for the other two pieces
 * used for database support.
 *
 * @author Kai Presler-Marshall
 */
@Entity
public class Recipe extends DomainObject {

    /** Recipe id */
    @Id
    @GeneratedValue
    private Long                     id;

    /** Recipe name */
    private String                   name;

    /** Recipe price */
    @Min ( 0 )
    private Integer                  price;

    /** List of ingredients in the recipe. **/
    @OneToMany ( cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true, mappedBy = "recipe" )
    private List<RecipeToIngredient> ingredients;

    /**
     * Creates a default recipe for the coffee maker.
     */
    public Recipe () {
        this.name = "";
        this.ingredients = new ArrayList<RecipeToIngredient>();
    }

    /**
     * Adds the given ingredient to the list of ingredients. IMPORTANT: Do not
     * use this method until both this Recipe and the ingredient you're trying
     * to add are saved. Save this recipe, then add ingredients, then save
     * again.
     *
     * @param ingredient
     *            Ingredient to add.
     * @param count
     *            the count of the ingredient
     */
    public void addIngredient ( final Ingredient ingredient, final int count ) {
        // Prevent adding a negative amount of an ingredient
        if ( count <= 0 ) {
            return;
        }

        final var rti = new RecipeToIngredient( this, ingredient, count );
        ingredients.add( rti );
    }

    /**
     * Returns list of ingredients in recipe as RecipeToIngredient objects
     *
     * @return list of ingredients in recipe as RecipeToIngredient objects
     */
    public List<RecipeToIngredient> getRecipeToIngredients () {
        return ingredients;
    }

    /**
     * Set ingredients list for this instance of a recipe to the given list of
     * recipeToIngredient list.
     *
     * @param ingredients
     *            List of recipeToIngredients for the recipe.
     */
    public void setIngredients ( final List<RecipeToIngredient> ingredients ) {
        this.ingredients = ingredients;
    }

    /**
     * Get the ID of the Recipe
     *
     * @return the ID
     */
    @Override
    public Long getId () {
        return id;
    }

    /**
     * Set the ID of the Recipe (Used by Hibernate)
     *
     * @param id
     *            the ID
     */
    @SuppressWarnings ( "unused" )
    private void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Returns name of the recipe.
     *
     * @return Returns the name.
     */
    public String getName () {
        return name;
    }

    /**
     * Sets the recipe name.
     *
     * @param name
     *            The name to set.
     */
    public void setName ( final String name ) {
        this.name = name;
    }

    /**
     * Returns the price of the recipe.
     *
     * @return Returns the price.
     */
    public Integer getPrice () {
        return price;
    }

    /**
     * Sets the recipe price.
     *
     * @param price
     *            The price to set.
     */
    public void setPrice ( final Integer price ) {
        this.price = price;
    }

    @Override
    public int hashCode () {
        return Objects.hash( id, ingredients, name, price );
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
        final Recipe other = (Recipe) obj;
        return Objects.equals( id, other.id ) && Objects.equals( ingredients, other.ingredients )
                && Objects.equals( name, other.name ) && Objects.equals( price, other.price );
    }

    @Override
    public String toString () {
        return "Recipe [name=" + name + ", price=" + price + "]";
    }

    /**
     * GENERATIVE AI WAS USED
     *
     * GPT 3.5, prompt: "how to write toJson method without libraries in java"
     *
     * AI was used for guidance in writing implementation
     *
     * Creates a json representation of a Recipe.
     *
     * @return json representation of the recipe
     */
    public String toJson () {
        final StringBuilder json = new StringBuilder();
        json.append( "{" );
        json.append( "\"id\": " ).append( id ).append( "," );
        json.append( "\"name\": \"" ).append( name ).append( "\"," );
        json.append( "\"price\": " ).append( price ).append( "," );
        json.append( "\"ingredients\": [" );
        for ( int i = 0; i < ingredients.size(); i++ ) {
            final RecipeToIngredient rti = ingredients.get( i );
            json.append( "{" );
            json.append( "\"ingredient\": {" ).append( "\"name\": \"" ).append( rti.getIngredient().getName() )
                    .append( "\"}," );
            json.append( "\"count\": " ).append( rti.getCount() );
            json.append( "}" );
            if ( i < ingredients.size() - 1 ) {
                json.append( "," );
            }
        }
        json.append( "]" );
        json.append( "}" );
        return json.toString();
    }
}
