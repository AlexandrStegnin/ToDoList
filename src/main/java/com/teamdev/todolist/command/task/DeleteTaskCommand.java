package com.teamdev.todolist.command.task;

import com.teamdev.todolist.command.Command;
import com.teamdev.todolist.entity.Task;
import com.teamdev.todolist.service.TaskService;

/**
 * @author Alexandr Stegnin
 */

public class DeleteTaskCommand implements Command {

    private TaskService taskService;
    private Task task;

    public DeleteTaskCommand(TaskService taskService, Task task) {
        this.taskService = taskService;
        this.task = task;
    }

    @Override
    public void execute() {
        taskService.delete(task.getId());
    }
}
