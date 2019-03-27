package com.teamdev.todolist.command.role;

import com.teamdev.todolist.entity.Role;
import com.teamdev.todolist.service.RoleService;
import com.teamdev.todolist.command.Command;

/**
 * @author Alexandr Stegnin
 */

public class CreateRoleCommand implements Command {

    private RoleService roleService;
    private Role role;
    private static final String COMMAND_NAME = "Create";

    public CreateRoleCommand(RoleService roleService, Role role) {
        this.roleService = roleService;
        this.role = role;
    }

    @Override
    public String getCommandName() {
        return COMMAND_NAME;
    }

    @Override
    public void execute() {
        roleService.create(role);
    }
}
