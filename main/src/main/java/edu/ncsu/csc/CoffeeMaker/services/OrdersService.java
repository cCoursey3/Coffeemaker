package edu.ncsu.csc.CoffeeMaker.services;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import edu.ncsu.csc.CoffeeMaker.models.Orders;
import edu.ncsu.csc.CoffeeMaker.repositories.OrdersRepository;

/**
 * The RecipeService is used to handle CRUD operations on the Recipe model. In
 * addition to all functionality from `Service`, we also have functionality for
 * retrieving a single Recipe by name.
 *
 * @author Kai Presler-Marshall
 *
 */
@Component
@Transactional
public class OrdersService extends Service<Orders, Long> {

    /**
     * RecipeRepository, to be autowired in by Spring and provide CRUD
     * operations on Recipe model.
     */
    @Autowired
    private OrdersRepository orderRepository;

    @Override
    protected JpaRepository<Orders, Long> getRepository () {
        return orderRepository;
    }

    /**
     * Find a order with the provided name
     *
     * @param id
     *            id of the order to find
     * @return found order, null if none
     */
    public Orders findByID ( final int id ) {
        return orderRepository.findById( id );
    }

}
