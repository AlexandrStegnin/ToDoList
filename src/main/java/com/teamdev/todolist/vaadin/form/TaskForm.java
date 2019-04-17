package com.teamdev.todolist.vaadin.form;

import com.teamdev.todolist.command.Command;
import com.teamdev.todolist.command.task.CreateTaskCommand;
import com.teamdev.todolist.command.task.DeleteTaskCommand;
import com.teamdev.todolist.command.task.UpdateTaskCommand;
import com.teamdev.todolist.configuration.security.SecurityUtils;
import com.teamdev.todolist.configuration.support.OperationEnum;
import com.teamdev.todolist.entity.*;
import com.teamdev.todolist.service.TagService;
import com.teamdev.todolist.service.TaskService;
import com.teamdev.todolist.service.TaskStatusService;
import com.teamdev.todolist.service.UserService;
import com.teamdev.todolist.vaadin.support.VaadinViewUtils;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import org.vaadin.gatanaso.MultiselectComboBox;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;


/**
 * @author Alexandr Stegnin
 */

public class TaskForm extends Dialog {
    private final UserService userService;
    private final TaskStatusService taskStatusService;
    private final TaskService taskService;
    private final TagService tagService;
    private Task task;
    private OperationEnum operation;
    private HorizontalLayout buttons;
    private VerticalLayout content;

    private final TextField title;
    private final TextArea description;
    private final Select<User> author;
    private final MultiselectComboBox<User> performers;
    private final MultiselectComboBox<Tag> tags;
    private final DatePicker creationDate;
    private final DatePicker expirationDate;
    private final Select<TaskStatus> status;
    private final TextArea comment;
    private final Workspace workspace;
    private final User currentUser;
    private Binder<Task> taskBinder;
    private final Button cancel;
    private final Button delegateTask;
    private final Button addNewTagBtn;
    private final TaskStatus defaultStatus;
    private Button submit;
    private boolean canceled = false;

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
        this.title = new TextField("НАЗВАНИЕ");
        this.description = new TextArea("ОПИСАНИЕ");
        this.author = new Select<>();
        this.status = new Select<>();
        this.creationDate = new DatePicker("ДАТА СОЗДАНИЯ");
        this.expirationDate = new DatePicker("ДАТА ОКОНЧАНИЯ");
        this.performers = new MultiselectComboBox<>();
        this.tags = new MultiselectComboBox<>();
        this.comment = new TextArea("КОММЕНТАРИЙ");
        this.submit = VaadinViewUtils.createButton(operation.name.toUpperCase(), "", "submit", "8px 10px 20px 8px");
        this.cancel = VaadinViewUtils.createButton("ОТМЕНИТЬ", "", "cancel", "8px 10px 20px 8px");
        this.delegateTask = VaadinViewUtils.createButton("ДЕЛЕГИРОВАТЬ ЗАДАЧУ", "call_split", "delegate", "8px 10px 20px 8px");
        this.addNewTagBtn = VaadinViewUtils.createButton("СОЗДАТЬ НОВЫЙ ТЭГ", "add", "submit", "");
        this.currentUser = userService.findByLogin(SecurityUtils.getUsername());
        this.defaultStatus = taskStatusService.getDefaultStatus();
        this.buttons = new HorizontalLayout();
        this.content = new VerticalLayout();
        init();
    }

    private void init() {
        setWidth("600px");

        author.setItems(getAllUsers());
        author.setTextRenderer(User::getLogin);
        author.setReadOnly(true);
        author.setValue(currentUser);
        author.setWidthFull();
        author.setLabel("АВТОР");

        title.setWidthFull();

        description.setWidthFull();
        description.setHeight("100px");

        status.setItems(getAllTaskStatuses());
        status.setTextRenderer(TaskStatus::getTitle);
        status.setEmptySelectionAllowed(false);
        status.setValue(defaultStatus);
        status.setRequiredIndicatorVisible(true);
        status.setWidthFull();
        status.setLabel("ВЫБЕРИТЕ СТАТУС ЗАДАЧИ");

        performers.setItems(getAllPerformers());
        performers.setRequired(true);
        performers.setRequiredIndicatorVisible(true);
        performers.setItemLabelGenerator(User::getLogin);
        performers.setWidthFull();
        performers.setLabel("ВЫБЕРИТЕ ИСПОЛНИТЕЛЕЙ");

        tags.setItems(getAllTags());
        tags.setRequired(true);
        tags.setRequiredIndicatorVisible(true);
        tags.setItemLabelGenerator(Tag::getTitle);
        tags.setWidthFull();
        tags.setLabel("ВЫБЕРИТЕ ТЭГ");

        creationDate.setWidthFull();
        addCreationDateValueChangeListener();
        expirationDate.setWidthFull();
        addExpirationDateValueChangeListener();

        comment.setWidthFull();
        comment.setHeight("100px");
        content.setWidthFull();
        content.setAlignItems(FlexComponent.Alignment.END);
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
        return new HashSet<>(userService.findByTeam(workspace.getTeam()));
    }

    private Set<Tag> getAllTags() {
        return new HashSet<>(workspace.getTags());
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
                .withValidator(users -> !Objects.equals(null, users) && !users.isEmpty(), "ДОБАВЬТЕ ХОТЯ БЫ 1 ИСПОЛНИТЕЛЯ")
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
                .withValidator(stat -> !status.isEmpty(), "ВЫБЕРИТЕ СТАТУС ЗАДАЧИ")
                .bind(Task_.STATUS);

        taskBinder.bindInstanceFields(this);
        buttons.removeAll();
        prepareButtons();
        buttons.add(submit, cancel);

        if (task.getId() != null && operation.compareTo(OperationEnum.DELETE) != 0) {
            delegateTask.addClickListener(e -> delegateTask(task));
            buttons.add(delegateTask);
        }

        HorizontalLayout second = new HorizontalLayout();
        second.setSizeFull();
        VerticalLayout left = new VerticalLayout();
        VerticalLayout right = new VerticalLayout();
        left.add(author, title, creationDate, expirationDate, status);

        if (operation.compareTo(OperationEnum.CREATE) == 0) {
            description.setHeight("30%");
            right.add(description, performers, tags, addNewTagBtn);
        } else {
            comment.setHeight("30%");
            right.add(description, performers, tags, addNewTagBtn, comment);
        }
        second.add(left, right);
        content.add(second, buttons);
        setReadOnlyFields(task.getAuthor().getId().equals(currentUser.getId()));
    }

    private List<User> getAllUsers() {
        return userService.findAll();
    }

    private void prepareButtons() {
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
        cancel.addClickListener(e -> {
            this.canceled = true;
            this.close();
        });
        buttons.getStyle().set("padding-right", "10px");
        addNewTagBtn.setWidthFull();
        addNewTagBtn.addClickListener(e -> showAddTagForm());
        addNewTagBtn.setVisible(task.getAuthor().getId().compareTo(currentUser.getId()) == 0);
    }

    private void showAddTagForm() {
        TagForm tagForm = new TagForm(tagService, OperationEnum.CREATE, new Tag(workspace));
        tagForm.addOpenedChangeListener(event -> {
            if (!event.isOpened()) {
                refreshTags(tagForm.getTag());
            }
        });
        tagForm.open();
    }

    private void refreshTags(Tag tag) {
        Set<Tag> tagsSet = getAllTags();
        Set<Tag> selectedTags = new HashSet<>(tags.getSelectedItems());
        tagsSet.add(tag);
        tags.setItems(tagsSet);
        selectedTags.add(tag);
        tags.setValue(selectedTags);
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
        performers.setReadOnly(!isAuthor);
        tags.setReadOnly(!isAuthor);
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

    public boolean isCanceled() {
        return canceled;
    }

}
