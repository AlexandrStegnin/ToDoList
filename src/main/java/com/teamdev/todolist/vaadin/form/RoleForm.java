package com.teamdev.todolist.vaadin.form;

import com.teamdev.todolist.command.role.CreateRoleCommand;
import com.teamdev.todolist.command.role.DeleteRoleCommand;
import com.teamdev.todolist.command.role.UpdateRoleCommand;
import com.teamdev.todolist.configuration.support.OperationEnum;
import com.teamdev.todolist.entity.Role;
import com.teamdev.todolist.command.Command;
import com.teamdev.todolist.service.RoleService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;

import java.util.Objects;


/**
 * @author Alexandr Stegnin
 */

public class RoleForm extends VerticalLayout {

    private final TextField title;
    private final TextField description;
    private final Binder<Role> roleBinder;
    private final OperationEnum operation;
    private final Component parent;
    private Button submit;
    private final RoleService roleService;
    private final Role role;

    public RoleForm(OperationEnum operation, Component parent, Role role, RoleService roleService) {
        this.title = new TextField("Title");
        this.description = new TextField("Description");
        this.roleBinder = new BeanValidationBinder<>(Role.class);
        this.roleService = roleService;
        this.operation = operation;
        this.submit = new Button(operation.name());
        this.parent = parent;
        this.role = role;
        init();
    }

    private void init() {
        roleBinder.setBean(role);
        roleBinder.bindInstanceFields(this);
        add(title, description);

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

        HorizontalLayout buttons = new HorizontalLayout();

        if (!Objects.equals(null, parent) && parent instanceof Dialog) {
            Button cancel = new Button("Cancel", e -> ((Dialog) parent).close());
            buttons.add(submit, cancel);
        } else {
            buttons.add(submit);
        }

        addComponentAtIndex(2, buttons);
    }

    private void executeCommand(Command command) {
        if (roleBinder.writeBeanIfValid(role)) {
            command.execute();
            ((Dialog) parent).close();
        }
    }

}
