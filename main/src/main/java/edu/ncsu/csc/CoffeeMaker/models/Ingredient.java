/**
 *
 */
package edu.ncsu.csc.CoffeeMaker.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.google.gson.Gson;

/**
 * Ingredient class containing the required functionality.
 *
 * @author Lliam Rankins
 */
@Entity
public class Ingredient extends DomainObject {

    /** Id for the ingredient. **/
    @Id
    @GeneratedValue
    private Long   id;

    /** Ingredient field. **/
    private String name;

    /** Amount of the ingredient. **/
    private int    amount;

    /**
     * Constructor for Ingredient with no fields for hybernate.
     */
    public Ingredient () {
        super();
    }

    /**
     * Constructor for ingredient, takes id ingredient and amount.
     *
     * @param name
     *            Ingredient name
     * @param amount
     *            Amount of the ingredient
     */
    public Ingredient ( final String name, final int amount ) {
        super();
        this.name = name;
        this.amount = amount;
    }

    /**
     * getter for id.
     *
     * @return Ingredient's
     */
    @Override
    public Long getId () {
        return id;
    }

    /**
     * setter for id.
     *
     * @param id
     *            Id for the ingredient.
     */
    public void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * gets the name of the ingredient
     *
     * @return the name of the ingredient
     */
    public String getName () {
        return name;
    }

    /**
     * Sets the name of the ingredient
     *
     * @param name
     *            the name to set
     */
    public void setName ( final String name ) {
        this.name = name;
    }

    /**
     * Getter for amount
     *
     * @return Returns the ingredient amount
     */
    public int getAmount () {
        return amount;
    }

    /**
     * Setter for amount
     *
     * @param amount
     *            Ingredient amount
     */
    public void setAmount ( final int amount ) {
        this.amount = amount;
    }

    /**
     * To string method for Ingredient
     *
     * @return String for ingredient
     */
    @Override
    public String toString () {
        return "Ingredient [id=" + id + ", name=" + name + ", amount=" + amount + "]";
    }

    /**
     * creates a json representation of an Ingredient
     *
     * @return the json string of the ingredient
     */
    public String toJson () {
        final Gson gson = new Gson();
        return gson.toJson( this );
    }

}
