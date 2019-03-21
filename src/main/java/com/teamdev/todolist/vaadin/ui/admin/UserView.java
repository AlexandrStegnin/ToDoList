package com.teamdev.todolist.vaadin.ui.admin;

import com.teamdev.todolist.configurations.security.UserDetailsServiceImpl;
import com.teamdev.todolist.configurations.support.OperationEnum;
import com.teamdev.todolist.entities.Role;
import com.teamdev.todolist.entities.User;
import com.teamdev.todolist.entities.User_;
import com.teamdev.todolist.services.RoleService;
import com.teamdev.todolist.vaadin.custom.CustomAppLayout;
import com.teamdev.todolist.vaadin.support.VaadinViewUtils;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
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
import java.util.stream.Collectors;

import static com.teamdev.todolist.configurations.support.Constants.ADMIN_USERS_PAGE;

@PageTitle("Users")
@Route(ADMIN_USERS_PAGE)
@Theme(value = Material.class, variant = Material.LIGHT)
public class UserView extends CustomAppLayout {

    private final UserDetailsServiceImpl userService;
    private final RoleService roleService;
    private Grid<User> grid;
    private final Button addNewBtn;
    private ListDataProvider<User> dataProvider;
    private List<Role> roles;
    private Binder<User> binder;

    public UserView(UserDetailsServiceImpl userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
        this.grid = new Grid<>();
        this.dataProvider = new ListDataProvider<>(getAll());
        this.binder = new BeanValidationBinder<>(User.class);
        this.addNewBtn = new Button("New user", VaadinIcon.PLUS.create(), e -> showDialog(new User(), OperationEnum.CREATE));
        this.roles = getRoles();
        init();
    }

    private void init() {
        addNewBtn.setIconAfterText(true);

        grid.setDataProvider(dataProvider);

        grid.addColumn(User::getLogin)
                .setHeader("Login")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);

        grid.addColumn(User::getName)
                .setHeader("First name")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);

        grid.addColumn(User::getSurname)
                .setHeader("Surname")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);

        grid.addColumn(User::getMiddlename)
                .setHeader("Middlename")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);

        grid.addColumn(User::getEmail)
                .setHeader("Email")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);

        grid.addColumn(user -> user.getRoles().stream().map(Role::getTitle)
                .collect(Collectors.joining(", ")))
                .setHeader("Roles")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);

        Button save = new Button("Save");
        save.addClassName("save");

        Button cancel = new Button("Cancel");
        cancel.addClassName("cancel");

        grid.addComponentColumn(user ->
                VaadinViewUtils.makeEditorColumnActions(
                        e -> showDialog(user, OperationEnum.UPDATE),
                        e -> showDialog(user, OperationEnum.DELETE)))
                .setTextAlign(ColumnTextAlign.CENTER)
                .setEditorComponent(new Div())
                .setHeader("Actions")
                .setFlexGrow(2);

        // TODO: 12.02.2019 Разобраться с component column, без setEditorComponent не рендерится

        VerticalLayout verticalLayout = new VerticalLayout(addNewBtn, grid);
        verticalLayout.setAlignItems(FlexComponent.Alignment.END);
        setContent(verticalLayout);
    }

    private void saveUser(User user) {
        userService.save(user);
        dataProvider.refreshAll();
    }

    private List<User> getAll() {
        return userService.findAll();
    }

    private void deleteUser(User user) {
        dataProvider.getItems().remove(user);
        userService.delete(user);
        dataProvider.refreshAll();
    }

    private List<Role> getRoles() {
        return roleService.findAll();
    }

    private void showDialog(User user, OperationEnum operation) {
        FormLayout formLayout = new FormLayout();
        TextField loginField = new TextField("Login");
        loginField.setValue(user.getLogin() == null ? "" : user.getLogin());
        binder.forField(loginField)
                .bind(User_.LOGIN);

        TextField pwdField = new TextField("Password");
        pwdField.setValue("");
        binder.forField(pwdField)
                .bind(User_.PASSWORD);

        TextField surnameField = new TextField("Surname");
        surnameField.setValue(user.getSurname() == null ? "" : user.getSurname());
        binder.forField(surnameField)
                .bind(User_.SURNAME);

        TextField nameField = new TextField("Name");
        nameField.setValue(user.getName() == null ? "" : user.getName());
        binder.forField(nameField)
                .bind(User_.NAME);

        TextField email = new TextField("Email");
        email.setValue(user.getEmail() == null ? "" : user.getEmail());
        binder.forField(email)
                .bind(User_.EMAIL);

        Div checkBoxDiv = VaadinViewUtils.makeUserRolesDiv(user, roles);

        formLayout.add(loginField, pwdField, surnameField, nameField, email, checkBoxDiv);

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
                    if (binder.writeBeanIfValid(user)) {
                        saveUser(user);
                        dialog.close();
                    }
                });
                break;
            case CREATE:
                content.add(formLayout, actions);
                save.addClickListener(e -> {
                    dataProvider.getItems().add(user);
                    saveUser(user);
                    dialog.close();
                });
                break;
            case DELETE:
                Div contentText = new Div();
                contentText.setText("Confirm delete user: " + user.getLogin() + "?");
                content.add(contentText, actions);
                save.setText("Yes");
                save.addClickListener(e -> {
                    deleteUser(user);
                    dialog.close();
                });
                break;
        }

        dialog.add(content);
        dialog.open();
        loginField.getElement().callFunction("focus");

    }

}
