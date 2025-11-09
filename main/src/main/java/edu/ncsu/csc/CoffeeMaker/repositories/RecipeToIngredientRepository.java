/**
 * Interface for Ingredient Repository.
 */
package edu.ncsu.csc.CoffeeMaker.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.ncsu.csc.CoffeeMaker.models.RecipeToIngredient;

/**
 * Interface for RecipeToIngredientRepository.
 */
public interface RecipeToIngredientRepository extends JpaRepository<RecipeToIngredient, Long> {

}
