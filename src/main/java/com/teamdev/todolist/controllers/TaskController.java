package com.teamdev.todolist.controllers;

import com.teamdev.todolist.entities.Task;
import com.teamdev.todolist.services.TaskService;
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
@RequestMapping(API + API_TASKS)
@Api(value = API + API_TASKS, description = "Operations with tasks")
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * Создать задачу
     *
     * @param task - задача в формате json
     * @return - Task
     */
    @ApiOperation(value = "Create task", response = Task.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    }
    )
    @PostMapping
    public Task create(@RequestBody Task task) {
        return taskService.create(task);
    }

    /**
     * Найти задачу по id
     *
     * @param taskId - id задачи
     * @return - Task
     */
    @ApiOperation(value = "Get task by id", response = Task.class)
    @GetMapping(value = API_TASKS_TASK_ID)
    public Task findById(@PathVariable(API_TASK_ID) Long taskId) {
        return taskService.findOne(taskId);
    }

    /**
     * Достать все задачи
     *
     * @return - список задач
     */
    @ApiOperation(value = "View a list of available tasks", response = Task.class, responseContainer = "List")
    @GetMapping
    public List<Task> findAllTasks() {
        return taskService.findAll();
    }

    /**
     * Изменить задачу
     *
     * @param task   - данные задачи для изменения в формате json
     * @return - Task
     */
    @ApiOperation(value = "Update task", response = Task.class)
    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Task update(@RequestBody Task task) {
        return taskService.update(task);
    }

    /**
     * Удалить задачу по id
     *
     * @param taskId - id задачи
     */
    @ApiOperation(value = "Delete task by id")
    @DeleteMapping(value = API_TASKS_TASK_ID)
    public void remove(@PathVariable(API_TASK_ID) Long taskId) {
        taskService.delete(taskId);
    }

    /**
     * Добавить исполнителя к задаче
     *
     * @param taskId - id задачи
     * @param performerId - id исполнителя
     * @return - Task
     */
    @ApiOperation(value = "Add performer to task")
    @PutMapping(value = API_TASKS_ADD_PERFORMER)
    public Task addPerformerToTask(@PathVariable(name = API_TASK_ID) Long taskId,
                                   @PathVariable(name = API_TASK_PERFORMER_ID) Long performerId) {
        return taskService.addPerformer(taskId, performerId);
    }

    /**
     * Удалить исполнителя из задачи
     *
     * @param taskId - id задачи
     * @param performerId - id исполнителя
     * @return - Task
     */
    @ApiOperation(value = "Remove performer from task")
    @PutMapping(value = API_TASKS_REMOVE_PERFORMER)
    public Task removePerformerToTask(@PathVariable(name = API_TASK_ID) Long taskId,
                                      @PathVariable(name = API_TASK_PERFORMER_ID) Long performerId) {
        return taskService.removePerformer(taskId, performerId);
    }

}
