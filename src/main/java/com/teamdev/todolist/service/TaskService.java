package com.teamdev.todolist.service;

import com.teamdev.todolist.configuration.security.SecurityUtils;
import com.teamdev.todolist.configuration.support.OperationEnum;
import com.teamdev.todolist.entity.Task;
import com.teamdev.todolist.entity.User;
import com.teamdev.todolist.entity.Workspace;
import com.teamdev.todolist.repository.TaskRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

/**
 * @author Leonid Lebidko
 */

@Service
@Transactional(readOnly = true)
public class TaskService {

    private TaskRepository taskRepository;
    private TaskLogService taskLogService;
    private UserService userService;

    @Autowired
    public void setTaskRepository(TaskRepository taskRepository, UserService userService, TaskLogService taskLogService) {
        this.taskRepository = taskRepository;
        this.taskLogService = taskLogService;
        this.userService = userService;
    }

    public List<Task> findAll() {
        return taskRepository.findAll();
    }

    public List<Task> findByAuthorLoginAndWorkspaceId(String login, Long workspaceId) {
        return taskRepository.getTasksByAuthorLoginAndWorkspaceId(login, workspaceId);
    }

    public List<Task> findAllByPerformer(User performer, Workspace workspace) {
        return taskRepository.findAllByPerformersAndWorkspace(Collections.singletonList(performer), workspace);
    }

    @Transactional
    public Task create(Task task) {
        Task newTask = taskRepository.save(task);
        taskLogService.createTaskLog(null, newTask, OperationEnum.CREATE, newTask.getAuthor());
        return newTask;
    }

    public Task findOne(Long taskId) {
        Task task = taskRepository.getOne(taskId);
        Hibernate.initialize(task.getWorkspace());
        return task;
    }

    @Transactional
    public Task update(Task task) {
        Task oldTask = taskRepository.getOne(task.getId());
        taskLogService.createTaskLog(oldTask, task, OperationEnum.UPDATE, userService.findByLogin(SecurityUtils.getUsername()));
        return taskRepository.save(task);
    }

    @Transactional
    public void delete(Long taskId) {
        Task newTask = taskRepository.getOne(taskId);
        taskLogService.createTaskLog(null, newTask, OperationEnum.DELETE, newTask.getAuthor());
        taskRepository.deleteById(taskId);
    }

    public Task addPerformer(Long taskId, Long performerId) {
        return updateTask(taskId, performerId, true);
    }

    public Task removePerformer(Long taskId, Long performerId) {
        return updateTask(taskId, performerId, false);
    }

    private Task updateTask(Long taskId, Long performerId, boolean add) {
        Task oldTask = taskRepository.getOne(taskId);
        Task task = taskRepository.getOne(taskId);
        User performer = userService.findOne(performerId);
        if (add) task.addPerformer(performer);
        else task.removePerformer(performer);
        taskLogService.createTaskLog(oldTask, task, OperationEnum.UPDATE, userService.findByLogin(SecurityUtils.getUsername()));
        return update(task);
    }

}
