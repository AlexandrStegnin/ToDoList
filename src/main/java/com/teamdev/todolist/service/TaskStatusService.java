package com.teamdev.todolist.service;

import com.teamdev.todolist.entity.TaskStatus;
import com.teamdev.todolist.repository.TaskStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Alexandr Stegnin
 */

@Service
@Transactional(readOnly = true)
public class TaskStatusService {

    private static final String DEFAULT_TASK_STATUS = "Новая";
    private final TaskStatusRepository taskStatusRepository;

    @Autowired
    public TaskStatusService(TaskStatusRepository taskStatusRepository) {
        this.taskStatusRepository = taskStatusRepository;
    }

    @Transactional
    public TaskStatus create(TaskStatus taskStatus) {
        return taskStatusRepository.save(taskStatus);
    }

    public TaskStatus findOne(Long taskStatusId) {
        return taskStatusRepository.getOne(taskStatusId);
    }

    public List<TaskStatus> findAll() {
        return taskStatusRepository.findAll();
    }

    @Transactional
    public TaskStatus update(TaskStatus taskStatus) {
        return taskStatusRepository.save(taskStatus);
    }

    @Transactional
    public void delete(Long taskStatusId) {
        taskStatusRepository.deleteById(taskStatusId);
    }

    @Transactional
    public TaskStatus save(TaskStatus taskStatus) {
        return taskStatusRepository.save(taskStatus);
    }

    public TaskStatus getDefaultStatus() {
        return taskStatusRepository.findByTitle(DEFAULT_TASK_STATUS);
    }

    public Long count() {
        return taskStatusRepository.count();
    }
}
