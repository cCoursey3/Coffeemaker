/**
 *
 */
package edu.ncsu.csc.CoffeeMaker.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc.CoffeeMaker.models.RecipeToIngredient;
import edu.ncsu.csc.CoffeeMaker.repositories.RecipeToIngredientRepository;

/**
 * Service used for CRUD operations on RecipeToIngredient model
 *
 * @author Deci Horine
 */
@Component
@Transactional
public class RecipeToIngredientService extends Service<RecipeToIngredient, Long> {

    /**
     * Autowired RecipeToIngredientRepository for supporting CRUD operations
     */
    @Autowired
    private RecipeToIngredientRepository recipeToIngredientService;

    @Override
    protected JpaRepository<RecipeToIngredient, Long> getRepository () {
        return recipeToIngredientService;
    }
}
