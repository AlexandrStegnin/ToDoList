package com.teamdev.todolist.vaadin.form;

import com.teamdev.todolist.command.Command;
import com.teamdev.todolist.command.role.CreateRoleCommand;
import com.teamdev.todolist.command.role.DeleteRoleCommand;
import com.teamdev.todolist.command.role.UpdateRoleCommand;
import com.teamdev.todolist.configuration.support.OperationEnum;
import com.teamdev.todolist.entity.Role;
import com.teamdev.todolist.service.RoleService;
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

public class RoleForm extends Dialog {

    private final RoleService roleService;
    private final Role role;
    private final TextField title;
    private final TextField description;
    private final Binder<Role> roleBinder;
    private final OperationEnum operation;
    private final Button cancel;
    private final HorizontalLayout buttons;
    private final VerticalLayout content;
    private Button submit;
    private boolean canceled = false;

    public RoleForm(OperationEnum operation, Role role, RoleService roleService) {
        this.title = new TextField("НАЗВАНИЕ");
        this.description = new TextField("ОПИСАНИЕ");
        this.roleBinder = new BeanValidationBinder<>(Role.class);
        this.roleService = roleService;
        this.operation = operation;
        this.submit = new Button(operation.name.toUpperCase());
        this.cancel = new Button("ОТМЕНИТЬ", e -> {
            this.canceled = true;
            this.close();
        });
        this.buttons = new HorizontalLayout();
        this.content = new VerticalLayout();
        this.role = role;
        init();
    }

    private void init() {
        prepareSubmitButton(operation);
        stylizeForm();
        buttons.add(submit, cancel);
        content.add(title, description, buttons);
        add(content);
        roleBinder.setBean(role);
        roleBinder.bindInstanceFields(this);
    }

    private void executeCommand(Command command) {
        if (operation.compareTo(OperationEnum.DELETE) == 0) {
            command.execute();
            this.close();
        } else if (roleBinder.writeBeanIfValid(role)) {
            command.execute();
            this.close();
        }
    }

    private void prepareSubmitButton(OperationEnum operation) {
        switch (operation) {
            case CREATE:
                submit.addClickListener(e -> executeCommand(new CreateRoleCommand(roleService, role)));
                break;
            case UPDATE:
                submit.addClickListener(e -> executeCommand(new UpdateRoleCommand(roleService, role)));
                break;
            case DELETE:
                submit.addClickListener(e -> executeCommand(new DeleteRoleCommand(roleService, role)));
                break;
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
        title.getStyle().set("font-size", "11px");

        description.getStyle().set("font-size", "11px");
        description.setWidthFull();

        submit.addClassNames("btn", "bg-green", "waves-effect");
        submit.getStyle().set("padding", "8px 10px 25px");

        cancel.addClassNames("btn", "bg-red", "waves-effect");
        cancel.getStyle().set("padding", "8px 10px 25px");

        buttons.setWidthFull();
        buttons.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        content.setHeightFull();
    }
}
