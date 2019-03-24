package com.teamdev.todolist.service;

import com.teamdev.todolist.entity.Role;
import com.teamdev.todolist.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Alexandr Stegnin
 */

@Service
public class RoleService {

    // TODO: 07.03.2019 Выбрасывать исключения, если роль не найдена

    private final RoleRepository roleRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Role findById(Long id) {
        return roleRepository.findById(id).orElse(null);
    }

    public Role findByTitle(String title) {
        return roleRepository.findByTitle(title);
    }

    public Role create(Role role) {
        return roleRepository.save(role);
    }

    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    public Role update(Role role) {
        return roleRepository.save(role);
    }

    public void delete(Long roleId) {
        roleRepository.deleteById(roleId);
    }
}
