package com.teamdev.todolist.vaadin.ui.admin;

import com.teamdev.todolist.configuration.support.OperationEnum;
import com.teamdev.todolist.entity.Role;
import com.teamdev.todolist.entity.User;
import com.teamdev.todolist.service.RoleService;
import com.teamdev.todolist.service.UserService;
import com.teamdev.todolist.vaadin.custom.CustomAppLayout;
import com.teamdev.todolist.vaadin.form.UserForm;
import com.teamdev.todolist.vaadin.support.VaadinViewUtils;
import com.teamdev.todolist.vaadin.ui.MainLayout;
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
import java.util.stream.Collectors;

import static com.teamdev.todolist.configuration.support.Constants.ADMIN_USERS_PAGE;

@PageTitle("ПОЛЬЗОВАТЕЛИ")
@Route(value = ADMIN_USERS_PAGE, layout = MainLayout.class) // Mapping - по типу RequestMapping в controller'e spring, только без переднего слэша
@HtmlImport("../VAADIN/grid-style.html")
@HtmlImport("../VAADIN/form-elements-style.html")
public class UserView extends CustomAppLayout {
    private final UserService userService;
    private final RoleService roleService;
    private Grid<User> grid; // сетка (таблица), основной элемент, в котором будут отображаться данные
    private final Button addNewBtn; // кнопка добавить нового пользователя
    private ListDataProvider<User> dataProvider; // провайдер для Grid, он управляет данными
    private UserForm userForm;

    public UserView(UserService userService, RoleService roleService) {
        super(userService);
        this.userService = userService;
        this.roleService = roleService;
        this.grid = new Grid<>(); // инициализация Grid'a
        this.dataProvider = new ListDataProvider<>(getAllUsers()); // инициализация провайдера с вставкой в него данных
        this.addNewBtn = VaadinViewUtils.createButton(
                "СОЗДАТЬ ПОЛЬЗОВАТЕЛЯ", "add", "submit", "8px 13px 28px 7px");
        init(); // инициализируем форму
    }

    private void init() {
        addNewBtn.addClickListener(e -> showUserForm(new User(), OperationEnum.CREATE));
        grid.setDataProvider(dataProvider); // говорим grid'у, что за его данные отвечает провайдер
        /* Создаём колонки */
        grid.addColumn(User::getLogin)
                .setHeader("ЛОГИН")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);

        grid.addColumn(user -> user.getProfile().getName())
                .setHeader("ИМЯ")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);

        grid.addColumn(user -> user.getProfile().getSurname())
                .setHeader("ФАМИЛИЯ")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);

        grid.addColumn(user -> user.getProfile().getMiddlename())
                .setHeader("ОТЧЕСТВО")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);

        grid.addColumn(user -> user.getProfile().getEmail())
                .setHeader("EMAIL")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);

        grid.addColumn(user -> user.getRoles().stream().map(Role::getTitle)
                .collect(Collectors.joining(", ")))
                .setHeader("РОЛИ")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);

        // добавляем "составную" колонку (2 кнопки с обработчиками событий)
        grid.addComponentColumn(user ->
                VaadinViewUtils.makeEditorColumnActions(
                        e -> showUserForm(user, OperationEnum.UPDATE),
                        e -> showUserForm(user, OperationEnum.DELETE)))
                .setTextAlign(ColumnTextAlign.CENTER)
                .setEditorComponent(new Div())
                .setHeader("ДЕЙСТВИЯ")
                .setFlexGrow(2);

        // TODO: 12.02.2019 Разобраться с component column, без setEditorComponent не рендерится
        /* вертикальный слой, на котором размещаем кнопку и под ней Grid */
        VerticalLayout verticalLayout = new VerticalLayout(addNewBtn, grid);
        verticalLayout.setAlignItems(FlexComponent.Alignment.END);

        // устанавливаем нашей странице контент в виде вертикального слоя, созданного выше
        setContent(verticalLayout);
    }

    private List<User> getAllUsers() {
        return userService.findAll();
    }

    // диалоговое окно с основными операциями
    private void showUserForm(final User user, final OperationEnum operation) {
        UserForm userForm = new UserForm(userService, roleService, user, operation);
        this.userForm = userForm;
        userForm.addOpenedChangeListener(event -> reload(!event.isOpened(), !this.userForm.isCanceled()));
        userForm.open();
    }

    private void reload(final boolean isClosed, final boolean isNotCanceled) {
        if (isClosed && isNotCanceled) dataProvider.refreshAll();
    }

}
