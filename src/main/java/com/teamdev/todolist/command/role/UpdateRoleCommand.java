package com.teamdev.todolist.command.role;

import com.teamdev.todolist.entity.Role;
import com.teamdev.todolist.service.RoleService;
import com.teamdev.todolist.command.Command;

/**
 * @author Alexandr Stegnin
 */

public class UpdateRoleCommand implements Command {

    private RoleService roleService;
    private Role role;


    public UpdateRoleCommand(RoleService roleService, Role role) {
        this.roleService = roleService;
        this.role = role;
    }

    @Override
    public void execute() {
        roleService.update(role);
    }
}
