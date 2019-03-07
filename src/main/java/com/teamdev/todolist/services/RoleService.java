package com.teamdev.todolist.services;

import com.teamdev.todolist.entities.Role;
import com.teamdev.todolist.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Alexandr Stegnin
 */

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Role findById(Long id) {
        return roleRepository.getOne(id);
    }

    public Role findByTitle(String title) {
        return roleRepository.findByTitle(title);
    }

}
