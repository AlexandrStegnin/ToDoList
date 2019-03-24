package com.teamdev.todolist.vaadin.ui.admin;

import com.teamdev.todolist.configurations.support.OperationEnum;
import com.teamdev.todolist.entities.*;
import com.teamdev.todolist.services.RoleService;
import com.teamdev.todolist.services.UserService;
import com.teamdev.todolist.vaadin.custom.CustomAppLayout;
import com.teamdev.todolist.vaadin.support.VaadinViewUtils;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
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
@Route(ADMIN_USERS_PAGE) // Mapping - по типу RequestMapping в controller'e spring, только без переднего слэша
@Theme(value = Material.class, variant = Material.LIGHT) // используемая тема для оформления
public class UserView extends CustomAppLayout {

    private final UserService userService;
    private final RoleService roleService;
    private Grid<User> grid; // сетка (таблица), основной элемент, в котором будут отображаться данные
    private final Button addNewBtn; // кнопка добавить нового пользователя
    private ListDataProvider<User> dataProvider; // провайдер для Grid, он управляет данными
    private List<Role> roles;
    private Binder<User> binder; // отвечает за привязку данных с полей формы
    private Binder<UserProfile> profileBinder;

    public UserView(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
        this.grid = new Grid<>(); // инициализация Grid'a
        this.dataProvider = new ListDataProvider<>(getAll()); // инициализация провайдера с вставкой в него данных
        this.binder = new BeanValidationBinder<>(User.class); // в нашем случае используем валидацию полей на основе аннотаций в классе
        this.profileBinder = new BeanValidationBinder<>(UserProfile.class);
        this.addNewBtn = new Button(
                "New user", // текст на кнопке
                VaadinIcon.PLUS.create(), // иконка кнопки
                e -> showDialog(new User(), OperationEnum.CREATE) // ButtonClickEventListener, что делаем при нажатии на кнопку
        );
        this.roles = getRoles();
        init(); // инициализируем форму
    }

    private void init() {
        addNewBtn.setIconAfterText(true);

        grid.setDataProvider(dataProvider); // говорим grid'у, что за его данные отвечает провайдер

        /* Создаём колонки */
        grid.addColumn(User::getLogin)
                .setHeader("Login")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);

        grid.addColumn(user -> user.getProfile().getName())
                .setHeader("First name")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);

        grid.addColumn(user -> user.getProfile().getSurname())
                .setHeader("Surname")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);

        grid.addColumn(user -> user.getProfile().getMiddlename())
                .setHeader("Middlename")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);

        grid.addColumn(user -> user.getProfile().getEmail())
                .setHeader("Email")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);

        grid.addColumn(user -> user.getRoles().stream().map(Role::getTitle)
                .collect(Collectors.joining(", ")))
                .setHeader("Roles")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);

        Button save = new Button("Save");
        save.addClassName("save"); // добавляем класс кнопке (стиль)

        Button cancel = new Button("Cancel");
        cancel.addClassName("cancel");

        // добавляем "составную" колонку (2 кнопки с обработчиками событий)
        grid.addComponentColumn(user ->
                VaadinViewUtils.makeEditorColumnActions(
                        e -> showDialog(user, OperationEnum.UPDATE),
                        e -> showDialog(user, OperationEnum.DELETE)))
                .setTextAlign(ColumnTextAlign.CENTER)
                .setEditorComponent(new Div())
                .setHeader("Actions")
                .setFlexGrow(2);

        // TODO: 12.02.2019 Разобраться с component column, без setEditorComponent не рендерится
        /* вертикальный слой, на котором размещаем кнопку и под ней Grid */
        VerticalLayout verticalLayout = new VerticalLayout(addNewBtn, grid);
        verticalLayout.setAlignItems(FlexComponent.Alignment.END);

        // устанавливаем нашей странице контент в виде вертикального слоя, созданного выше
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

    // диалоговое окно с основными операциями
    private void showDialog(User user, OperationEnum operation) {
        FormLayout formLayout = new FormLayout(); // создаём форму
        TextField loginField = new TextField("Login"); // создаём текстовое поле
        loginField.setValue(user.getLogin() == null ? "" : user.getLogin()); // устанавливаем значение полю
        // говорим, что правильность заполнения этого поля будет проверять binder
        binder.forField(loginField)
                .bind(User_.LOGIN);

        TextField pwdField = new TextField("Password");
        pwdField.setValue("");

        TextField surnameField = new TextField("Surname");
        surnameField.setValue(user.getProfile().getSurname() == null ? "" : user.getProfile().getSurname());
        profileBinder.forField(surnameField)
                .bind(UserProfile_.SURNAME);

        TextField nameField = new TextField("Name");
        nameField.setValue(user.getProfile().getName() == null ? "" : user.getProfile().getName());
        profileBinder.forField(nameField)
                .bind(UserProfile_.NAME);

        TextField email = new TextField("Email");
        email.setValue(user.getProfile().getEmail() == null ? "" : user.getProfile().getEmail());
        profileBinder.forField(email)
                .bind(UserProfile_.EMAIL);

        // создаём div с ролями пользователя
        Div checkBoxDiv = VaadinViewUtils.makeUserRolesDiv(user, roles);
        formLayout.add(loginField);
        if (!operation.equals(OperationEnum.UPDATE)) {
            binder.forField(pwdField)
                    .bind("password");
            formLayout.add(pwdField);
        }

        Checkbox accountNonLocked = new Checkbox("Account enabled", user.isAccountNonExpired());
        accountNonLocked.addValueChangeListener(e -> user.setAccountNonLocked(e.getValue()));
        // добавляем на форму элементы
        formLayout.add(surnameField, nameField, email, checkBoxDiv, accountNonLocked);

        // создаём диалоговое окно и размещаем на нём недостающие компоненты
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
                    // тут binder проверяет, всё ли пользователь заполнил верно
                    if (binder.writeBeanIfValid(user) &&
                            profileBinder.writeBeanIfValid(user.getProfile())) {
                        saveUser(user);
                        dialog.close();
                    }
                });
                break;
            case CREATE:
                content.add(formLayout, actions);
                save.addClickListener(e -> {
                    if (binder.writeBeanIfValid(user) &&
                            profileBinder.writeBeanIfValid(user.getProfile())) {
                        dataProvider.getItems().add(user); // добавляем в провайдер, а он сам добавляет в Grid
                        saveUser(user);
                        dialog.close();
                    }
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

        // показываем диалоговое окно
        dialog.open();

        //ставим фокус на поле login
        loginField.getElement().callFunction("focus");

    }

}
