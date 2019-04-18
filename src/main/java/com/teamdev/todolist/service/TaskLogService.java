package com.teamdev.todolist.service;

import com.teamdev.todolist.configuration.support.OperationEnum;
import com.teamdev.todolist.entity.Tag;
import com.teamdev.todolist.entity.Task;
import com.teamdev.todolist.entity.TaskLog;
import com.teamdev.todolist.entity.User;
import com.teamdev.todolist.repository.TaskLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Alexandr Stegnin
 */


@Service
@Transactional(readOnly = true)
public class TaskLogService {

    private final TaskLogRepository taskLogRepository;

    public TaskLogService(TaskLogRepository taskLogRepository) {
        this.taskLogRepository = taskLogRepository;
    }

    @Transactional
    public TaskLog create(TaskLog taskLog) {
        return taskLogRepository.save(taskLog);
    }

    public TaskLog findOne(Long id) {
        return taskLogRepository.getOne(id);
    }

    @Transactional
    public TaskLog update(TaskLog taskLog) {
        return taskLogRepository.save(taskLog);
    }

    @Transactional
    public void delete(TaskLog taskLog) {
        taskLogRepository.delete(taskLog);
    }

    @Transactional
    public void delete(Long taskLogId) {
        taskLogRepository.deleteById(taskLogId);
    }

    public void createTaskLog(Task oldTask, Task newTask, OperationEnum operation, User editor) {
        TaskLog taskLog = new TaskLog();
        taskLog.setOperation(operation);
        taskLog.setEditorId(editor.getId());
        StringBuilder result = new StringBuilder(String.format("[Задача с ID = %d]. [Пользователь = %s]. ", newTask.getId(), editor.getLogin()));
        switch (operation) {
            case UPDATE:
                taskLog.setTaskId(oldTask.getId());
                taskLog.setComment(updateTask(oldTask, newTask, result));
                break;
            case CREATE:
                taskLog.setTaskId(newTask.getId());
                taskLog.setComment(createTask(newTask, result));
                break;
            case DELETE:
                taskLog.setTaskId(newTask.getId());
                taskLog.setComment(deleteTask(newTask, result));
                break;
        }
        create(taskLog);
    }

    private void appendPerformers(Task oldTask, Task newTask, StringBuilder result) {
        List<User> updatedPerformers = newTask.getPerformers()
                .stream()
                .filter(user -> !oldTask.getPerformers().contains(user))
                .collect(Collectors.toList());
        final int[] last = {0};
        updatedPerformers.forEach(user -> {
            last[0]++;
            if (last[0] != updatedPerformers.size()) {
                result.append(String.format("[%s], ", user.getLogin()));
            } else {
                result.append(String.format("[%s]. ", user.getLogin()));
            }
        });
    }

    private void appendTags(Task oldTask, Task newTask, StringBuilder result) {
        List<Tag> updatedTags = newTask.getTags()
                .stream()
                .filter(tag -> !oldTask.getTags().contains(tag))
                .collect(Collectors.toList());
        final int[] last = {0};
        updatedTags.forEach(tag -> {
            last[0]++;
            if (last[0] != updatedTags.size()) {
                result.append(String.format("[%s], ", tag.getTitle()));
            } else {
                result.append(String.format("[%s]. ", tag.getTitle()));
            }
        });
    }

    private String updateTask(Task oldTask, Task newTask, StringBuilder result) {
        if (!oldTask.getTitle().equalsIgnoreCase(newTask.getTitle())) {
            result.append(String.format("Название задачи изменено c %s на %s. ", oldTask.getTitle(), newTask.getTitle()));
        }
        if (!oldTask.getDescription().equalsIgnoreCase(newTask.getDescription())) {
            result.append(String.format("Описание задачи изменено c %s на %s. ", oldTask.getDescription(), newTask.getDescription()));
        }
        if (oldTask.getPerformers().size() != newTask.getPerformers().size()) {
            if (oldTask.getPerformers().size() < newTask.getPerformers().size()) {
                result.append("Были добавлены исполнители: ");
                appendPerformers(oldTask, newTask, result);
            } else {
                result.append("Были удалены исполнители: ");
                appendPerformers(newTask, oldTask, result);
            }
        }
        if (oldTask.getCreationDate().toLocalDate().compareTo(newTask.getCreationDate().toLocalDate()) != 0) {
            result.append(String.format("Дата создания была изменена с %s на %s. ", oldTask.getCreationDate().toString(), newTask.getCreationDate().toString()));
        }
        if (oldTask.getExecutionDate().toLocalDate().compareTo(newTask.getExecutionDate().toLocalDate()) != 0) {
            result.append(String.format("Дата окончания была изменена с %s на %s. ", oldTask.getExecutionDate().toString(), newTask.getExecutionDate().toString()));
        }
        if (!oldTask.getStatus().getTitle().equalsIgnoreCase(newTask.getStatus().getTitle())) {
            result.append(String.format("Статус задачи был изменён с %s на %s. ", oldTask.getStatus().getTitle(), newTask.getStatus().getTitle()));
        }
        if (!Objects.equals(null, oldTask.getComment()) && !Objects.equals(null, newTask.getComment()) &&
                !oldTask.getComment().equalsIgnoreCase(newTask.getComment())) {
            if (oldTask.getComment().trim().isEmpty()) {
                result.append(String.format("Был добавлен комментарий к задаче [%s]. ", newTask.getComment()));
            } else {
                if (newTask.getComment().trim().isEmpty()) {
                    result.append("Был удалён комментарий из задачи. ");
                } else {
                    result.append(String.format("Комментарий к задаче был изменён с %s на %s. ", oldTask.getComment(), newTask.getComment()));
                }
            }
        }
        if (oldTask.getTags().size() != newTask.getTags().size()) {
            if (oldTask.getTags().size() < newTask.getTags().size()) {
                result.append("Были добавлены тэги: ");
                appendTags(oldTask, newTask, result);
            } else {
                result.append("Были удалены тэги: ");
                appendTags(newTask, oldTask, result);
            }
        }
        return result.toString();
    }

    private String createTask(Task newTask, StringBuilder result) {
        result.append(String.format("Создана новая задача [id = %d]. ", newTask.getId()));
        return result.toString();
    }

    private String deleteTask(Task task, StringBuilder result) {
        result.append(String.format("Задача [id = %d] удалена. ", task.getId()));
        return result.toString();
    }

}
