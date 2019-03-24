package com.teamdev.todolist.repository;

import com.teamdev.todolist.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author Alexandr Stegnin
 */

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByTitle(String title);

    Optional<Role> findById(Long id);

}
