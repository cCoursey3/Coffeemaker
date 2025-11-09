package edu.ncsu.csc.CoffeeMaker.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Recipe for the coffee maker. Recipe is tied to the database using Hibernate
 * libraries. See RecipeRepository and RecipeService for the other two pieces
 * used for database support.
 *
 * @author Kai Presler-Marshall
 */
@Entity
public class User extends DomainObject implements UserDetails {

    /**
     * Required because UserDetails provides serialization
     */
    private static final long serialVersionUID = 1L;

    /** user id */
    @Id
    @GeneratedValue
    private Long              userId;

    /** User username */
    private String            username;

    /** Recipe price */
    private String            password;

    /** Users Role */
    @ManyToOne
    private Role              role;

    /** List of ingredients in the recipe. **/
    @OneToMany ( cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true, mappedBy = "user" )
    private List<Orders>      userOrders;

    /**
     * Creates a default recipe for the coffee maker.
     */
    public User () {
        this.userOrders = new ArrayList<Orders>();
    }

    /**
     * Creates a default recipe for the coffee maker.
     *
     * @param username
     *            user's username.
     * @param password
     *            user's password.
     */
    public User ( final String username, final String password ) {
        this.username = username;
        this.password = password;
        this.userOrders = new ArrayList<Orders>();
    }

    /**
     * Get the ID of the Recipe
     *
     * @return the ID
     */
    @Override
    public Long getId () {
        return userId;
    }

    /**
     * Set the ID of the Recipe (Used by Hibernate)
     *
     * @param id
     *            the ID
     */
    @SuppressWarnings ( "unused" )
    private void setId ( final Long id ) {
        this.userId = id;
    }

    /**
     * Sets the username.
     *
     * @param username
     *            The username to set.
     */
    public void setUsername ( final String username ) {
        this.username = username;
    }

    /**
     * Returns the username
     *
     * @return String, user's Username
     */
    @Override
    public String getUsername () {
        return this.username;
    }

    /**
     * Sets the password.
     *
     * @param password
     *            The password to set.
     */
    public void setPassword ( final String password ) {
        this.password = password;
    }

    /**
     * Returns the password
     *
     * @return String, user's password
     */
    @Override
    public String getPassword () {
        return this.password;
    }

    @Override
    public String toString () {
        return "" + userId + "," + username + "," + role.getRoleName() + "," + userOrders;
    }

    /**
     * Gets the list of orders associated with the user.
     *
     * @return a list of the user's orders.
     */
    public List<Orders> getOrders () {
        return userOrders;
    }

    /**
     * Sets the user's list of orders to the given list.
     *
     * @param orders
     *            List of orders to set to
     */
    public void setOrders ( final List<Orders> orders ) {
        userOrders = orders;
    }

    /**
     * Returns user's role
     *
     * @return User's role.
     */
    public Role getRole () {
        return role;
    }

    /**
     * Sets user's role
     *
     * @param role
     *            Role to set for user.
     */
    public void setRole ( final Role role ) {
        this.role = role;
    }

    /**
     * Orders coffee for the user with the given recipe.
     *
     * @param recipe
     *            Recipe user is ordering
     * @return Order that the user ordered.
     */
    public Orders orderCoffee ( final Recipe recipe ) {
        final Orders ret = new Orders( recipe, "Preparing" );
        userOrders.add( ret );
        ret.setUser( this );
        return ret;
    }

    /**
     * Force update this user's order list with new order details
     *
     * @param order
     *            the order to update
     */
    public void updateOrder ( final Orders order ) {
        userOrders.removeIf( o -> o.getId() == order.getId() );
        userOrders.add( order );
    }

    @Override
    public int hashCode () {
        return Objects.hash( username, userOrders, password, role, userId );
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
        final User other = (User) obj;
        return Objects.equals( username, other.username ) && Objects.equals( userOrders, other.userOrders )
                && Objects.equals( password, other.password ) && Objects.equals( role, other.role )
                && Objects.equals( userId, other.userId );
    }

    /**
     * creates a json representation of a User
     *
     * @return the json string of the user
     */
    public String toJson () {
        final Gson gson = new GsonBuilder().setExclusionStrategies( new ExclusionStrategy() {

            @Override
            public boolean shouldSkipField ( final FieldAttributes f ) {
                // Source:
                // https://stackoverflow.com/questions/3340485/how-to-solve-circular-reference-in-json-serializer-caused-by-hibernate-bidirecti
                return ( f.getName().equals( "user" ) && f.getDeclaringClass() == Orders.class )
                        || ( f.getName().equals( "ingredients" ) && f.getDeclaringClass() == Recipe.class );
            }

            @Override
            public boolean shouldSkipClass ( final Class< ? > clazz ) {
                return false;
            }

        } ).create();
        return gson.toJson( this );
    }

    @Override
    public Collection<SimpleGrantedAuthority> getAuthorities () {
        return getRole().getAuthorities();
    }

    @Override
    public boolean isAccountNonExpired () {
        return true;
    }

    @Override
    public boolean isAccountNonLocked () {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired () {
        return true;
    }

    @Override
    public boolean isEnabled () {
        return true;
    }

}
