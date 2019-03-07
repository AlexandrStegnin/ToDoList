package com.teamdev.todolist.controllers;

import com.teamdev.todolist.entities.Role;
import com.teamdev.todolist.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.teamdev.todolist.configurations.support.Constants.*;

/**
 * @author Alexandr Stegnin
 */

@Transactional
@RestController
@RequestMapping(API + API_ROLES)
public class RoleController {

    private final RoleService roleService;

    @Autowired
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    /**
     * Создать роль
     *
     * @param role - роль в формате json
     * @return - Role
     */
    @PostMapping
    public Role
    create(@RequestBody Role role) {
        return roleService.create(role);
    }

    /**
     * Найти роль по id
     *
     * @param roleId - id роли
     * @return - Role
     */
    @GetMapping(value = API_ROLES_ROLE_ID)
    public Role findById(@PathVariable(name = API_ROLE_ID) Long roleId) {
        return roleService.findById(roleId);
    }

    /**
     * Достать все роли
     *
     * @return - Список ролей
     */
    @GetMapping
    public List<Role> findAllRoles() {
        return roleService.findAll();
    }

    /**
     * Изменить роль
     *
     * @param roleId - id роли
     * @param role   - данные роли для изменения в формате json
     * @return - Role
     */
    @PutMapping(value = API_ROLES_ROLE_ID, produces = MediaType.APPLICATION_JSON_VALUE)
    public Role update(@PathVariable(API_ROLE_ID) Long roleId, @RequestBody Role role) {
        role.setId(roleId);
        return roleService.update(role);
    }

    /**
     * Удалить роль по id
     *
     * @param roleId - id роли
     */
    @DeleteMapping(value = API_ROLES_ROLE_ID)
    public void remove(@PathVariable(API_ROLE_ID) Long roleId) {
        roleService.delete(roleId);
    }

}
