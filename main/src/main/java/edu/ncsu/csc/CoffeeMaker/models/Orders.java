package edu.ncsu.csc.CoffeeMaker.models;

import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Model for a User's order.
 *
 * @author cpcours2
 */

@Entity
public class Orders extends DomainObject {

    /** Order id */
    @Id
    @GeneratedValue
    private Long   id;

    /** Order status */
    private String status;

    /** Recipe ordered */
    @ManyToOne
    private Recipe recipe;

    /** User that placed this order */
    @ManyToOne ( cascade = CascadeType.REFRESH )
    private User   user;

    /** Amount the user paid */
    private float  amountPaid;

    /** Amount of amountChange the user recieved */
    private float  amountChange;

    /**
     * Orders constructor, empty.
     */
    public Orders () {
        super();
    }

    /**
     * Orders constructor with recipe and status fields.
     *
     * @param r
     *            recipe for the order.
     * @param status
     *            Status of the order.
     */
    public Orders ( final Recipe r, final String status ) {
        super();
        this.recipe = r;
        this.status = status;
    }

    /**
     * Get the ID of the order
     *
     * @return the ID
     */
    @Override
    public Long getId () {
        return id;
    }

    /**
     * Set the ID of the order (Used by Hibernate)
     *
     * @param id
     *            the ID
     */
    @SuppressWarnings ( "unused" )
    private void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * retrieve the current recipe in the order
     *
     * @return recipe Order's recipe
     */
    public Recipe getRecipe () {
        return recipe;
    }

    /**
     * Update the recipe in the order
     *
     * @param recipe
     *            New recipe for the order.
     */
    public void setRecipe ( final Recipe recipe ) {
        this.recipe = recipe;
        updateUser();
    }

    /**
     * retrieve the status of an order
     *
     * @return status Status of the order.
     */
    public String getStatus () {
        return status;
    }

    /**
     * Set the status of an order
     *
     * @param status
     *            New status for the order.
     */
    public void setStatus ( final String status ) {
        this.status = status;
        updateUser();
    }

    /**
     * Get this order's user
     *
     * @return the order's user
     */
    public User getUser () {
        return user;
    }

    /**
     * Set this order's user
     *
     * @param user
     *            the user to set
     */
    public void setUser ( final User user ) {
        this.user = user;
        updateUser();
    }

    /**
     * Gets the amount paid.
     *
     * @return amountPaid
     */
    public float getAmountPaid () {
        return amountPaid;
    }

    /**
     * Sets amount paid, and updates User
     *
     * @param amountPaid
     *            amountPaid
     */
    public void setAmountPaid ( final float amountPaid ) {
        this.amountPaid = amountPaid;
        updateUser();
    }

    /**
     * Gets amount of change
     *
     * @return amountChange
     */
    public float getAmountChange () {
        return amountChange;
    }

    /**
     * sets the amount of change
     *
     * @param amountChange
     *            to set
     */
    public void setAmountChange ( final float amountChange ) {
        this.amountChange = amountChange;
        updateUser();
    }

    /**
     * Attempt to update order list of the user that owns this order
     */
    private void updateUser () {
        if ( user != null ) {
            user.updateOrder( this );
        }
    }

    @Override
    public String toString () {
        return "Order: [id= " + id + ",status= " + status + ",recipe=" + recipe.getName() + "]\n" + "Recipe in Order: "
                + recipe.toString();

    }

    @Override
    public int hashCode () {
        return Objects.hash( id, recipe, status );
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
        final Orders other = (Orders) obj;
        return Objects.equals( id, other.id ) && Objects.equals( recipe, other.recipe )
                && Objects.equals( status, other.status );
    }

    /**
     * creates a json representation of an Ingredient
     *
     * @return the json string of the ingredient
     */
    public String toJson () {
        final Gson gson = new GsonBuilder().setExclusionStrategies( new ExclusionStrategy() {

            @Override
            public boolean shouldSkipField ( final FieldAttributes f ) {
                // Source:
                // https://stackoverflow.com/questions/3340485/how-to-solve-circular-reference-in-json-serializer-caused-by-hibernate-bidirecti
                return ( f.getName().equals( "userOrders" ) && f.getDeclaringClass() == User.class )
                        || ( f.getName().equals( "ingredients" ) && f.getDeclaringClass() == Recipe.class );
            }

            @Override
            public boolean shouldSkipClass ( final Class< ? > clazz ) {
                return false;
            }

        } ).create();
        return gson.toJson( this );
    }

}
