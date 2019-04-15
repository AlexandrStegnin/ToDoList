package com.teamdev.todolist.vaadin.ui.admin;

import com.teamdev.todolist.configuration.support.OperationEnum;
import com.teamdev.todolist.entity.TaskStatus;
import com.teamdev.todolist.service.TaskStatusService;
import com.teamdev.todolist.service.UserService;
import com.teamdev.todolist.vaadin.custom.CustomAppLayout;
import com.teamdev.todolist.vaadin.form.TaskStatusForm;
import com.teamdev.todolist.vaadin.support.VaadinViewUtils;
import com.teamdev.todolist.vaadin.ui.MainLayout;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.List;

import static com.teamdev.todolist.configuration.support.Constants.ADMIN_TASK_STATUSES_PAGE;

/**
 * @author Alexandr Stegnin
 */


@PageTitle("СТАТУСЫ ЗАДАЧ")
@HtmlImport("../VAADIN/grid-style.html")
@HtmlImport("../VAADIN/form-elements-style.html")
@Route(value = ADMIN_TASK_STATUSES_PAGE, layout = MainLayout.class)
public class TaskStatusView extends CustomAppLayout {

    private final TaskStatusService taskStatusService;
    private Grid<TaskStatus> grid;
    private ListDataProvider<TaskStatus> dataProvider;
    private final Button addNewBtn;
    private TaskStatusForm taskStatusForm;

    public TaskStatusView(TaskStatusService taskStatusService, UserService userService) {
        super(userService);
        this.addNewBtn = new Button("СОЗДАТЬ НОВЫЙ СТАТУС", e -> showTaskStatusForm(new TaskStatus(), OperationEnum.CREATE));
        this.taskStatusService = taskStatusService;
        this.grid = new Grid<>();
        this.dataProvider = new ListDataProvider<>(getAllTaskStatuses());
        init(); // инициализируем форму
    }

    private void init() {
        stylizeButtons();
        grid.setDataProvider(dataProvider);
        /* Создаём колонки */
        grid.addColumn(TaskStatus::getTitle)
                .setHeader("НАЗВАНИЕ")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);

        grid.addColumn(TaskStatus::getDescription)
                .setHeader("ОПИСАНИЕ")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);

        grid.addComponentColumn(taskStatus -> VaadinViewUtils.makeEditorColumnActions(
                e -> showTaskStatusForm(taskStatus, OperationEnum.UPDATE), e -> showTaskStatusForm(taskStatus, OperationEnum.DELETE)))
                .setTextAlign(ColumnTextAlign.CENTER)
                .setEditorComponent(new Div())
                .setHeader("ДЕЙСТВИЯ")
                .setFlexGrow(2);

        VerticalLayout verticalLayout = new VerticalLayout(addNewBtn, grid);
        verticalLayout.setAlignItems(FlexComponent.Alignment.END);
        setContent(verticalLayout);
    }

    private List<TaskStatus> getAllTaskStatuses() {
        return taskStatusService.findAll();
    }

    private void showTaskStatusForm(TaskStatus taskStatus, OperationEnum operation) {
        TaskStatusForm taskStatusForm = new TaskStatusForm(taskStatusService, taskStatus, operation);
        this.taskStatusForm = taskStatusForm;
        taskStatusForm.addOpenedChangeListener(event -> reload(!event.isOpened(), !this.taskStatusForm.isCanceled()));
        taskStatusForm.open();
    }

    private void reload(final boolean isClosed, final boolean isNotCanceled) {
        if (isClosed && isNotCanceled) dataProvider.refreshAll();
    }

    private void stylizeButtons() {
        addNewBtn.addClassNames("btn", "btn-lg", "bg-green", "waves-effect");
        addNewBtn.getStyle().set("padding", "8px 10px 25px 10px");
        Html icon = new Html("<i class=\"material-icons\">add</i>");
        addNewBtn.setIcon(icon);
    }

}
