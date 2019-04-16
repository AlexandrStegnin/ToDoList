package com.teamdev.todolist.vaadin.form;

import com.teamdev.todolist.command.Command;
import com.teamdev.todolist.command.taskstatus.CreateTaskStatusCommand;
import com.teamdev.todolist.command.taskstatus.DeleteTaskStatusCommand;
import com.teamdev.todolist.command.taskstatus.UpdateTaskStatusCommand;
import com.teamdev.todolist.configuration.support.OperationEnum;
import com.teamdev.todolist.entity.TaskStatus;
import com.teamdev.todolist.service.TaskStatusService;
import com.teamdev.todolist.vaadin.support.VaadinViewUtils;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;

/**
 * @author Alexandr Stegnin
 */
public class TaskStatusForm extends Dialog {

    private final TaskStatusService taskStatusService;
    private final TaskStatus taskStatus;
    private final TextField title;
    private final TextField description;
    private final Binder<TaskStatus> taskStatusBinder;
    private final OperationEnum operation;
    private final Button cancel;
    private final HorizontalLayout buttons;
    private final VerticalLayout content;
    private Button submit;
    private boolean canceled = false;

    public TaskStatusForm(TaskStatusService taskStatusService, TaskStatus taskStatus, OperationEnum operation) {
        this.taskStatusService = taskStatusService;
        this.taskStatus = taskStatus;
        this.operation = operation;
        this.title = new TextField("НАЗВАНИЕ");
        this.description = new TextField("ОПИСАНИЕ");
        this.taskStatusBinder = new BeanValidationBinder<>(TaskStatus.class);
        this.submit = VaadinViewUtils.createButton(
                operation.name.toUpperCase(), "", "submit", "8px 10px 19px 6px");
        this.cancel = VaadinViewUtils.createButton("ОТМЕНИТЬ", "", "cancel", "8px 10px 19px 6px");
        this.buttons = new HorizontalLayout();
        this.content = new VerticalLayout();
        init();
    }

    private void init() {
        prepareButtons(operation);
        stylizeForm();
        buttons.add(submit, cancel);
        content.add(title, description, buttons);
        add(content);
        taskStatusBinder.setBean(taskStatus);
        taskStatusBinder.bindInstanceFields(this);
    }

    private void prepareButtons(OperationEnum operation) {
        switch (operation) {
            case CREATE:
                submit.addClickListener(e -> executeCommand(new CreateTaskStatusCommand(taskStatusService, taskStatus)));
                break;
            case UPDATE:
                submit.addClickListener(e -> executeCommand(new UpdateTaskStatusCommand(taskStatusService, taskStatus)));
                break;
            case DELETE:
                submit.addClickListener(e -> executeCommand(new DeleteTaskStatusCommand(taskStatusService, taskStatus)));
                break;
        }
        cancel.addClickListener(e -> {
            this.canceled = true;
            this.close();
        });
    }

    private void executeCommand(Command command) {
        if (command instanceof DeleteTaskStatusCommand) {
            command.execute();
            this.close();
        } else if (taskStatusBinder.writeBeanIfValid(taskStatus)) {
            command.execute();
            this.close();
        }
    }

    public boolean isCanceled() {
        return canceled;
    }

    private void stylizeForm() {
        setWidth("400px");
        setHeight("200px");
        title.setPlaceholder("ВВЕДИТЕ НАЗВАНИЕ");
        title.setRequiredIndicatorVisible(true);
        title.setWidthFull();
        description.setWidthFull();

        buttons.setWidthFull();
        buttons.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        content.setHeightFull();
    }

}
