package edu.ncsu.csc.CoffeeMaker.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * Model for the Role of a user, used to control permissions.
 */
@Entity
public class Role extends DomainObject {

    /** Role id */
    @Id
    @GeneratedValue
    private Long    roleID;

    /** Name of the Role **/
    private String  roleName;

    /** If the Role can make coffee **/
    private boolean makeCoffee;

    /** If the Role can add ingredients **/
    private boolean addIngredients;

    /** If the Role can add ingredient type **/
    private boolean addIngredientType;

    /** If the Role can add staff accounts **/
    private boolean addStaffAccount;

    /**
     * Constructor for creating a null role.
     */
    public Role () {
        this.roleName = null;
        this.makeCoffee = false;
        this.addIngredients = false;
        this.addIngredientType = false;
        this.addStaffAccount = false;
    }

    /**
     * Gets role Id.
     *
     * @return the roleID
     */
    public Long getRoleID () {
        return roleID;
    }

    /**
     * Sets RoleID.
     *
     * @param roleID
     *            the roleID to set
     */
    public void setRoleID ( final Long roleID ) {
        this.roleID = roleID;
    }

    /**
     * Returns RoleName.
     *
     * @return the roleName
     */
    public String getRoleName () {
        return roleName;
    }

    /**
     * Sets Rolename.
     *
     * @param roleName
     *            the roleName to set
     */
    public void setRoleName ( final String roleName ) {
        this.roleName = roleName;
    }

    /**
     * Returns if they can Make coffee
     *
     * @return the makeCoffee
     */
    public boolean getMakeCoffee () {
        return makeCoffee;
    }

    /**
     * Sets make coffee boolean
     *
     * @param makeCoffee
     *            the makeCoffee to set
     */
    public void setMakeCoffee ( final boolean makeCoffee ) {
        this.makeCoffee = makeCoffee;
    }

    /**
     * Gets add ingredient bool.
     *
     * @return the addIngredients
     */
    public boolean getAddIngredients () {
        return addIngredients;
    }

    /**
     * Sets if they can add ingredients.
     *
     * @param addIngredients
     *            the addIngredients to set
     */
    public void setAddIngredients ( final boolean addIngredients ) {
        this.addIngredients = addIngredients;
    }

    /**
     * gets the add ingredient type bool.
     *
     * @return the addIngredientType
     */
    public boolean getAddIngredientType () {
        return addIngredientType;
    }

    /**
     * Sets add ingredient type
     *
     * @param addIngredientType
     *            the addIngredientType to set
     */
    public void setAddIngredientType ( final boolean addIngredientType ) {
        this.addIngredientType = addIngredientType;
    }

    /**
     * Gets if they can make staff accounts.
     *
     * @return the addStaffAccount
     */
    public boolean getAddStaffAccount () {
        return addStaffAccount;
    }

    /**
     * Sets addStaffAccount bool.
     *
     * @param addStaffAccount
     *            the addStaffAccount to set
     */
    public void setAddStaffAccount ( final boolean addStaffAccount ) {
        this.addStaffAccount = addStaffAccount;
    }

    /**
     * gets the Array list of SimpleGrantedAuthority for associated permissions.
     * Used by Spring Security
     *
     * @return ArrayList of SimpleGrantedAuthority.
     */
    public ArrayList<SimpleGrantedAuthority> getAuthorities () {
        final ArrayList<SimpleGrantedAuthority> ret = new ArrayList<SimpleGrantedAuthority>();
        // Gives make_coffee authority if the role has it
        if ( getMakeCoffee() ) {
            ret.add( new SimpleGrantedAuthority( "MAKE_COFFEE" ) );
        }

        // Gives add_ingredients authority if the role has it
        if ( getAddIngredients() ) {
            ret.add( new SimpleGrantedAuthority( "ADD_INGREDIENTS" ) );
        }

        // Gives add_ingredient_type authority if the role has it
        if ( getAddIngredientType() ) {
            ret.add( new SimpleGrantedAuthority( "ADD_INGREDIENTS_TYPE" ) );
        }

        // Gives add_staff_account authority if the role has it
        if ( getAddStaffAccount() ) {
            ret.add( new SimpleGrantedAuthority( "ADD_STAFF_ACCOUNT" ) );
        }

        return ret;
    }

    @Override
    public Serializable getId () {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int hashCode () {
        return Objects.hash( addIngredientType, addIngredients, addStaffAccount, makeCoffee, roleID, roleName );
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
        final Role other = (Role) obj;
        return addIngredientType == other.addIngredientType && addIngredients == other.addIngredients
                && addStaffAccount == other.addStaffAccount && makeCoffee == other.makeCoffee
                && Objects.equals( roleID, other.roleID ) && Objects.equals( roleName, other.roleName );
    }

}
