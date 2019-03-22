package com.teamdev.todolist.controllers;

import com.teamdev.todolist.entities.Role;
import com.teamdev.todolist.services.RoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
@Api(value = API + API_ROLES, description = "Operations with system roles")
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
    @ApiOperation(value = "Create system role", response = Role.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    }
    )
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
    @ApiOperation(value = "Get role by id", response = Role.class)
    @GetMapping(value = API_ROLES_ROLE_ID)
    public Role findById(@PathVariable(name = API_ROLE_ID) Long roleId) {
        return roleService.findById(roleId);
    }

    /**
     * Достать все роли
     *
     * @return - Список ролей
     */
    @ApiOperation(value = "View a list of available roles", response = List.class)
    @GetMapping
    public List<Role> findAllRoles() {
        return roleService.findAll();
    }

    /**
     * Изменить роль
     *
     * @param role   - данные роли для изменения в формате json
     * @return - Role
     */
    @ApiOperation(value = "Update role", response = Role.class)
    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Role update(@RequestBody Role role) {
        return roleService.update(role);
    }

    /**
     * Удалить роль по id
     *
     * @param roleId - id роли
     */
    @ApiOperation(value = "Delete role by id")
    @DeleteMapping(value = API_ROLES_ROLE_ID)
    public void remove(@PathVariable(API_ROLE_ID) Long roleId) {
        roleService.delete(roleId);
    }

}
