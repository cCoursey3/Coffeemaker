package edu.ncsu.csc.CoffeeMaker.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.CoffeeMaker.models.Orders;
import edu.ncsu.csc.CoffeeMaker.services.OrdersService;
import edu.ncsu.csc.CoffeeMaker.services.UserService;

/**
 * This is the controller that holds the REST endpoints that handle CRUD
 * operations for Orders.
 *
 * Spring will automatically convert all of the ResponseEntity and List results
 * to JSON
 *
 * @author Lliam Rankins
 *
 */
@SuppressWarnings ( { "unchecked", "rawtypes" } )
@RestController
public class APIOrderController extends APIController {

    /**
     * OrderService object, to be autowired in by Spring to allow for
     * manipulating the Order model
     */
    @Autowired
    private OrdersService service;

    /**
     * UserService object used for verifying permissions
     */
    @Autowired
    private UserService   userService;

    /**
     * REST API method to provide GET access to all orders in the system
     *
     * @return JSON representation of all recipes in the system
     */
    @GetMapping ( BASE_PATH + "/orders" )
    public ResponseEntity getOrders () {
        return new ResponseEntity( ordersToJson( service.findAll() ), HttpStatus.OK );
    }

    /**
     * REST API method to provide GET access to a specific order, as indicated
     * by the path variable provided (the id of the Order desired)
     *
     * @param id
     *            order id
     * @return response to the request
     */
    @GetMapping ( BASE_PATH + "/orders/{id}" )
    public ResponseEntity getOrder ( @PathVariable final Long id ) {
        final Orders order = service.findById( id );
        return null == order
                ? new ResponseEntity( errorResponse( "No order found with id " + id ), HttpStatus.NOT_FOUND )
                : new ResponseEntity( order.toJson(), HttpStatus.OK );
    }

    /**
     * REST API method to provide POST access to the Order model. This is used
     * to create a new Order by automatically converting the JSON RequestBody
     * provided to a Order object. Invalid JSON will fail.
     *
     * @param order
     *            The valid Order to be saved.
     * @return ResponseEntity indicating success if the Order could be saved to
     *         the repository, or an error if it could not be
     */
    @PostMapping ( BASE_PATH + "/orders" )
    public ResponseEntity createOrder ( @RequestBody final Orders order ) {
        service.save( order );
        return new ResponseEntity( successResponse( order.getId() + " successfully created" ), HttpStatus.OK );
    }

    /**
     * REST API method to allow deleting a Order from the CoffeeMaker order
     * repository, by making a DELETE request to the API endpoint and indicating
     * the order to delete (as a path variable)
     *
     * @param id
     *            The id of the Order to delete
     * @return Success if the order could be deleted; an error if the order does
     *         not exist
     */
    @DeleteMapping ( BASE_PATH + "/orders/{id}" )
    public ResponseEntity deleteOrder ( @PathVariable final Long id ) {
        final Orders order = service.findById( id );
        if ( null == order ) {
            return new ResponseEntity( errorResponse( "No order found for id " + id ), HttpStatus.NOT_FOUND );
        }
        service.delete( order );

        return new ResponseEntity( successResponse( id + " was deleted successfully" ), HttpStatus.OK );
    }

    /**
     * Edit a order
     *
     * @param id
     *            the id of the order
     * @param newOrder
     *            Order with the new fields that need updating
     *
     * @return a response entity
     */
    @PutMapping ( BASE_PATH + "/orders/{id}" )
    public ResponseEntity editOrder ( @PathVariable final Long id, @RequestBody final Orders newOrder ) {
        if ( newOrder == null ) {
            return new ResponseEntity( errorResponse( "No order given" ), HttpStatus.BAD_REQUEST );
        }

        final Orders oldOrder = service.findById( id );

        if ( oldOrder == null ) {
            return new ResponseEntity( errorResponse( "No order with given Id." ), HttpStatus.NOT_FOUND );
        }

        oldOrder.setStatus( newOrder.getStatus() );

        service.save( oldOrder );

        return new ResponseEntity( successResponse( id + " was updated successfully" ), HttpStatus.OK );
    }

    /**
     * Create a JSON string from a list of orders using the Orders.toJson to
     * method
     *
     * @param orders
     *            the orders to convert
     * @return a json string
     */
    private String ordersToJson ( final List<Orders> orders ) {
        final StringBuilder sb = new StringBuilder( "[ " );
        for ( final Orders o : orders ) {
            sb.append( o.toJson() );
            sb.append( ", " );
        }
        sb.delete( sb.length() - 2, sb.length() );
        sb.append( " ]" );
        return sb.toString();
    }
}
