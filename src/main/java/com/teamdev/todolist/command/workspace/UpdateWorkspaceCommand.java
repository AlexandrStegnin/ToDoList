package com.teamdev.todolist.command.workspace;

import com.teamdev.todolist.command.Command;
import com.teamdev.todolist.entity.Workspace;
import com.teamdev.todolist.service.WorkspaceService;

/**
 * @author Alexandr Stegnin
 */

public class UpdateWorkspaceCommand implements Command {

    private final Workspace workspace;
    private final WorkspaceService workspaceService;

    public UpdateWorkspaceCommand(final WorkspaceService workspaceService, final Workspace workspace) {
        this.workspaceService = workspaceService;
        this.workspace = workspace;
    }

    @Override
    public void execute() {
        workspaceService.update(workspace);
    }
}
