package edu.ncsu.csc.CoffeeMaker.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.ncsu.csc.CoffeeMaker.models.Orders;

/**
 * Repository for Orders
 */
public interface OrdersRepository extends JpaRepository<Orders, Long> {

    /**
     * Finds a Order object with the provided id. Spring will generate code to
     * make this happen.
     *
     * @param id
     *            id of the order
     * @return Found recipe, null if none.
     */
    Orders findById ( int id );

}
