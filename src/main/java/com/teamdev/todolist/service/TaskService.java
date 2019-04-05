package com.teamdev.todolist.service;

import com.teamdev.todolist.entity.Task;
import com.teamdev.todolist.entity.User;
import com.teamdev.todolist.entity.Workspace;
import com.teamdev.todolist.repository.TaskRepository;
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
    private UserService userService;
    private WorkspaceService workspaceService;

    @Autowired
    public void setTaskRepository(TaskRepository taskRepository, UserService userService, WorkspaceService workspaceService) {
        this.workspaceService = workspaceService;
        this.taskRepository = taskRepository;
        this.userService = userService;
    }

    public List<Task> findAll() {
        return taskRepository.findAll();
    }

    public List<Task> findAllByAuthor(User author) {
        return taskRepository.findAllByAuthor(author);
    }

    public List<Task> findAllByPerformer(User performer) {
        return taskRepository.findAllByPerformers(Collections.singletonList(performer));
    }

    @Transactional
    public Task create(Task task) {
        return taskRepository.save(task);
    }

    public Task findOne(Long taskId) {
        return taskRepository.getOne(taskId);
    }

    @Transactional
    public Task update(Task task) {
        return taskRepository.save(task);
    }

    @Transactional
    public void delete(Long taskId) {
        taskRepository.deleteById(taskId);
    }

    @Transactional
    public Task addPerformer(Long taskId, Long performerId) {
        Task task = taskRepository.getOne(taskId);
        User performer = userService.findOne(performerId);
        task.addPerformer(performer);
        return taskRepository.save(task);
    }

    @Transactional
    public Task removePerformer(Long taskId, Long performerId) {
        Task task = taskRepository.getOne(taskId);
        User performer = userService.findOne(performerId);
        task.removePerformer(performer);
        return taskRepository.save(task);
    }

    public List<Task> findAllByWorkspaceId(Long workspaceId) {
        // todo добавить в поиск пользователя, чтобы не попасть в чужую рабочую область
        Workspace workspace = workspaceService.findById(workspaceId);
        return taskRepository.findByWorkSpace(workspace);
    }
}
