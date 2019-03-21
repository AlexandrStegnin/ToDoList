package com.teamdev.todolist.vaadin.ui;

import com.teamdev.todolist.entities.Task;
import com.teamdev.todolist.services.TaskService;
import com.teamdev.todolist.vaadin.custom.CustomAppLayout;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.material.Material;

import java.util.List;
import java.util.stream.Collectors;

import static com.teamdev.todolist.configurations.support.Constants.TASK_LIST_PAGE;

/**
 * @author Leonid Lebidko
 */

@Route(TASK_LIST_PAGE)
@PageTitle("Task List")
@Theme(value = Material.class, variant = Material.LIGHT)
public class TaskListView extends CustomAppLayout {

    private final TaskService taskService;
    private Grid<Task> grid;
    private ListDataProvider<Task> dataProvider;
    private Binder<Task> binder; // отвечает за привязку данных с полей формы

    public TaskListView(TaskService taskService) {
        this.taskService = taskService;
        this.grid = new Grid<>();
        this.dataProvider = new ListDataProvider<>(getAll());
        this.binder = new BeanValidationBinder<>(Task.class);
        init(); // инициализируем форму
    }

    private void init() {
        grid.setDataProvider(dataProvider);
        /* Создаём колонки */
        grid.addColumn(Task::getTitle)
                .setHeader("Title")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);

        grid.addColumn(Task::getDescription)
                .setHeader("Description")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);

        grid.addColumn(task -> task.getAuthor().getName() + " " + task.getAuthor().getSurname())
                .setHeader("Author")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);

        grid.addColumn(task -> task.getPerformers().stream().findFirst()) // todo: победить перформеров
                //.map(performer -> performer.getName() + " " + performer.getSurname())
                //.collect(Collectors.joining(", ")))
                .setHeader("Performers")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);

        grid.addColumn(Task::getCreationDate)
                .setHeader("Created at")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);

        grid.addColumn(Task::getExecutionDate)
                .setHeader("Executed at")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);

        VerticalLayout verticalLayout = new VerticalLayout(grid);
        verticalLayout.setAlignItems(FlexComponent.Alignment.END);
        setContent(verticalLayout);
    }

    private List<Task> getAll() {
        return taskService.findAll();
    }

}
