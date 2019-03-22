package com.teamdev.todolist.controllers;

import com.teamdev.todolist.entities.TaskStatus;
import com.teamdev.todolist.services.TaskStatusService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.teamdev.todolist.configurations.support.Constants.*;

/**
 * @author Alexandr Stegnin
 */

@RestController
@RequestMapping(API + API_TASK_STATUSES)
@Api(value = API + API_TASK_STATUSES, description = "Operations with task statuses")
public class TaskStatusController {

    private final TaskStatusService taskStatusService;

    @Autowired
    public TaskStatusController(TaskStatusService taskStatusService) {
        this.taskStatusService = taskStatusService;
    }

    /**
     * Создать статус задачи
     *
     * @param taskStatus - статус в формате json
     * @return - TaskStatus
     */
    @ApiOperation(value = "Create task status", response = TaskStatus.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    }
    )
    @PostMapping
    public TaskStatus create(@RequestBody TaskStatus taskStatus) {
        return taskStatusService.create(taskStatus);
    }

    /**
     * Найти статус задачи по id
     *
     * @param taskStatusId - id статуса задачи
     * @return - TaskStatus
     */
    @ApiOperation(value = "Get task status by id", response = TaskStatus.class)
    @GetMapping(value = API_TASK_STATUSES_STATUS_ID)
    public TaskStatus findById(@PathVariable(API_TASK_STATUS_ID) Long taskStatusId) {
        return taskStatusService.findOne(taskStatusId);
    }

    /**
     * Достать все статусы задач
     *
     * @return - список статусов
     */
    @ApiOperation(value = "View a list of available statuses", response = TaskStatus.class, responseContainer = "List")
    @GetMapping
    public List<TaskStatus> findAllTaskStatuses() {
        return taskStatusService.findAll();
    }

    /**
     * Изменить статус задачи
     *
     * @param taskStatusId - id статуса
     * @param taskStatus   - данные статуса для изменения в формате json
     * @return - TaskStatus
     */
    @ApiOperation(value = "Update task status by id", response = TaskStatus.class)
    @PutMapping(value = API_TASK_STATUSES_STATUS_ID, produces = MediaType.APPLICATION_JSON_VALUE)
    public TaskStatus update(@PathVariable(API_TASK_STATUS_ID) Long taskStatusId, @RequestBody TaskStatus taskStatus) {
        taskStatus.setId(taskStatusId);
        return taskStatusService.update(taskStatus);
    }

    /**
     * Удалить статус задачи по id
     *
     * @param taskStatusId - id статуса
     */
    @ApiOperation(value = "Delete task status by id")
    @DeleteMapping(value = API_TASK_STATUSES_STATUS_ID)
    public void remove(@PathVariable(API_TASK_STATUS_ID) Long taskStatusId) {
        taskStatusService.delete(taskStatusId);
    }

}
