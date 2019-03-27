package com.teamdev.todolist.command.user;

import com.teamdev.todolist.entity.User;
import com.teamdev.todolist.service.UserService;
import com.teamdev.todolist.command.Command;

/**
 * @author Alexandr Stegnin
 */

public class CreateUserCommand implements Command {

    private User user;
    private UserService userService;

    public CreateUserCommand(UserService userService, User user) {
        this.userService = userService;
        this.user = user;
    }

    @Override
    public void execute() {
        userService.create(user);
    }
}
