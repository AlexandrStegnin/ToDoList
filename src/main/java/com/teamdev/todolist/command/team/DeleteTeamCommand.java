package com.teamdev.todolist.command.team;

import com.teamdev.todolist.command.Command;
import com.teamdev.todolist.entity.Team;
import com.teamdev.todolist.service.TeamService;

/**
 * @author Alexandr Stegnin
 */

public class DeleteTeamCommand implements Command {

    private final Team team;
    private final TeamService teamService;

    public DeleteTeamCommand(final TeamService teamService, final Team team) {
        this.teamService = teamService;
        this.team = team;
    }

    @Override
    public void execute() {
        teamService.delete(team);
    }
}
