package edu.ncsu.csc.CoffeeMaker.services;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import edu.ncsu.csc.CoffeeMaker.models.Role;
import edu.ncsu.csc.CoffeeMaker.repositories.RoleRepository;

/**
 * The RoleService is used to handle CRUD operations on the Role model. In
 * addition to all functionality from `Service`, we also have functionality for
 * retrieving a single Role by name.
 *
 * @author Kai Presler-Marshall
 *
 */
@Component
@Transactional
public class RoleService extends Service<Role, Long> {

    /**
     * RoleRepository, to be autowired in by Spring and provide CRUD operations
     * on Role model.
     */
    @Autowired
    private RoleRepository roleRepository;

    @Override
    protected JpaRepository<Role, Long> getRepository () {
        return roleRepository;
    }

    /**
     * Find a role with the provided name
     *
     * @param roleName
     *            Name of the role to find
     * @return found role, null if none
     */
    public Role findByName ( final String roleName ) {
        return roleRepository.findByRoleName( roleName );
    }

}
