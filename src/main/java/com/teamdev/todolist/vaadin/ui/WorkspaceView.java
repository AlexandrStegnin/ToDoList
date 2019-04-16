package com.teamdev.todolist.vaadin.ui;

import com.teamdev.todolist.configuration.security.SecurityUtils;
import com.teamdev.todolist.configuration.support.OperationEnum;
import com.teamdev.todolist.entity.Tag;
import com.teamdev.todolist.entity.Task;
import com.teamdev.todolist.entity.User;
import com.teamdev.todolist.entity.Workspace;
import com.teamdev.todolist.service.*;
import com.teamdev.todolist.vaadin.custom.CustomAppLayout;
import com.teamdev.todolist.vaadin.form.CalendarForm;
import com.teamdev.todolist.vaadin.form.TaskForm;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.*;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.material.Material;
import org.apache.commons.lang3.StringUtils;

import javax.xml.soap.Detail;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.teamdev.todolist.configuration.support.Constants.WORKSPACES_PAGE;

/**
 * @author Alexandr Stegnin
 */

@Route(WORKSPACES_PAGE)
@PageTitle("Work space")
@HtmlImport("../VAADIN/grid-style.html")
@Theme(value = Material.class, variant = Material.LIGHT)
public class WorkspaceView extends CustomAppLayout implements HasUrlParameter<String> {

    private Long workspaceId;
    private Workspace workspace;

    private final TaskService taskService;
    private final UserService userService;
    private final TaskStatusService taskStatusService;
    private final WorkspaceService workspaceService;
    private final TagService tagService;
    private final User currentUser;
    private final DateTimeFormatter formatter;
    private final Button update;
    private final Button delete;
    private final Button addNewBtn;
    private final Button calendarBtn;
    private Grid<Task> authorGrid, performerGrid;
    private ListDataProvider<Task> authorDataProvider, performerDataProvider;
    private Predicate<Task> colorPredicate;

    private final String RED = "red";

    public WorkspaceView(TaskService taskService, UserService userService, WorkspaceService workspaceService,
                         TaskStatusService taskStatusService, TagService tagService) {
        super(userService);
        this.userService = userService;
        this.taskService = taskService;
        this.taskStatusService = taskStatusService;
        this.tagService = tagService;
        this.workspaceService = workspaceService;
        this.currentUser = this.userService.findByLogin(SecurityUtils.getUsername());
        this.formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");
        this.update = new Button("Обновить", e -> buttonsListener(OperationEnum.UPDATE));
        this.delete = new Button("Удалить", e -> buttonsListener(OperationEnum.DELETE));
        this.addNewBtn = new Button("Создать задачу", e -> showTaskForm(OperationEnum.CREATE, new Task()));
        this.calendarBtn = new Button("Календарь задач", e -> showCalendarForm());
        this.colorPredicate = (task) -> getFormattedDate(task.getExecutionDate()).compareTo(getFormattedDate(LocalDateTime.now())) < 0;
    }

    private void init() {
        this.authorDataProvider = new ListDataProvider<>(getByAuthor());
        this.performerDataProvider = new ListDataProvider<>(getByPerformerAndWorkspace());
        this.workspace = workspaceService.findById(workspaceId);
        delete.setEnabled(false);
        update.setEnabled(false);

        createAuthorGrid();
        createPerformerGrid();

        Tab authorTab = new Tab("Созданные мной задачи");
        Div authorPage = new Div();
        authorPage.setText("Author");
        Tab performerTab = new Tab("Назначенные мне задачи");
        Div performerPage = new Div();
        performerPage.setText("Performer");

//        HorizontalLayout authorBtnGroup = new HorizontalLayout();
//        authorBtnGroup.add(addNewBtn);
//        authorBtnGroup.add(delete);
//        authorBtnGroup.add(update);
//        authorBtnGroup.setAlignItems(FlexComponent.Alignment.STRETCH);
//        authorPage.add(authorBtnGroup);
//        authorPage.add(authorGrid);
//        authorPage.setVisible(false);
//
//        HorizontalLayout performerBtnGroup = new HorizontalLayout();
//        performerBtnGroup.add(calendarBtn);
//        performerBtnGroup.setAlignItems(FlexComponent.Alignment.STRETCH);
//        performerPage.add(performerBtnGroup);
//        performerPage.add(performerGrid);

     //   tabs.setSelectedTab(performerTab);


        Map<Tab, Component> tabsToPages = new HashMap<>();
        tabsToPages.put(authorTab, authorPage);
        tabsToPages.put(performerTab, performerPage);
        Tabs tabs = new Tabs(authorTab, performerTab);
        Div pages = new Div(authorPage, performerPage);
        Set<Component> pagesShown = Stream.of(performerPage)
                .collect(Collectors.toSet());

        tabs.addSelectedChangeListener(event -> {
            pagesShown.forEach(page -> page.setVisible(false));
            pagesShown.clear();
            Component selectedPage = tabsToPages.get(tabs.getSelectedTab());
            selectedPage.setVisible(true);
            pagesShown.add(selectedPage);
        });

//        Details authorZone = new Details();
//        authorZone.setSummaryText("Созданные мной задачи");
//        HorizontalLayout authorBtnGroup = new HorizontalLayout();
//        authorBtnGroup.add(addNewBtn);
//        authorBtnGroup.add(delete);
//        authorBtnGroup.add(update);
//        authorBtnGroup.setAlignItems(FlexComponent.Alignment.STRETCH);
//        authorZone.addContent(new VerticalLayout(authorBtnGroup, authorGrid));
////        authorZone.addContent(update);
////        authorZone.addContent(delete);
////        authorZone.addContent(authorGrid);
//        authorZone.setOpened(true);
//
//        Details performerZone = new Details();
//        performerZone.setSummaryText("Назначенные мне задачи");
//        performerZone.addContent(calendarBtn);
//        performerZone.addContent(performerGrid);
//        performerZone.setOpened(true);


//        VerticalLayout authorLayout = new VerticalLayout();
//        authorLayout.add(new Span("Созданные мной"));
//        authorLayout.add(authorGrid);
//        authorLayout.setAlignItems(FlexComponent.Alignment.CENTER);
//        authorLayout.setWidth("85%");
//
//        HorizontalLayout authorZoneLayout = new HorizontalLayout();
//        authorZoneLayout.add(authorLayout);
//        VerticalLayout authorRightPane = new VerticalLayout();
//        authorRightPane.setWidth("15%");
//        authorRightPane.add(addNewBtn);
//        authorRightPane.add(update);
//        authorRightPane.add(delete);
//
//        authorRightPane.setAlignItems(FlexComponent.Alignment.CENTER);
//        authorZoneLayout.add(authorRightPane);
//        authorZoneLayout.setWidthFull();
//
//        VerticalLayout performerLayout = new VerticalLayout();
//        performerLayout.add(new Span("Назначенные мне"));
//        performerLayout.add(performerGrid);
//        performerLayout.setAlignItems(FlexComponent.Alignment.CENTER);
//        performerLayout.setWidth("85%");
//
//        HorizontalLayout performerZoneLayout = new HorizontalLayout();
//        performerZoneLayout.add(performerLayout);
//        VerticalLayout performerRightPane = new VerticalLayout();
//        performerRightPane.setWidth("15%");
//        performerRightPane.add(calendarBtn);
//        performerRightPane.setAlignItems(FlexComponent.Alignment.CENTER);
//        performerZoneLayout.add(performerRightPane);
//        performerZoneLayout.setWidthFull();
//
//        VerticalLayout mainLayout = new VerticalLayout();
//        mainLayout.add(authorZone);
//        mainLayout.add(performerZone);

        setContent(tabs);
    }

    private void showTaskForm(final OperationEnum operation, final Task task) {
        TaskForm taskForm = new TaskForm(userService, taskService, taskStatusService, tagService, workspace, operation, task);
        taskForm.addOpenedChangeListener(event -> refreshDataProviders(event.isOpened(), taskForm.getOperation(), taskForm.getTask()));
        taskForm.open();
    }

    private void showCalendarForm() {
        CalendarForm calendarForm = new CalendarForm(getByPerformerAndWorkspace());
        calendarForm.open();
    }

    private List<Task> getByAuthor() {
        return taskService.findByAuthorLoginAndWorkspaceId(SecurityUtils.getUsername(), workspaceId);
    }

    private List<Task> getByPerformerAndWorkspace() {
        return taskService.findAllByPerformer(currentUser, workspaceService.findById(workspaceId));
    }

    private void createAuthorGrid() {
        authorGrid = new Grid<>();
        authorGrid.setDataProvider(authorDataProvider);
        Grid.Column<Task> titleColumn = authorGrid
                .addComponentColumn(task -> getColoredData(task, task.getTitle(), colorPredicate, RED))
                .setHeader("Название")
                .setFooter("Всего задач: " + authorDataProvider.getItems().size())
                .setSortable(true);
        Grid.Column<Task> descriptionColumn = authorGrid
                .addComponentColumn(task -> getColoredData(task, task.getDescription(), colorPredicate, RED))
                .setHeader("Описание");
        Grid.Column<Task> performerColumn = authorGrid
                .addComponentColumn(task -> getColoredData(task, getAllPerformers(task), colorPredicate, RED))
                .setHeader("Исполнители");
        Grid.Column<Task> creationDateColumn = authorGrid
                .addComponentColumn(task -> getColoredData(task, getFormattedDate(task.getCreationDate()), colorPredicate, RED))
                .setHeader("Создана")
                .setSortable(true);
        Grid.Column<Task> expiredDateColumn = authorGrid
                .addComponentColumn(task -> getColoredData(task, getFormattedDate(task.getExecutionDate()), colorPredicate, RED))
                .setHeader("Должна быть решена")
                .setSortable(true);
        authorGrid.addComponentColumn(task -> getColoredData(task, task.getComment(), colorPredicate, RED))
                .setHeader("Комментарий");
        authorGrid.addComponentColumn(task -> getColoredData(task, getAllTags(task), colorPredicate, RED))
                .setHeader("Тэги");

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

        authorGrid.addItemClickListener(e -> {
            enableButtons(e.getItem().getAuthor().getId().equals(currentUser.getId()));
            performerGrid.deselectAll();
        });

        authorGrid.addItemDoubleClickListener(e -> showTaskForm(OperationEnum.UPDATE, e.getItem()));
    }

    private void createPerformerGrid() {
        performerGrid = new Grid<>();
        performerGrid.setDataProvider(performerDataProvider);
        Grid.Column<Task> titleColumn = performerGrid
                .addComponentColumn(task -> getColoredData(task, task.getTitle(), colorPredicate, RED))
                .setHeader("Название")
                .setFooter("Всего задач: " + performerDataProvider.getItems().size())
                .setSortable(true);
        Grid.Column<Task> descriptionColumn = performerGrid
                .addComponentColumn(task -> getColoredData(task, task.getDescription(), colorPredicate, RED))
                .setHeader("Описание");
        Grid.Column<Task> authorColumn = performerGrid
                .addComponentColumn(task -> getColoredData(task, getAuthorFullName(task), colorPredicate, RED))
                .setHeader("Автор")
                .setSortable(true);
        Grid.Column<Task> creationDateColumn = performerGrid
                .addComponentColumn(task -> getColoredData(task, getFormattedDate(task.getCreationDate()), colorPredicate, RED))
                .setHeader("Создана")
                .setSortable(true);
        Grid.Column<Task> expiredDateColumn = performerGrid
                .addComponentColumn(task -> getColoredData(task, getFormattedDate(task.getExecutionDate()), colorPredicate, RED))
                .setHeader("Должна быть решена")
                .setSortable(true);
        performerGrid.addComponentColumn(task -> getColoredData(task, task.getComment(), colorPredicate, RED))
                .setHeader("Комментарий");
        performerGrid.addComponentColumn(task -> getColoredData(task, getAllTags(task), colorPredicate, RED))
                .setHeader("Тэги");

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

    private Span getColoredData(Task task, String text, Predicate<Task> predicate, String color) {
        Span result = new Span(text);
        if (predicate.test(task)) result.getStyle().set("color", color);
        return result;
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

    private String getAllTags(Task task) {
        return task.getTags().stream()
                .map(Tag::getTitle)
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

    @Override
    public void setParameter(BeforeEvent beforeEvent, @WildcardParameter String param) {
        Location location = beforeEvent.getLocation();
        workspaceId = Long.valueOf(location.getSegments().get(2));
        init();
    }
}
