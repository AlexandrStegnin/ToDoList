package com.teamdev.todolist.service;

import com.teamdev.todolist.entity.Tag;
import com.teamdev.todolist.entity.Task;
import com.teamdev.todolist.entity.TaskLog;
import com.teamdev.todolist.entity.User;
import com.teamdev.todolist.repository.TaskLogRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Alexandr Stegnin
 */


@Service
public class TaskLogService {

    private final TaskLogRepository taskLogRepository;

    public TaskLogService(TaskLogRepository taskLogRepository) {
        this.taskLogRepository = taskLogRepository;
    }

    public TaskLog create(TaskLog taskLog) {
        return taskLogRepository.save(taskLog);
    }

    public TaskLog findOne(Long id) {
        return taskLogRepository.getOne(id);
    }

    public TaskLog update(TaskLog taskLog) {
        return taskLogRepository.save(taskLog);
    }

    public void delete(TaskLog taskLog) {
        taskLogRepository.delete(taskLog);
    }

    public void delete(Long taskLogId) {
        taskLogRepository.deleteById(taskLogId);
    }

    public String createComment(Task oldTask, Task newTask) {
        StringBuilder result = new StringBuilder();
        if (!oldTask.getTitle().equalsIgnoreCase(newTask.getTitle())) {
            result.append(String.format("Название задачи изменено c %s на %s. ", newTask.getTitle(), oldTask.getTitle()));
        }
        if (!oldTask.getDescription().equalsIgnoreCase(newTask.getDescription())) {
            result.append(String.format("Описание задачи изменено c %s на %s. ", newTask.getDescription(), oldTask.getDescription()));
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
        if (oldTask.getCreationDate().compareTo(newTask.getCreationDate()) != 0) {
            result.append(String.format("Дата создания была изменена с %s на %s. ", oldTask.getCreationDate().toString(), newTask.getCreationDate().toString()));
        }
        if (oldTask.getExecutionDate().compareTo(newTask.getExecutionDate()) != 0) {
            result.append(String.format("Дата окончания была изменена с %s на %s. ", oldTask.getExecutionDate().toString(), newTask.getExecutionDate().toString()));
        }
        if (!oldTask.getStatus().getTitle().equalsIgnoreCase(newTask.getStatus().getTitle())) {
            result.append(String.format("Статус задачи был изменён с %s на %s. ", oldTask.getStatus().getTitle(), newTask.getStatus().getTitle()));
        }
        if (!Objects.equals(null, oldTask.getComment()) && !Objects.equals(null, newTask.getComment()) &&
                !oldTask.getComment().equalsIgnoreCase(newTask.getComment())) {
            result.append(String.format("Комментарий к задаче был изменён с %s на %s. ", oldTask.getComment(), newTask.getComment()));
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

}
