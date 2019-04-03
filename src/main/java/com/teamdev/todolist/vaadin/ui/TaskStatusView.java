package com.teamdev.todolist.vaadin.ui;

import com.teamdev.todolist.configuration.support.OperationEnum;
import com.teamdev.todolist.entity.TaskStatus;
import com.teamdev.todolist.entity.TaskStatus_;
import com.teamdev.todolist.service.TaskStatusService;
import com.teamdev.todolist.service.UserService;
import com.teamdev.todolist.vaadin.custom.CustomAppLayout;
import com.teamdev.todolist.vaadin.support.VaadinViewUtils;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.material.Material;

import java.util.List;

import static com.teamdev.todolist.configuration.support.Constants.ADMIN_TASK_STATUSES_PAGE;

/**
 * @author Alexandr Stegnin
 */


@Route(ADMIN_TASK_STATUSES_PAGE)
@PageTitle("Task statuses")
@Theme(value = Material.class, variant = Material.LIGHT)
public class TaskStatusView extends CustomAppLayout {

    private final TaskStatusService taskStatusService;
    private Grid<TaskStatus> grid;
    private ListDataProvider<TaskStatus> dataProvider;
    private Binder<TaskStatus> binder; // отвечает за привязку данных с полей формы
    private final Button addNewBtn;

    public TaskStatusView(TaskStatusService taskStatusService, UserService userService) {
        super(userService);
        this.addNewBtn = new Button("Add new task status", e -> showDialog(new TaskStatus(), OperationEnum.CREATE));
        this.taskStatusService = taskStatusService;
        this.grid = new Grid<>();
        this.dataProvider = new ListDataProvider<>(getAll());
        this.binder = new BeanValidationBinder<>(TaskStatus.class);
        init(); // инициализируем форму
    }

    private void init() {
        grid.setDataProvider(dataProvider);
        /* Создаём колонки */
        grid.addColumn(TaskStatus::getTitle)
                .setHeader("Title")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);

        grid.addColumn(TaskStatus::getDescription)
                .setHeader("Description")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);

        grid.addComponentColumn(taskStatus -> VaadinViewUtils.makeEditorColumnActions(
                e -> showDialog(taskStatus, OperationEnum.UPDATE), e -> showDialog(taskStatus, OperationEnum.DELETE)))
                .setTextAlign(ColumnTextAlign.CENTER)
                .setEditorComponent(new Div())
                .setHeader("Actions")
                .setFlexGrow(2);

        VerticalLayout verticalLayout = new VerticalLayout(addNewBtn, grid);
        verticalLayout.setAlignItems(FlexComponent.Alignment.END);
        setContent(verticalLayout);
    }

    private List<TaskStatus> getAll() {
        return taskStatusService.findAll();
    }

    private void showDialog(TaskStatus taskStatus, OperationEnum operation) {
        FormLayout formLayout = new FormLayout();
        TextField title = new TextField("Title");
        title.setValue(taskStatus.getTitle() == null ? "" : taskStatus.getTitle());
        binder.forField(title)
                .bind(TaskStatus_.TITLE);

        TextField description = new TextField("Description");
        description.setValue(taskStatus.getDescription() == null ? "" : taskStatus.getDescription());
        binder.forField(description)
                .bind(TaskStatus_.DESCRIPTION);

        formLayout.add(title, description);

        Dialog dialog = VaadinViewUtils.initDialog();
        Button save = new Button("Save");

        Button cancel = new Button("Cancel", e -> dialog.close());
        HorizontalLayout actions = new HorizontalLayout();
        actions.add(save, cancel);

        VerticalLayout content = new VerticalLayout();

        switch (operation) {
            case UPDATE:
                content.add(formLayout, actions);
                save.addClickListener(e -> {
                    if (binder.writeBeanIfValid(taskStatus)) {
                        saveTaskStatus(taskStatus);
                        dialog.close();
                    }
                });
                break;
            case CREATE:
                content.add(formLayout, actions);
                save.addClickListener(e -> {
                    if (binder.writeBeanIfValid(taskStatus)) {
                        dataProvider.getItems().add(taskStatus);
                        saveTaskStatus(taskStatus);
                        dialog.close();
                    }
                });
                break;
            case DELETE:
                Div contentText = new Div();
                contentText.setText("Confirm delete task status: " + taskStatus.getTitle() + "?");
                content.add(contentText, actions);
                save.setText("Yes");
                save.addClickListener(e -> {
                    deleteTaskStatus(taskStatus);
                    dialog.close();
                });
                break;
        }

        dialog.add(content);
        dialog.open();
        title.getElement().callFunction("focus");

    }

    private void saveTaskStatus(TaskStatus taskStatus) {
        taskStatusService.save(taskStatus);
        dataProvider.refreshAll();
    }

    private void deleteTaskStatus(TaskStatus taskStatus) {
        dataProvider.getItems().remove(taskStatus);
        taskStatusService.delete(taskStatus.getId());
        dataProvider.refreshAll();
    }

}
