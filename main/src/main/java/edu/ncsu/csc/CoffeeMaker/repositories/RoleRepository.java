package edu.ncsu.csc.CoffeeMaker.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.ncsu.csc.CoffeeMaker.models.Role;

/**
 * Repository for role
 */
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Finds a Role object with the provided name. Spring will generate code to
     * make this happen.
     *
     * @param roleName
     *            Name of the role
     * @return Found role, null if none.
     */
    Role findByRoleName ( String roleName );

}
