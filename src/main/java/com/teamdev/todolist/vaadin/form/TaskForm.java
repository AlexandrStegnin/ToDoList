package com.teamdev.todolist.vaadin.form;

import com.teamdev.todolist.command.Command;
import com.teamdev.todolist.command.task.CreateTaskCommand;
import com.teamdev.todolist.command.task.DeleteTaskCommand;
import com.teamdev.todolist.command.task.UpdateTaskCommand;
import com.teamdev.todolist.configuration.security.SecurityUtils;
import com.teamdev.todolist.configuration.support.OperationEnum;
import com.teamdev.todolist.entity.*;
import com.teamdev.todolist.service.*;
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

public class TaskForm extends Dialog {
    // todo multiSelectCombobox не отображает сообщение о не пройденной валидации
    // todo multiSelectCombobox не устанавливается в readOnly или enabled
    private final UserService userService;
    private final TaskStatusService taskStatusService;
    private final TaskService taskService;
    private final TagService tagService;
    private Task task;
    private OperationEnum operation;
    private HorizontalLayout buttons;

    private final TextField title;
    private final TextField description;
    private final Select<User> author;
    private final MultiselectComboBox<User> performers;
    private final MultiselectComboBox<Tag> tags;
    private final DatePicker creationDate;
    private final DatePicker expirationDate;
    private final Select<TaskStatus> status;
    private final TextField comment;
    private final Workspace workspace;
    private final User currentUser;
    private Binder<Task> taskBinder;
    private final Button cancel;
    private final Button delegateTask;
    private final TaskStatus defaultStatus;
    private Button submit;

    public TaskForm(UserService userService, TaskService taskService,
                    TaskStatusService taskStatusService, TagService tagService,
                    Workspace workspace, OperationEnum operation, Task task) {
        this.userService = userService;
        this.taskService = taskService;
        this.taskStatusService = taskStatusService;
        this.tagService = tagService;
        this.workspace = workspace;
        this.taskBinder = new BeanValidationBinder<>(Task.class);
        this.operation = operation;
        this.task = task;
        this.title = new TextField("Название");
        this.description = new TextField("Описание");
        this.author = new Select<>();
        this.status = new Select<>();
        this.creationDate = new DatePicker("Дата создания");
        this.expirationDate = new DatePicker("Дата окончания");
        this.performers = new MultiselectComboBox<>(this::getUserName);
        this.tags = new MultiselectComboBox<>(Tag::getTitle);
        this.comment = new TextField("Комментарий");
        this.cancel = new Button("Отменить", e -> this.close());
        this.delegateTask = new Button("Делегировать задачу");
        this.currentUser = userService.findByLogin(SecurityUtils.getUsername());
        this.defaultStatus = taskStatusService.getDefaultStatus();
        this.buttons = new HorizontalLayout();
        init();
    }

    private void init() {
        setMinWidth("300px");
        setMaxWidth("400px");

        author.setItems(getAllUsers());
        author.setTextRenderer(User::getLogin);
        author.setReadOnly(true);
        author.setValue(currentUser);

        status.setItems(getAllTaskStatuses());
        status.setTextRenderer(TaskStatus::getTitle);
        status.setEmptySelectionAllowed(false);
        status.setValue(defaultStatus);
        status.setRequiredIndicatorVisible(true);

        performers.setItems(getAllPerformers());
        performers.setRequired(true);
        performers.setRequiredIndicatorVisible(true);

        tags.setItems(getAllTags());
        tags.setRequired(true);
        tags.setRequiredIndicatorVisible(true);

        addCreationDateValueChangeListener();
        addExpirationDateValueChangeListener();

        VerticalLayout content = new VerticalLayout(title, description, creationDate, expirationDate,
                performers, comment, status, tags);
        add(content);
        task.setWorkspace(workspace);
        prepareForm(task);
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

    private Set<Tag> getAllTags() {
        return new HashSet<>(tagService.findAll());
    }

    private String getUserName(User user) {
        return user.getProfile().getName() + " " + user.getProfile().getSurname();
    }

    private void prepareForm(Task task) {
        if (Objects.equals(null, task.getAuthor())) {
            task.setAuthor(currentUser);
        }
        if (Objects.equals(null, task.getStatus())) {
            task.setStatus(defaultStatus);
        }
        taskBinder.setBean(task);

        taskBinder.forField(author)
                .bind(Task_.AUTHOR);

        taskBinder.forField(performers)
                .withValidator(users -> !Objects.equals(null, users) && !users.isEmpty(), "Добавьте минимум 1 исполнителя")
                .bind(Task_.PERFORMERS);

        taskBinder.forField(tags)
                .bind(Task_.TAGS);

        taskBinder.forField(creationDate)
                .withConverter(localDate -> LocalDateTime.of(localDate, LocalTime.now()), LocalDateTime::toLocalDate)
                .bind(Task_.CREATION_DATE);

        taskBinder.forField(expirationDate)
                .withConverter(localDate -> LocalDateTime.of(localDate, LocalTime.now()), LocalDateTime::toLocalDate)
                .bind(Task_.EXECUTION_DATE);

        taskBinder.forField(status)
                .withValidator(stat -> !status.isEmpty(), "Выберите статус задачи")
                .bind(Task_.STATUS);

        taskBinder.bindInstanceFields(this);
        addComponentAsFirst(author);
        buttons.removeAll();
        prepareSubmitButton();
        buttons.add(submit, cancel);

        if (task.getId() != null && operation.compareTo(OperationEnum.DELETE) != 0) {
            delegateTask.addClickListener(e -> delegateTask(task));
            buttons.add(delegateTask);
        }
        addComponentAtIndex(Integer.valueOf(String.valueOf(getChildren().count())), buttons);

        setReadOnlyFields(task.getAuthor().getId().equals(currentUser.getId()));
    }

    private List<User> getAllUsers() {
        return userService.findAll();
    }

    private void prepareSubmitButton() {
        this.submit = new Button(operation.name);
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
            this.close();
        } else if (taskBinder.writeBeanIfValid(task)) {
            command.execute();
            this.close();
        }
    }

    private void setReadOnlyFields(final boolean isAuthor) {
        title.setReadOnly(!isAuthor);
        description.setReadOnly(!isAuthor);
        creationDate.setReadOnly(!isAuthor);
        expirationDate.setReadOnly(!isAuthor);
    }

    private void delegateTask(Task task) {
        Task newTask = new Task();
        newTask.setTitle(task.getTitle());
        newTask.setDescription(task.getDescription());
        newTask.setStatus(defaultStatus);
        newTask.setAuthor(currentUser);
        newTask.setParentTask(task);
        this.operation = OperationEnum.CREATE;
        this.task = newTask;
        init();
    }

    public OperationEnum getOperation() {
        return operation;
    }

    public Task getTask() {
        return task;
    }

}
