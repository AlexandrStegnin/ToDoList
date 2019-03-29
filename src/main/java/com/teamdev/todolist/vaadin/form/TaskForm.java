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
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import de.wathoserver.vaadin.MultiselectComboBox;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
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

@UIScope
@SpringComponent
public class TaskForm extends VerticalLayout {
//todo после добавления задачи и попытке открыть форму обновления/удаления не подтягиваются исполнители
    private UserService userService;
    private TaskService taskService;
    private TaskStatusService taskStatusService;

    private TextField title = new TextField("Название");
    private TextField description = new TextField("Описание");
    private Select<User> author;
    private User currentUser;
    private MultiselectComboBox<User> performers;
    private DatePicker creationDate;
    private DatePicker expirationDate;
    private Select<TaskStatus> status;

    private Binder<Task> taskBinder;
    private Component parent;
    private Button submit;
    private Button cancel;
    private Task task;

    public TaskForm(@Autowired UserService userService,
                    @Autowired TaskService taskService,
                    @Autowired TaskStatusService taskStatusService) {
        this.userService = userService;
        this.taskService = taskService;
        this.taskStatusService = taskStatusService;
    }

    @PostConstruct
    private void init() {
        setMinWidth("300px");
        setMaxWidth("400px");
        author = new Select<>();
        author.setItems(getAllUsers());
        author.setTextRenderer(User::getLogin);
        author.setReadOnly(true);

        status = new Select<>();
        status.setItems(getAllTaskStatuses());
        status.setTextRenderer(TaskStatus::getTitle);
        status.setEmptySelectionAllowed(false);
        status.setValue(taskStatusService.getDefaultStatus());

        submit = new Button();
        cancel = new Button("Отменить");
        performers = new MultiselectComboBox<>(this::getUserName);
        performers.setItems(getAllPerformers());
        creationDate = new DatePicker("Дата создания");
        expirationDate = new DatePicker("Дата окончания");
        addCreationDateValueChangeListener();
        addExpirationDateValueChangeListener();

        taskBinder = new BeanValidationBinder<>(Task.class);
        add(title, description, creationDate, expirationDate, performers, status);
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

    public void prepareForm(OperationEnum operation, Component parent, Task task) {
        currentUser = userService.findByLogin(SecurityUtils.getUsername());
        this.task = task;
        this.parent = parent;
        this.taskBinder.setBean(this.task);
        if (Objects.equals(null, this.task.getAuthor())) {
            task.setAuthor(currentUser);
            author.setValue(currentUser);
        }
        taskBinder.forField(author)
                .bind(Task::getAuthor, Task::setAuthor);

        taskBinder.forField(performers)
                .bind(Task::getPerformers, Task::setPerformers);

        taskBinder.forField(creationDate)
                .withConverter(localDate -> LocalDateTime.of(localDate, LocalTime.now()), LocalDateTime::toLocalDate)
                .bind(Task::getCreationDate, Task::setCreationDate);

        taskBinder.forField(expirationDate)
                .withConverter(localDate -> LocalDateTime.of(localDate, LocalTime.now()), LocalDateTime::toLocalDate)
                .bind(Task::getExecutionDate, Task::setExecutionDate);

        taskBinder.bindInstanceFields(this);

        submit.setText(operation.name);
        switch (operation) {
            case CREATE:
                submit.addClickListener(e -> executeCommand(new CreateTaskCommand(taskService, task)));
                break;
            case UPDATE:
                submit.addClickListener(e -> executeCommand(new UpdateTaskCommand(taskService, task)));
                break;
            case DELETE:
                submit.addClickListener(e -> executeCommand(new DeleteTaskCommand(taskService, task)));
                break;
        }

        HorizontalLayout buttons = new HorizontalLayout();

        if (!Objects.equals(null, parent) && parent instanceof Dialog) {
            cancel.addClickListener(e -> {
                parent.setId("canceled");
                ((Dialog) parent).close();
            });
            buttons.add(submit, cancel);
        } else {
            buttons.add(submit);
        }
        addComponentAsFirst(author);
        addComponentAtIndex(7, buttons);
    }

    private void executeCommand(Command command) {
        if (taskBinder.writeBeanIfValid(task)) {
            command.execute();
            ((Dialog) parent).close();
        }
    }

    private List<User> getAllUsers() {
        return userService.findAll();
    }
}
