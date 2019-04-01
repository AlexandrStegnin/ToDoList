package com.teamdev.todolist.vaadin.form;

import com.teamdev.todolist.command.Command;
import com.teamdev.todolist.command.task.CreateTaskCommand;
import com.teamdev.todolist.command.task.DeleteTaskCommand;
import com.teamdev.todolist.command.task.UpdateTaskCommand;
import com.teamdev.todolist.configuration.security.SecurityUtils;
import com.teamdev.todolist.configuration.support.OperationEnum;
import com.teamdev.todolist.entity.Task;
import com.teamdev.todolist.entity.TaskStatus;
import com.teamdev.todolist.entity.User;
import com.teamdev.todolist.service.TaskService;
import com.teamdev.todolist.service.TaskStatusService;
import com.teamdev.todolist.service.UserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import de.wathoserver.vaadin.MultiselectComboBox;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;


/**
 * @author Alexandr Stegnin
 */

public class TaskForm extends VerticalLayout {
    // todo после добавления задачи и попытке открыть форму обновления/удаления не подтягиваются исполнители
    // todo multiSelectCombobox, не отображается сообщение о не пройденной валидации
    private UserService userService;
    private TaskStatusService taskStatusService;
    private TaskService taskService;
    private Task task;
    private OperationEnum operation;

    private TextField title;
    private TextField description;
    private Select<User> author;
    private MultiselectComboBox<User> performers;
    private DatePicker creationDate;
    private DatePicker expirationDate;
    private Select<TaskStatus> status;
    private Dialog dialog;

    private Binder<Task> taskBinder;
    private Button cancel;
    private Button submit;

    public TaskForm(UserService userService, TaskService taskService,
                    TaskStatusService taskStatusService, OperationEnum operation,
                    Task task, Dialog dialog) {
        this.userService = userService;
        this.taskService = taskService;
        this.taskStatusService = taskStatusService;
        this.taskBinder = new BeanValidationBinder<>(Task.class);
        this.operation = operation;
        this.task = task;
        this.dialog = dialog;
        this.submit = new Button();
        init();
    }

    private void init() {
        setMinWidth("300px");
        setMaxWidth("400px");
        title = new TextField("Название");
        description = new TextField("Описание");
        author = new Select<>();
        author.setItems(getAllUsers());
        author.setTextRenderer(User::getLogin);
        author.setReadOnly(true);

        status = new Select<>();
        status.setItems(getAllTaskStatuses());
        status.setTextRenderer(TaskStatus::getTitle);
        status.setEmptySelectionAllowed(false);
        status.setValue(taskStatusService.getDefaultStatus());
        status.setRequiredIndicatorVisible(true);

        cancel = new Button("Отменить");
        performers = new MultiselectComboBox<>(this::getUserName);
        performers.setItems(getAllPerformers());
        performers.setRequired(true);
        performers.setRequiredIndicatorVisible(true);
        creationDate = new DatePicker("Дата создания");
        expirationDate = new DatePicker("Дата окончания");
        addCreationDateValueChangeListener();
        addExpirationDateValueChangeListener();
        add(title, description, creationDate, expirationDate, performers, status);

        prepareForm(task);
        prepareSubmitButton();
    }

    private void addCreationDateValueChangeListener() {
        creationDate.addValueChangeListener(event -> {
            LocalDate selectedDate = event.getValue();
            if (selectedDate != null) {
                expirationDate.setMin(selectedDate.plusDays(1));
            } else {
                expirationDate.setMin(null);
            }
        });
    }

    private void addExpirationDateValueChangeListener() {
        expirationDate.addValueChangeListener(event -> {
            LocalDate selectedDate = event.getValue();
            if (selectedDate != null) {
                creationDate.setMax(selectedDate.minusDays(1));
            } else {
                creationDate.setMax(null);
            }
        });
    }

    private List<TaskStatus> getAllTaskStatuses() {
        return taskStatusService.findAll();
    }

    private Set<User> getAllPerformers() {
        return new HashSet<>(userService.findAll());
    }

    private String getUserName(User user) {
        return user.getProfile().getName() + " " + user.getProfile().getSurname();
    }

    private void prepareForm(Task task) {
        this.task = task;
        User currentUser = userService.findByLogin(SecurityUtils.getUsername());
        this.taskBinder.setBean(task);
        if (Objects.equals(null, task.getAuthor())) {
            task.setAuthor(currentUser);
            author.setValue(currentUser);
        }
        taskBinder.forField(author)
                .bind(Task::getAuthor, Task::setAuthor);

        taskBinder.forField(performers)
                .withValidator(users -> !Objects.equals(null, users) && !users.isEmpty(), "Add 1 or more performers")
                .bind(Task::getPerformers, Task::setPerformers);

        taskBinder.forField(creationDate)
                .withConverter(localDate -> LocalDateTime.of(localDate, LocalTime.now()), LocalDateTime::toLocalDate)
                .bind(Task::getCreationDate, Task::setCreationDate);

        taskBinder.forField(expirationDate)
                .withConverter(localDate -> LocalDateTime.of(localDate, LocalTime.now()), LocalDateTime::toLocalDate)
                .bind(Task::getExecutionDate, Task::setExecutionDate);

        taskBinder.forField(status)
                .withValidator(stat -> !status.isEmpty(), "Choose task status")
                .bind(Task::getStatus, Task::setStatus);

        taskBinder.bindInstanceFields(this);

        HorizontalLayout buttons = new HorizontalLayout();

        cancel.addClickListener(e -> {
            dialog.removeAll();
            dialog.close();
        });
        prepareSubmitButton();
        buttons.add(submit, cancel);
        addComponentAsFirst(author);
        addComponentAtIndex(7, buttons);
    }

    private List<User> getAllUsers() {
        return userService.findAll();
    }

    private void prepareSubmitButton() {
        submit.setText(operation.name);
        switch (operation) {
            case CREATE:
                submit.addClickListener(e -> executeCommand(new CreateTaskCommand(taskService, task), task));
                break;
            case UPDATE:
                submit.addClickListener(e -> executeCommand(new UpdateTaskCommand(taskService, task), task));
                break;
            case DELETE:
                submit.addClickListener(e -> executeCommand(new DeleteTaskCommand(taskService, task), task));
                break;
        }
    }

    private void executeCommand(Command command, Task task) {
        if (command instanceof DeleteTaskCommand) {
            command.execute();
            dialog.removeAll();
            dialog.close();
        } else if (taskBinder.writeBeanIfValid(task)) {
            command.execute();
            dialog.removeAll();
            dialog.close();
        }
    }

}
