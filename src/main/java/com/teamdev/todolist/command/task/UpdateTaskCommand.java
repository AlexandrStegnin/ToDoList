package com.teamdev.todolist.command.task;

import com.teamdev.todolist.command.Command;
import com.teamdev.todolist.entity.Task;
import com.teamdev.todolist.service.TaskService;

/**
 * @author Alexandr Stegnin
 */

public class UpdateTaskCommand implements Command {

    private TaskService taskService;
    private Task task;
    private static final String COMMAND_NAME = "Update";


    public UpdateTaskCommand(TaskService taskService, Task task) {
        this.taskService = taskService;
        this.task = task;
    }

    @Override
    public String getCommandName() {
        return COMMAND_NAME;
    }

    @Override
    public void execute() {
        taskService.update(task);
    }
}
