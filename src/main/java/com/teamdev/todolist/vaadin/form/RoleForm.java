package com.teamdev.todolist.vaadin.form;

import com.teamdev.todolist.entity.Role;
import com.teamdev.todolist.command.Command;
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
    private final Command command;
    private final Component parent;
    private final Role role;

    public RoleForm(Command command, Component parent, Role role) {
        this.title = new TextField("Title");
        this.description = new TextField("Description");
        this.roleBinder = new BeanValidationBinder<>(Role.class);
        this.command = command;
        this.parent = parent;
        this.role = role;
        init();
    }

    private void init() {
        roleBinder.bindInstanceFields(this);
        add(title, description);
        Button submit = new Button(command.getCommandName(), e -> {
            if (roleBinder.writeBeanIfValid(role)) {
                executeCommand(command);
            }
        });

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
        command.execute();
    }

}
