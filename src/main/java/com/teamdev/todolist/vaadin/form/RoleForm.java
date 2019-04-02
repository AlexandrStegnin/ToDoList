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

    private Button submit;
    private boolean canceled = false;

    public RoleForm(OperationEnum operation, Role role, RoleService roleService) {
        this.title = new TextField("Title");
        this.description = new TextField("Description");
        this.roleBinder = new BeanValidationBinder<>(Role.class);
        this.roleService = roleService;
        this.operation = operation;
        this.submit = new Button(operation.name);
        this.cancel = new Button("Отменить", e -> {
            this.canceled = true;
            this.close();
        });
        this.buttons = new HorizontalLayout();
        this.role = role;
        init();
    }

    private void init() {
        prepareSubmitButton(operation);
        buttons.add(submit, cancel);
        VerticalLayout content = new VerticalLayout(title, description, buttons);
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

}
