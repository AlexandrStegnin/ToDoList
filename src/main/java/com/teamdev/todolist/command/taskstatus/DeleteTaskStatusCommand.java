package com.teamdev.todolist.command.taskstatus;

import com.teamdev.todolist.command.Command;
import com.teamdev.todolist.entity.TaskStatus;
import com.teamdev.todolist.service.TaskStatusService;

/**
 * @author Alexandr Stegnin
 */

public class DeleteTaskStatusCommand implements Command {

    private TaskStatusService taskStatusService;
    private TaskStatus taskStatus;

    public DeleteTaskStatusCommand(TaskStatusService taskStatusService, TaskStatus taskStatus) {
        this.taskStatusService = taskStatusService;
        this.taskStatus = taskStatus;
    }

    @Override
    public void execute() {
        taskStatusService.delete(taskStatus.getId());
    }
}
