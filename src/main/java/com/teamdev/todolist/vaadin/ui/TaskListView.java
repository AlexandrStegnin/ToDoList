package com.teamdev.todolist.vaadin.ui;

import com.teamdev.todolist.configuration.security.SecurityUtils;
import com.teamdev.todolist.entity.Task;
import com.teamdev.todolist.entity.User;
import com.teamdev.todolist.service.TaskService;
import com.teamdev.todolist.service.UserService;
import com.teamdev.todolist.vaadin.custom.CustomAppLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.material.Material;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
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
    private Grid<Task> authorGrid, performerGrid;
    private ListDataProvider<Task> authorDataProvider, performerDataProvider;
    private Long currentUserId;
    private Binder<Task> binder; // отвечает за привязку данных с полей формы

    public TaskListView(TaskService taskService, UserService userService) {
        this.taskService = taskService;
        this.userService = userService;
        this.currentUserId = userService.getIdByLogin(SecurityUtils.getCurrentUser().getLogin());
        this.authorDataProvider = new ListDataProvider<>(getByAuthor());
        this.performerDataProvider = new ListDataProvider<>(getByPerformer());
        this.binder = new BeanValidationBinder<>(Task.class);
        init(); // инициализируем форму
    }

    private void init() {
        createAuthorGrid();
        createPerformerGrid();

        VerticalLayout authorLayout = new VerticalLayout();
        authorLayout.add(new Span("Созданные мной"));
        authorLayout.add(authorGrid);
        authorLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        HorizontalLayout authorZoneLayout = new HorizontalLayout();
        authorZoneLayout.add(authorLayout);
        VerticalLayout authorRightPane = new VerticalLayout();
        authorRightPane.setWidth("200px");
        authorRightPane.add(new Button("Создать новую задачу"));
        authorRightPane.setAlignItems(FlexComponent.Alignment.CENTER);
        authorZoneLayout.add(authorRightPane);
        authorZoneLayout.setWidthFull();

        VerticalLayout performerLayout = new VerticalLayout();
        performerLayout.add(new Span("Назначенные мне"));
        performerLayout.add(performerGrid);
        performerLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        HorizontalLayout performerZoneLayout = new HorizontalLayout();
        performerZoneLayout.add(performerLayout);
        Component perfromerRightPane = new Span("Какие-то графики исполнения задач, горящие задачи");
        ((Span) perfromerRightPane).setWidth("200px");
        performerZoneLayout.add(perfromerRightPane);
        performerZoneLayout.setWidthFull();

        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.add(authorZoneLayout);
        mainLayout.add(performerZoneLayout);

        setContent(mainLayout);
    }

    private List<Task> getByAuthor() {
        return taskService.findAllByAuthor(currentUserId);
    }

    private List<Task> getByPerformer() {
        return taskService.findAllByPerformer(currentUserId);
    }

    private void createAuthorGrid() {
        authorGrid = new Grid<>();
        authorGrid.setDataProvider(authorDataProvider);
        authorGrid.addColumn(Task::getTitle)
                .setHeader("Название")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);
        authorGrid.addColumn(Task::getDescription)
                .setHeader("Описание")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);
        authorGrid.addColumn(task -> task.getPerformers().stream()
                .map(performer -> performer.getProfile().getName() + " " + performer.getProfile().getSurname())
                .collect(Collectors.joining(", ")))
                .setHeader("Исполнители")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);
        authorGrid.addColumn(new LocalDateTimeRenderer<>(
                Task::getCreationDate,
                DateTimeFormatter.ofLocalizedDateTime(
                        FormatStyle.MEDIUM,
                        FormatStyle.SHORT)))
                .setHeader("Создана")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);
        authorGrid.addColumn(new LocalDateTimeRenderer<>(
                Task::getExecutionDate,
                DateTimeFormatter.ofLocalizedDateTime(
                        FormatStyle.MEDIUM,
                        FormatStyle.SHORT)))
                .setHeader("Должна быть решена")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);
    }

    private void createPerformerGrid() {
        performerGrid = new Grid<>();
        performerGrid.setDataProvider(performerDataProvider);
        performerGrid.addColumn(Task::getTitle)
                .setHeader("Название")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);
        performerGrid.addColumn(Task::getDescription)
                .setHeader("Описание")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);
        performerGrid.addColumn(task -> task.getAuthor().getProfile().getName() + " " + task.getAuthor().getProfile().getSurname())
                .setHeader("Автор")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);
        performerGrid.addColumn(new LocalDateTimeRenderer<>(
                Task::getCreationDate,
                DateTimeFormatter.ofLocalizedDateTime(
                        FormatStyle.MEDIUM,
                        FormatStyle.SHORT)))
                .setHeader("Создана")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);
        performerGrid.addColumn(new LocalDateTimeRenderer<>(
                Task::getExecutionDate,
                DateTimeFormatter.ofLocalizedDateTime(
                        FormatStyle.MEDIUM,
                        FormatStyle.SHORT)))
                .setHeader("Должна быть решена")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);
    }
}



