package com.teamdev.todolist.vaadin.form;

import com.teamdev.todolist.command.Command;
import com.teamdev.todolist.command.user.CreateUserCommand;
import com.teamdev.todolist.command.user.DeleteUserCommand;
import com.teamdev.todolist.command.user.UpdateUserCommand;
import com.teamdev.todolist.configuration.support.OperationEnum;
import com.teamdev.todolist.entity.Role;
import com.teamdev.todolist.entity.User;
import com.teamdev.todolist.entity.UserProfile;
import com.teamdev.todolist.service.RoleService;
import com.teamdev.todolist.service.UserService;
import com.teamdev.todolist.vaadin.support.VaadinViewUtils;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import org.vaadin.gatanaso.MultiselectComboBox;

import java.util.List;

/**
 * @author Alexandr Stegnin
 */
public class UserForm extends Dialog {

    private final UserService userService;
    private final RoleService roleService;
    private final User user;
    private final TextField login;
    private final TextField name;
    private final TextField surname;
    private final TextField middlename;
    private final TextField password;
    private final EmailField email;
    private final MultiselectComboBox<Role> roles;
    private final Checkbox accountNonLocked;

    private final Binder<User> userBinder;
    private final Binder<UserProfile> profileBinder;
    private final OperationEnum operation;
    private final Button cancel;
    private final HorizontalLayout buttons;
    private final VerticalLayout content;
    private Button submit;
    private boolean canceled = false;

    public UserForm(UserService userService, RoleService roleService, User user, OperationEnum operation) {
        this.userService = userService;
        this.roleService = roleService;
        this.login = new TextField("ИМЯ ПОЛЬЗОВАТЕЛЯ");
        this.name = new TextField("ИМЯ");
        this.surname = new TextField("ФАМИЛИЯ");
        this.middlename = new TextField("ОТЧЕСТВО");
        this.password = new TextField("ПАРОЛЬ");
        this.email = new EmailField("EMAIL");
        this.roles = new MultiselectComboBox<>();
        this.accountNonLocked = new Checkbox("АККАУНТ НЕ БЛОКИРОВАН");

        this.userBinder = new BeanValidationBinder<>(User.class);
        this.profileBinder = new BeanValidationBinder<>(UserProfile.class);
        this.operation = operation;
        this.submit = VaadinViewUtils.createButton(operation.name.toUpperCase(), "", "submit", "8px 10px 22px 8px");
        this.cancel = VaadinViewUtils.createButton("ОТМЕНИТЬ", "", "cancel", "8px 10px 22px 8px");
        this.buttons = new HorizontalLayout();
        this.content = new VerticalLayout();
        this.user = user;
        init();
    }

    private void init() {
        accountNonLocked.setVisible(true);
        prepareButtons(operation);
        stylizeForm();
        roles.setItems(getAllRoles());
        if (operation.compareTo(OperationEnum.CREATE) == 0) accountNonLocked.setVisible(false);
        buttons.add(submit, cancel);
        content.add(login, surname, name, middlename, email, roles, password, accountNonLocked, buttons);
        add(content);
        userBinder.setBean(user);
        userBinder.bindInstanceFields(this);
        profileBinder.setBean(user.getProfile());
        profileBinder.bindInstanceFields(this);
    }

    private void prepareButtons(OperationEnum operation) {
        switch (operation) {
            case CREATE:
                submit.addClickListener(e -> executeCommand(new CreateUserCommand(userService, user)));
                break;
            case UPDATE:
                submit.addClickListener(e -> executeCommand(new UpdateUserCommand(userService, user)));
                break;
            case DELETE:
                submit.addClickListener(e -> executeCommand(new DeleteUserCommand(userService, user)));
                break;
        }
        cancel.addClickListener(e -> {
            this.canceled = true;
            this.close();
        });
    }

    private void executeCommand(Command command) {
        if (command instanceof DeleteUserCommand) {
            command.execute();
            this.close();
        } else if (userBinder.writeBeanIfValid(user) &&
                    profileBinder.writeBeanIfValid(user.getProfile())) {
            command.execute();
            this.close();
        }
    }

    public boolean isCanceled() {
        return canceled;
    }

    private void stylizeForm() {
        login.setPlaceholder("ИМЯ ПОЛЬЗОВАТЕЛЯ");
        login.setRequiredIndicatorVisible(true);
        login.setWidthFull();

        name.setPlaceholder("ИМЯ");
        name.setRequired(true);
        name.setRequiredIndicatorVisible(true);
        name.setWidthFull();

        surname.setPlaceholder("ФАМИЛИЯ");
        surname.setRequired(true);
        surname.setRequiredIndicatorVisible(true);
        surname.setWidthFull();

        middlename.setPlaceholder("ОТЧЕСТВО");
        middlename.setWidthFull();

        email.setPlaceholder("EMAIL");
        email.setPreventInvalidInput(true);
        email.setRequiredIndicatorVisible(true);
        email.setWidthFull();

        roles.setLabel("ВЫБЕРИТЕ РОЛИ");
        roles.setItemLabelGenerator(Role::getTitle);
        roles.setItems(getAllRoles());

        password.setPlaceholder("ПАРОЛЬ");
        password.setRequired(true);
        password.setRequiredIndicatorVisible(true);
        password.setWidthFull();

        buttons.setWidthFull();
        buttons.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        content.setHeightFull();
        setWidth("400px");
        setHeightFull();
    }

    private List<Role> getAllRoles() {
        return roleService.findAll();
    }

}
