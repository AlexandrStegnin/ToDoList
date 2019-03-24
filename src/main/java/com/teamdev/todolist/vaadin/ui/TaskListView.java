package com.teamdev.todolist.vaadin.ui;

import com.teamdev.todolist.entity.Task;
import com.teamdev.todolist.service.TaskService;
import com.teamdev.todolist.vaadin.custom.CustomAppLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
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

@Route(TASK_LIST_PAGE)
@PageTitle("Task List")
@Theme(value = Material.class, variant = Material.LIGHT)
public class TaskListView extends CustomAppLayout {

    private final TaskService taskService;
    private Grid<Task> authorGrid, performerGrid;
    private ListDataProvider<Task> dataProvider;
    private Binder<Task> binder; // отвечает за привязку данных с полей формы

    public TaskListView(TaskService taskService) {
        this.taskService = taskService;
        this.authorGrid = new Grid<>();
        this.dataProvider = new ListDataProvider<>(getAll());
        this.binder = new BeanValidationBinder<>(Task.class);
        init(); // инициализируем форму
    }

    private void init() {
        authorGrid.setDataProvider(dataProvider);
        /* Создаём колонки */
        authorGrid.addColumn(Task::getTitle)
                .setHeader("Название")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);

        authorGrid.addColumn(Task::getDescription)
                .setHeader("Описание")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);

        authorGrid.addColumn(task -> task.getAuthor().getProfile().getName() + " " + task.getAuthor().getProfile().getSurname())
                .setHeader("Автор")
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
                .setHeader("Решена")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);

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
        performerLayout.add(new Span("Not yet implemented"));
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

    private List<Task> getAll() {
        return taskService.findAll();
    }

}
