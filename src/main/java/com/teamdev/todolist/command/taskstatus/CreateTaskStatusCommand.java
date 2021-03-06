package com.teamdev.todolist.command.taskstatus;

import com.teamdev.todolist.command.Command;
import com.teamdev.todolist.entity.TaskStatus;
import com.teamdev.todolist.service.TaskStatusService;

/**
 * @author Alexandr Stegnin
 */

public class CreateTaskStatusCommand implements Command {

    private TaskStatusService taskStatusService;
    private TaskStatus taskStatus;

    public CreateTaskStatusCommand(TaskStatusService taskStatusService, TaskStatus taskStatus) {
        this.taskStatusService = taskStatusService;
        this.taskStatus = taskStatus;
    }

    @Override
    public void execute() {
        taskStatusService.create(taskStatus);
    }
}
