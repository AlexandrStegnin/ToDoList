package com.teamdev.todolist.controller;

import com.teamdev.todolist.entity.Team;
import com.teamdev.todolist.service.TeamService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.teamdev.todolist.configuration.support.Constants.*;

/**
 * @author Alexandr Stegnin
 */

@RestController
@RequestMapping(value = API + API_TEAMS, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
@Api(value = API + API_TEAMS, description = "Operations with teams")
public class TeamController {

    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    /**
     * Создать команду
     *
     * @param team - команда в формате json
     * @return - Team
     */
    @ApiOperation(value = "Create team", response = Team.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    }
    )
    @PostMapping
    public Team create(@RequestBody Team team) {
        return teamService.create(team);
    }

    /**
     * Добавить участника команды
     *
     * @param team - команда (id команды и id нового участника)
     * @return - Team
     */
    @ApiOperation(value = "add member to team", response = Team.class)
    @PutMapping(value = API_TEAMS_ADD_MEMBER)
    public Team addMember(@RequestBody Team team) {
        return teamService.addMember(team);
    }

    /**
     * Удалить участника команды
     *
     * @param team - команда (id команды и id участника для удаления)
     * @return - Team
     */
    @ApiOperation(value = "remove member from team", response = Team.class)
    @PutMapping(value = API_TEAMS_REMOVE_MEMBER)
    public Team removeMember(@RequestBody Team team) {
        return teamService.removeMember(team);
    }

    /**
     * Найти команду по id
     *
     * @param teamId - id команды
     * @return - Team
     */
    @ApiOperation(value = "Get team by id", response = Team.class)
    @GetMapping(value = API_TEAMS_TEAM_ID)
    public Team findById(@PathVariable(API_TEAM_ID) Long teamId) {
        return teamService.findOne(teamId);
    }

    /**
     * Достать все команды
     *
     * @return - список команд
     */
    @ApiOperation(value = "View a list of available teams", response = Team.class, responseContainer = "List")
    @GetMapping
    public List<Team> findAllTeams() {
        return teamService.findAll();
    }

    /**
     * Изменить название команды
     *
     * @param team - команда (id и новое название)
     * @return - Team
     */
    @ApiOperation(value = "Update team title", response = Team.class)
    @PutMapping
    public Team update(@RequestBody Team team) {
        return teamService.changeTeamTitle(team);
    }

    /**
     * Удалить команду по id
     *
     * @param teamId - id команды
     */
    @ApiOperation(value = "Delete team by id")
    @DeleteMapping(value = API_TEAMS_TEAM_ID)
    public void remove(@PathVariable(API_TEAM_ID) Long teamId) {
        teamService.delete(teamId);
    }

}
