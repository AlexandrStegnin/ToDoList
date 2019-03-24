package com.teamdev.todolist.service;

import com.teamdev.todolist.entity.Task;
import com.teamdev.todolist.entity.User;
import com.teamdev.todolist.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Leonid Lebidko
 */

@Service
public class TaskService {

    private TaskRepository taskRepository;
    private UserService userService;

    @Autowired
    public void setTaskRepository(TaskRepository taskRepository,
                                  UserService userService) {
        this.taskRepository = taskRepository;
        this.userService = userService;
    }

    public List<Task> findAll() {
        return taskRepository.findAll();
    }

    public List<Task> findAllByAuthor(Long userId) {
        return taskRepository.findAllByAuthor(userService.getById(userId));
    }

    public List<Task> findAllByPerformer(final Long userId) {
        // todo: refactoring изменить выборку по всем на нормальный репозиторий
        return findAll().stream()
                .filter(x -> x.getPerformers().stream().anyMatch(performer -> performer.getId() == userId))
                .collect(Collectors.toList());
    }

    public Task create(Task task) {
        return taskRepository.save(task);
    }

    public Task findOne(Long taskId) {
        return taskRepository.getOne(taskId);
    }

    public Task update(Task task) {
        return taskRepository.save(task);
    }

    public void delete(Long taskId) {
        taskRepository.deleteById(taskId);
    }

    public Task addPerformer(Long taskId, Long performerId) {
        Task task = taskRepository.getOne(taskId);
        User performer = userService.findOne(performerId);
        task.addPerformer(performer);
        return taskRepository.save(task);
    }

    public Task removePerformer(Long taskId, Long performerId) {
        Task task = taskRepository.getOne(taskId);
        User performer = userService.findOne(performerId);
        task.removePerformer(performer);
        return taskRepository.save(task);
    }
}
