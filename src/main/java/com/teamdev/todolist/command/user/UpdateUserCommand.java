package com.teamdev.todolist.command.user;

import com.teamdev.todolist.entity.User;
import com.teamdev.todolist.service.UserService;
import com.teamdev.todolist.command.Command;

/**
 * @author Alexandr Stegnin
 */

public class UpdateUserCommand implements Command {

    private User user;
    private UserService userService;

    public UpdateUserCommand(UserService userService, User user) {
        this.userService = userService;
        this.user = user;
    }

    @Override
    public void execute() {
        userService.update(user);
    }
}
