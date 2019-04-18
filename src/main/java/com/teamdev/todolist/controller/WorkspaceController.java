package com.teamdev.todolist.controller;

import com.teamdev.todolist.entity.Workspace;
import com.teamdev.todolist.service.WorkspaceService;
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
@RequestMapping(value = API + API_WORKSPACES, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
@Api(value = API + API_WORKSPACES, description = "Operations with workspaces")
public class WorkspaceController {

    private final WorkspaceService workspaceService;

    public WorkspaceController(WorkspaceService workspaceService) {
        this.workspaceService = workspaceService;
    }

    /**
     * Создать рабочую область (далее РО)
     *
     * @param workspace - РО в формате json
     * @return - Workspace
     */
    @ApiOperation(value = "Create workspace", response = Workspace.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    }
    )
    @PostMapping
    public Workspace create(@RequestBody Workspace workspace) {
        return workspaceService.create(workspace);
    }

    /**
     * Найти РО по id
     *
     * @param workspaceId - id РО
     * @return - Workspace
     */
    @ApiOperation(value = "Get workspace by id", response = Workspace.class)
    @GetMapping(value = API_WORKSPACES_WS_ID)
    public Workspace findById(@PathVariable(API_WORKSPACE_ID) Long workspaceId) {
        return workspaceService.findById(workspaceId);
    }

    /**
     * Достать все РО
     *
     * @return - список РО
     */
    @ApiOperation(value = "View a list of available workspaces", response = Workspace.class, responseContainer = "List")
    @GetMapping
    public List<Workspace> findAllWorkspaces() {
        return workspaceService.findAll();
    }

    /**
     * Изменить название РО
     *
     * @param workspace - РО (id и новое название)
     * @return - Workspace
     */
    @ApiOperation(value = "Update workspace title", response = Workspace.class)
    @PutMapping
    public Workspace update(@RequestBody Workspace workspace) {
        return workspaceService.changeWsTitle(workspace);
    }

    /**
     * Добавить команду в РО
     *
     * @param workspace - РО (id РО и id команды)
     * @return - Workspace
     */
    @ApiOperation(value = "add team to workspace", response = Workspace.class)
    @PutMapping(value = API_WORKSPACES_ADD_TEAM)
    public Workspace addTeam(@RequestBody Workspace workspace) {
        return workspaceService.addTeam(workspace);
    }

    /**
     * Удалить команду в РО
     *
     * @param workspace - РО (id РО)
     * @return - Workspace
     */
    @ApiOperation(value = "remove team from workspace", response = Workspace.class)
    @PutMapping(value = API_WORKSPACES_REMOVE_TEAM)
    public Workspace removeTeam(@RequestBody Workspace workspace) {
        return workspaceService.removeTeam(workspace);
    }

    /**
     * Удалить РО по id
     *
     * @param workspaceId - id РО
     */
    @ApiOperation(value = "Delete workspace by id")
    @DeleteMapping(value = API_WORKSPACES_WS_ID)
    public void remove(@PathVariable(API_WORKSPACE_ID) Long workspaceId) {
        workspaceService.delete(workspaceId);
    }

}
