package com.teamdev.todolist.vaadin.ui;

import com.teamdev.todolist.configuration.security.SecurityUtils;
import com.teamdev.todolist.configuration.support.OperationEnum;
import com.teamdev.todolist.entity.Task;
import com.teamdev.todolist.entity.User;
import com.teamdev.todolist.service.TaskService;
import com.teamdev.todolist.service.TaskStatusService;
import com.teamdev.todolist.service.UserService;
import com.teamdev.todolist.vaadin.custom.CustomAppLayout;
import com.teamdev.todolist.vaadin.form.TaskForm;
import com.teamdev.todolist.vaadin.support.VaadinViewUtils;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.ItemClickEvent;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.material.Material;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static com.teamdev.todolist.configuration.support.Constants.TASK_LIST_PAGE;

/**
 * @author Leonid Lebidko
 */

// @StyleSheet("task.css") todo: разобраться с импортом css в ваадин. пока не работает из разных путей
@Route(TASK_LIST_PAGE)
@PageTitle("Task List")
@Theme(value = Material.class, variant = Material.LIGHT)
public class TaskListView extends CustomAppLayout {

    private final TaskService taskService;
    private final UserService userService;
    private final TaskStatusService taskStatusService;
    private Dialog dialog;
    private Grid<Task> authorGrid, performerGrid;
    private User currentUser;
    private DateTimeFormatter formatter;
    private ListDataProvider<Task> authorDataProvider, performerDataProvider;
    private Button update;
    private Button delete;

    public TaskListView(TaskService taskService, UserService userService,
                        TaskStatusService taskStatusService) {
        this.userService = userService;
        this.taskService = taskService;
        this.taskStatusService = taskStatusService;
        this.currentUser = this.userService.findByLogin(SecurityUtils.getUsername());
        this.formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");
        this.authorDataProvider = new ListDataProvider<>(getByAuthor());
        this.performerDataProvider = new ListDataProvider<>(getByPerformer());
        this.update = new Button("Обновить");
        this.delete = new Button("Удалить");
        this.dialog = VaadinViewUtils.initDialog();
        init();
    }

    private void showTaskForm(OperationEnum operation, Task task) {
        TaskForm taskForm = new TaskForm(userService, taskService, taskStatusService, operation, task, dialog);
        dialog.add(taskForm);
        dialog.open();
        dialog.addOpenedChangeListener(event -> {
            if (!event.isOpened()) {
                refreshDataProviders(operation, task);
            }
        });
    }

    private void init() {

        UI.getCurrent().getPage().addStyleSheet("css/task.css");

        createAuthorGrid();
        createPerformerGrid();

        VerticalLayout authorLayout = new VerticalLayout();
        authorLayout.add(new Span("Созданные мной"));
        authorLayout.add(authorGrid);
        authorLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        authorLayout.setWidth("85%");

        HorizontalLayout authorZoneLayout = new HorizontalLayout();
        authorZoneLayout.add(authorLayout);
        VerticalLayout authorRightPane = new VerticalLayout();
        authorRightPane.setWidth("15%");
        authorRightPane.add(new Button("Создать новую задачу", e -> showTaskForm(OperationEnum.CREATE, new Task())));
        update.setVisible(false);
        delete.setVisible(false);
        authorRightPane.add(update);
        authorRightPane.add(delete);

        authorRightPane.setAlignItems(FlexComponent.Alignment.CENTER);
        authorZoneLayout.add(authorRightPane);
        authorZoneLayout.setWidthFull();

        VerticalLayout performerLayout = new VerticalLayout();
        performerLayout.add(new Span("Назначенные мне"));
        performerLayout.add(performerGrid);
        performerLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        performerLayout.setWidth("85%");

        HorizontalLayout performerZoneLayout = new HorizontalLayout();
        performerZoneLayout.add(performerLayout);
        VerticalLayout perfromerRightPane = new VerticalLayout();
        perfromerRightPane.setWidth("15%");
        perfromerRightPane.add(new Span("Какие-то графики исполнения задач, горящие задачи"));
        performerZoneLayout.add(perfromerRightPane);
        performerZoneLayout.setWidthFull();

        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.add(authorZoneLayout);
        mainLayout.add(performerZoneLayout);

        setContent(mainLayout);
    }

    private List<Task> getByAuthor() {
        return taskService.findAllByAuthor(currentUser);
    }

    private List<Task> getByPerformer() {
        return taskService.findAllByPerformer(currentUser);
    }

    private void createAuthorGrid() {
        authorGrid = new Grid<>();
        authorGrid.setDataProvider(authorDataProvider);
        Grid.Column<Task> titleColumn = authorGrid.addColumn(Task::getTitle)
                .setHeader("Название")
                .setFooter("Всего задач: " + authorDataProvider.getItems().size())
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1)
                .setSortable(true);
        Grid.Column<Task> descriptionColumn = authorGrid.addColumn(Task::getDescription)
                .setHeader("Описание")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);
        Grid.Column<Task> performerColumn = authorGrid.addColumn(this::getAllPerformers)
                .setHeader("Исполнители")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);
        Grid.Column<Task> creationDateColumn = authorGrid.addColumn(task -> getFormattedDate(task.getCreationDate()))
                .setHeader("Создана")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1)
                .setSortable(true);
        Grid.Column<Task> expiredDateColumn = authorGrid.addColumn(task -> getFormattedDate(task.getExecutionDate()))
                .setHeader("Должна быть решена")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1)
                .setSortable(true);
        authorGrid.getStyle().set("border", "1px solid #9E9E9E").set("height", "22em");
        authorGrid.setMultiSort(true);

        HeaderRow filterRow = authorGrid.appendHeaderRow();

        TextField titleField = new TextField();
        TextField descriptionField = new TextField();
        TextField performerField = new TextField();
        TextField creationDateField = new TextField();
        TextField expiredDateField = new TextField();

        titleField.addValueChangeListener(event -> authorDataProvider
                .addFilter(task -> StringUtils.containsIgnoreCase(task.getTitle(), titleField.getValue())));
        titleField.setValueChangeMode(ValueChangeMode.EAGER);
        filterRow.getCell(titleColumn).setComponent(titleField);
        titleField.setSizeFull();
        titleField.setPlaceholder("Фильтр");

        descriptionField.addValueChangeListener(event -> authorDataProvider
                .addFilter(task -> StringUtils.containsIgnoreCase(task.getDescription(), descriptionField.getValue())));
        descriptionField.setValueChangeMode(ValueChangeMode.EAGER);
        filterRow.getCell(descriptionColumn).setComponent(descriptionField);
        descriptionField.setSizeFull();
        descriptionField.setPlaceholder("Фильтр");

        performerField.addValueChangeListener(event -> authorDataProvider
                .addFilter(task -> StringUtils.containsIgnoreCase(getAllPerformers(task), performerField.getValue())));
        performerField.setValueChangeMode(ValueChangeMode.EAGER);
        filterRow.getCell(performerColumn).setComponent(performerField);
        performerField.setSizeFull();
        performerField.setPlaceholder("Фильтр");

        creationDateField.addValueChangeListener(event -> authorDataProvider
                .addFilter(task -> StringUtils.containsIgnoreCase(getFormattedDate(task.getCreationDate()), creationDateField.getValue())));
        creationDateField.setValueChangeMode(ValueChangeMode.EAGER);
        filterRow.getCell(creationDateColumn).setComponent(creationDateField);
        creationDateField.setSizeFull();
        creationDateField.setPlaceholder("Фильтр");

        expiredDateField.addValueChangeListener(event -> authorDataProvider
                .addFilter(task -> StringUtils.containsIgnoreCase(getFormattedDate(task.getExecutionDate()), expiredDateField.getValue())));
        expiredDateField.setValueChangeMode(ValueChangeMode.EAGER);
        filterRow.getCell(expiredDateColumn).setComponent(expiredDateField);
        expiredDateField.setSizeFull();
        expiredDateField.setPlaceholder("Фильтр");

        authorGrid.setClassNameGenerator(task -> {
            if (getFormattedDate(task.getExecutionDate()).compareTo(getFormattedDate(LocalDateTime.now())) < 0)
                return "expired_row";
            return null;
        });

        authorGrid.addItemClickListener(e -> {
            showButtons(e);
            performerGrid.deselectAll();
        });

        authorGrid.addItemDoubleClickListener(e -> showTaskForm(OperationEnum.UPDATE, e.getItem()));
    }

    private void createPerformerGrid() {
        performerGrid = new Grid<>();
        performerGrid.setDataProvider(performerDataProvider);
        Grid.Column<Task> titleColumn = performerGrid.addColumn(Task::getTitle)
                .setHeader("Название")
                .setFooter("Всего задач: " + performerDataProvider.getItems().size())
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1)
                .setSortable(true);
        Grid.Column<Task> descriptionColumn = performerGrid.addColumn(Task::getDescription)
                .setHeader("Описание")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);
        Grid.Column<Task> authorColumn = performerGrid.addColumn(this::getAuthorFullName)
                .setHeader("Автор")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1)
                .setSortable(true);
        Grid.Column<Task> creationDateColumn = performerGrid.addColumn(task -> getFormattedDate(task.getCreationDate()))
                .setHeader("Создана")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1)
                .setSortable(true);
        Grid.Column<Task> expiredDateColumn = performerGrid.addColumn(task -> getFormattedDate(task.getExecutionDate()))
                .setHeader("Должна быть решена")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1)
                .setSortable(true);
        performerGrid.getStyle().set("border", "1px solid #9E9E9E").set("height", "22em");
        performerGrid.setMultiSort(true);

        HeaderRow filterRow = performerGrid.appendHeaderRow();

        TextField titleField = new TextField();
        TextField descriptionField = new TextField();
        TextField authorField = new TextField();
        TextField creationDateField = new TextField();
        TextField expiredDateField = new TextField();

        titleField.addValueChangeListener(event -> performerDataProvider
                .addFilter(task -> StringUtils.containsIgnoreCase(task.getTitle(), titleField.getValue())));
        titleField.setValueChangeMode(ValueChangeMode.EAGER);
        filterRow.getCell(titleColumn).setComponent(titleField);
        titleField.setSizeFull();
        titleField.setPlaceholder("Фильтр");

        descriptionField.addValueChangeListener(event -> performerDataProvider
                .addFilter(task -> StringUtils.containsIgnoreCase(task.getDescription(), descriptionField.getValue())));
        descriptionField.setValueChangeMode(ValueChangeMode.EAGER);
        filterRow.getCell(descriptionColumn).setComponent(descriptionField);
        descriptionField.setSizeFull();
        descriptionField.setPlaceholder("Фильтр");

        authorField.addValueChangeListener(event -> performerDataProvider
                .addFilter(task -> StringUtils.containsIgnoreCase(getAuthorFullName(task), authorField.getValue())));
        authorField.setValueChangeMode(ValueChangeMode.EAGER);
        filterRow.getCell(authorColumn).setComponent(authorField);
        authorField.setSizeFull();
        authorField.setPlaceholder("Фильтр");

        creationDateField.addValueChangeListener(event -> performerDataProvider
                .addFilter(task -> StringUtils.containsIgnoreCase(getFormattedDate(task.getCreationDate()), creationDateField.getValue())));
        creationDateField.setValueChangeMode(ValueChangeMode.EAGER);
        filterRow.getCell(creationDateColumn).setComponent(creationDateField);
        creationDateField.setSizeFull();
        creationDateField.setPlaceholder("Фильтр");

        expiredDateField.addValueChangeListener(event -> performerDataProvider
                .addFilter(task -> StringUtils.containsIgnoreCase(getFormattedDate(task.getExecutionDate()), expiredDateField.getValue())));
        expiredDateField.setValueChangeMode(ValueChangeMode.EAGER);
        filterRow.getCell(expiredDateColumn).setComponent(expiredDateField);
        expiredDateField.setSizeFull();
        expiredDateField.setPlaceholder("Фильтр");

        performerGrid.addItemClickListener(e -> {
            showButtons(e);
            authorGrid.deselectAll();
        });

        performerGrid.addItemDoubleClickListener(e -> showTaskForm(OperationEnum.UPDATE, e.getItem()));
    }

    private String getFormattedDate(LocalDateTime localDateTime) {
        return localDateTime.format(formatter);
    }

    private String getAuthorFullName(Task task) {
        return task.getAuthor().getProfile().getName() + " " + task.getAuthor().getProfile().getSurname();
    }

    private String getAllPerformers(Task task) {
        return task.getPerformers().stream()
                .map(performer -> performer.getProfile().getName() + " " + performer.getProfile().getSurname())
                .collect(Collectors.joining(", "));
    }

    private void showButtons(ItemClickEvent<Task> e) {
        update.setVisible(true);
        update.addClickListener(event -> {
            showTaskForm(OperationEnum.UPDATE, e.getItem());
            e.getSource().getDataProvider().refreshAll();
        });
        delete.setVisible(true);
        delete.addClickListener(event -> {
            showTaskForm(OperationEnum.DELETE, e.getItem());
        });
    }

    private void refreshDataProviders(OperationEnum operation, Task task) {
        if (operation.compareTo(OperationEnum.CREATE) == 0 && task.getAuthor().getId().equals(currentUser.getId())) {
            authorDataProvider.getItems().add(task);
            authorDataProvider.refreshAll();
        } else if (operation.compareTo(OperationEnum.DELETE) == 0 && task.getAuthor().getId().equals(currentUser.getId())) {
            authorDataProvider.getItems().remove(task);
            authorDataProvider.refreshAll();
        } else {
            authorDataProvider.refreshItem(task);
        }
        if (operation.compareTo(OperationEnum.CREATE) == 0 && task.getPerformers().contains(currentUser)) {
            performerDataProvider.getItems().add(task);
            performerDataProvider.refreshAll();
        } else if (operation.compareTo(OperationEnum.DELETE) == 0 && task.getPerformers().contains(currentUser)) {
            performerDataProvider.getItems().remove(task);
            performerDataProvider.refreshAll();
        } else {
            performerDataProvider.refreshItem(task);
        }
    }

}
