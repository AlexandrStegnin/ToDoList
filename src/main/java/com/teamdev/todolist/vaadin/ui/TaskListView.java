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
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
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
    private final User currentUser;
    private final DateTimeFormatter formatter;
    private final Button update;
    private final Button delete;
    private final Button addNewBtn;
    private Grid<Task> authorGrid, performerGrid;
    private ListDataProvider<Task> authorDataProvider, performerDataProvider;

    public TaskListView(TaskService taskService, UserService userService,
                        TaskStatusService taskStatusService) {
        this.userService = userService;
        this.taskService = taskService;
        this.taskStatusService = taskStatusService;
        this.currentUser = this.userService.findByLogin(SecurityUtils.getUsername());
        this.formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");
        this.authorDataProvider = new ListDataProvider<>(getByAuthor());
        this.performerDataProvider = new ListDataProvider<>(getByPerformer());
        this.update = new Button("Обновить", e -> buttonsListener(OperationEnum.UPDATE));
        this.delete = new Button("Удалить", e -> buttonsListener(OperationEnum.DELETE));
        this.addNewBtn = new Button("Создать задачу", e -> showTaskForm(OperationEnum.CREATE, new Task()));
        init();
    }

    private void showTaskForm(final OperationEnum operation, final Task task) {
        TaskForm taskForm = new TaskForm(userService, taskService, taskStatusService, operation, task);
        taskForm.addOpenedChangeListener(event -> refreshDataProviders(event.isOpened(), taskForm.getOperation(), taskForm.getTask()));
        taskForm.open();
    }

    private void init() {
        delete.setEnabled(false);
        update.setEnabled(false);

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
        authorRightPane.add(addNewBtn);
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
        authorGrid.addColumn(Task::getComment)
                .setHeader("Комментарий")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);

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
            enableButtons(e.getItem().getAuthor().getId().equals(currentUser.getId()));
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
        performerGrid.addColumn(Task::getComment)
                .setHeader("Комментарий")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);
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
            enableButtons(e.getItem().getAuthor().getId().equals(currentUser.getId()));
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

    private void refreshDataProviders(final boolean isOpened, final OperationEnum operation, final Task task) {
        if (task.getId() != null) {
            if (!isOpened) {
                if (operation.compareTo(OperationEnum.CREATE) == 0 && task.getAuthor().getId().equals(currentUser.getId())) {
                    authorDataProvider.getItems().add(task);
                } else if (operation.compareTo(OperationEnum.DELETE) == 0 && task.getAuthor().getId().equals(currentUser.getId())) {
                    authorDataProvider.getItems().remove(task);
                }
                if (operation.compareTo(OperationEnum.CREATE) == 0 && task.getPerformers().contains(currentUser)) {
                    performerDataProvider.getItems().add(task);
                } else if (operation.compareTo(OperationEnum.DELETE) == 0 && task.getPerformers().contains(currentUser)) {
                    performerDataProvider.getItems().remove(task);
                }
                authorDataProvider.refreshAll();
                performerDataProvider.refreshAll();
            }
        }
    }

    private void buttonsListener(OperationEnum operation) {
        Task authorGridTask = authorGrid.getSelectedItems().stream().findFirst().orElse(null);
        Task performerGridTask = performerGrid.getSelectedItems().stream().findFirst().orElse(null);
        if (authorGridTask != null) {
            showTaskForm(operation, authorGridTask);
        } else if (performerGridTask != null) {
            showTaskForm(operation, performerGridTask);
        }
    }

    private void enableButtons(final boolean isAuthor) {
        update.setEnabled(true);
        delete.setEnabled(isAuthor);
    }

}
