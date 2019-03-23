package com.teamdev.todolist.services;

import com.teamdev.todolist.entities.TaskStatus;
import com.teamdev.todolist.repositories.TaskStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Alexandr Stegnin
 */

@Service
public class TaskStatusService {

    private final TaskStatusRepository taskStatusRepository;

    @Autowired
    public TaskStatusService(TaskStatusRepository taskStatusRepository) {
        this.taskStatusRepository = taskStatusRepository;
    }

    public TaskStatus create(TaskStatus taskStatus) {
        return taskStatusRepository.save(taskStatus);
    }

    public TaskStatus findOne(Long taskStatusId) {
        return taskStatusRepository.getOne(taskStatusId);
    }

    public List<TaskStatus> findAll() {
        return taskStatusRepository.findAll();
    }

    public TaskStatus update(TaskStatus taskStatus) {
        return taskStatusRepository.save(taskStatus);
    }

    public void delete(Long taskStatusId) {
        taskStatusRepository.deleteById(taskStatusId);
    }

    public TaskStatus save(TaskStatus taskStatus) {
        return taskStatusRepository.save(taskStatus);
    }

}
