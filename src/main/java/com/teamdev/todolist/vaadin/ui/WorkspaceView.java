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
import com.teamdev.todolist.vaadin.support.VaadinViewUtils;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.HtmlImport;
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
import org.apache.commons.lang3.StringUtils;

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

@PageTitle("ЗАДАЧИ")
@HtmlImport("../VAADIN/grid-style.html")
@HtmlImport("../VAADIN/form-elements-style.html")
@Route(value = WORKSPACES_PAGE, layout = MainLayout.class)
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
    private final Button updatePerformer;
    private final Button updateAuthor;
    private final Button delete;
    private final Button addNewBtn;
    private final Button calendarBtn;
    private Grid<Task> authorGrid, performerGrid;
    private ListDataProvider<Task> authorDataProvider, performerDataProvider;
    private Predicate<Task> colorPredicate;
    private TaskForm taskForm;

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
        this.formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        this.updatePerformer = VaadinViewUtils.createButton("ОБНОВИТЬ", "", "submit", "8px 10px 20px 8px");
        this.updateAuthor = VaadinViewUtils.createButton("ОБНОВИТЬ", "", "submit", "8px 10px 20px 8px");
        this.delete = VaadinViewUtils.createButton("УДАЛИТЬ", "", "cancel", "8px 10px 20px 8px");
        this.addNewBtn = VaadinViewUtils.createButton("СОЗДАТЬ ЗАДАЧУ", "", "submit", "8px 10px 20px 8px");
        this.calendarBtn = VaadinViewUtils.createButton("КАЛЕНДАРЬ ЗАДАЧ","", "submit", "8px 10px 20px 8px");
        this.colorPredicate = (task) -> getFormattedDate(task.getExecutionDate()).compareTo(getFormattedDate(LocalDateTime.now())) < 0;
    }

    private void init() {
        prepareButtons();
        this.authorDataProvider = new ListDataProvider<>(getByAuthor());
        this.performerDataProvider = new ListDataProvider<>(getByPerformerAndWorkspace());
        this.workspace = workspaceService.findById(workspaceId);

        createAuthorGrid();
        createPerformerGrid();

        Tab authorTab = new Tab("СОЗДАННЫЕ МНОЙ ЗАДАЧИ");
        authorTab.getStyle().set("color", "black");
        Div authorPage = new Div();
        Tab performerTab = new Tab("НАЗНАЧЕННЫЕ МНЕ ЗАДАЧИ");
        performerTab.getStyle().set("color", "black");
        Div performerPage = new Div();

        HorizontalLayout authorBtnGroup = new HorizontalLayout();
        authorBtnGroup.add(addNewBtn);
        authorBtnGroup.add(updateAuthor);
        authorBtnGroup.add(delete);
        authorBtnGroup.setAlignItems(FlexComponent.Alignment.STRETCH);
        authorPage.add(authorBtnGroup);
        authorPage.add(authorGrid);
        authorPage.setVisible(false);

        HorizontalLayout performerBtnGroup = new HorizontalLayout();
        performerBtnGroup.add(calendarBtn);
        performerBtnGroup.add(updatePerformer);
        performerBtnGroup.setAlignItems(FlexComponent.Alignment.STRETCH);
        performerPage.add(performerBtnGroup);
        performerPage.add(performerGrid);

        Map<Tab, Component> tabsToPages = new HashMap<>();
        tabsToPages.put(authorTab, authorPage);
        tabsToPages.put(performerTab, performerPage);
        Tabs tabs = new Tabs(performerTab, authorTab);
        Div pages = new Div(authorPage, performerPage);
        pages.setWidthFull();
        Set<Component> pagesShown = Stream.of(performerPage)
                .collect(Collectors.toSet());

        tabs.setSelectedTab(performerTab);

        tabs.addSelectedChangeListener(event -> {
            pagesShown.forEach(page -> page.setVisible(false));
            pagesShown.clear();
            Component selectedPage = tabsToPages.get(tabs.getSelectedTab());
            selectedPage.setVisible(true);
            pagesShown.add(selectedPage);
        });

        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.add(tabs);
        mainLayout.add(pages);

        setContent(mainLayout);
    }

    public void showTaskForm(final OperationEnum operation, final Task task) {
        TaskForm taskForm = new TaskForm(userService, taskService, taskStatusService, tagService, workspace, operation, task);
        this.taskForm = taskForm;
        taskForm.addOpenedChangeListener(event -> refreshDataProviders(event.isOpened(), taskForm.getOperation(), taskForm.getTask()));
        taskForm.open();
    }

    private void showCalendarForm() {
        CalendarForm calendarForm = new CalendarForm(getByPerformerAndWorkspace(), this);
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

        authorGrid.getStyle().set("border", "1px solid #9E9E9E");
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
            enableAuthorButton(e.getItem().getAuthor().getId().equals(currentUser.getId()));
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

        performerGrid.getStyle().set("border", "1px solid #9E9E9E");
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
            enablePerformerButtons(e.getItem().getPerformers().contains(currentUser));
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
            if (!isOpened && !taskForm.isCanceled()) {
                if (operation.compareTo(OperationEnum.CREATE) == 0 &&
                        task.getAuthor().getId().equals(currentUser.getId())) {
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

    private void enableAuthorButton(final boolean isAuthor) {
        updateAuthor.setEnabled(isAuthor);
        delete.setEnabled(isAuthor);
    }

    private void enablePerformerButtons(final boolean isAuthor) {
        updatePerformer.setEnabled(isAuthor);
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, @WildcardParameter String param) {
        Location location = beforeEvent.getLocation();
        workspaceId = Long.valueOf(location.getSegments().get(2));
        init();
    }

    private void prepareButtons() {
        updatePerformer.setEnabled(false);
        updatePerformer.addClickListener(e -> buttonsListener(OperationEnum.UPDATE));
        updatePerformer.getStyle().set("margin-bottom", "5px");
        updateAuthor.setEnabled(false);
        updateAuthor.addClickListener(e -> buttonsListener(OperationEnum.UPDATE));
        updateAuthor.getStyle().set("margin-bottom", "5px");
        delete.setEnabled(false);
        delete.addClickListener(e -> buttonsListener(OperationEnum.DELETE));
        delete.getStyle().set("margin-bottom", "5px");
        addNewBtn.addClickListener(e -> showTaskForm(OperationEnum.CREATE, new Task()));
        addNewBtn.getStyle().set("margin-bottom", "5px");
        calendarBtn.addClickListener(e -> showCalendarForm());
        calendarBtn.getStyle().set("margin-bottom", "5px");
    }
}

