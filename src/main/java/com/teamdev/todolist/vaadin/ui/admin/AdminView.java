package com.teamdev.todolist.vaadin.ui.admin;

import com.teamdev.todolist.service.RoleService;
import com.teamdev.todolist.service.TaskStatusService;
import com.teamdev.todolist.service.UserService;
import com.teamdev.todolist.vaadin.custom.CustomAppLayout;
import com.teamdev.todolist.vaadin.ui.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import static com.teamdev.todolist.configuration.support.Constants.ADMIN_PAGE;

@Route(value = ADMIN_PAGE, layout = MainLayout.class)
@PageTitle("АДМИНИСТРИРОВАНИЕ")
public class AdminView extends CustomAppLayout {

    private final TaskStatusService taskStatusService;
    private final RoleService roleService;
    private final UserService userService;

    public AdminView(TaskStatusService taskStatusService, RoleService roleService, UserService userService) {
        super(userService);
        this.taskStatusService = taskStatusService;
        this.roleService = roleService;
        this.userService = userService;
        init();
    }

    private void init() {
        setContent(createAdminDiv());
    }

    private void goToPage(Class<? extends Component> clazz) {
        getUI().ifPresent(ui -> ui.navigate(clazz));
    }

    private Div createAdminDiv() {
        Div container = new Div();
        container.addClassName("container-fluid");

        Div row = new Div();
        row.addClassNames("row", "animated", "flipInX");
        row.getStyle()
                .set("margin", "15%");

        row.add(createDiv("people", "bg-indigo", "ПОЛЬЗОВАТЕЛИ", userService.count().toString(), UserView.class));
        row.add(createDiv("security", "bg-deep-orange", "РОЛИ", roleService.count().toString(), RoleView.class));
        row.add(createDiv("assignment", "bg-green", "СТАТУСЫ ЗАДАЧ", taskStatusService.count().toString(), TaskStatusView.class));
        container.add(row);
        return container;
    }

    private Div createDiv(String iconType, String bgColor, String text, String number,
                          Class<? extends Component> clazz) {


        Div colDiv = new Div();

        colDiv.addClassNames("col-lg-4", "col-md-4", "col-sm-4", "col-xs-12");
        Div infoBox = new Div();
        infoBox.addClickListener(onClick -> goToPage(clazz));
        infoBox.getStyle().set("cursor", "pointer");
        infoBox.addClassNames("info-box", bgColor, "hover-expand-effect");
        colDiv.add(infoBox);
        Div icon = new Div();
        icon.addClassName("icon");
        infoBox.add(icon);
        Html ic = new Html("<i class=\"material-icons\">" + iconType + "</i>");
        icon.add(ic);
        Div content = new Div();
        content.addClassName("content");
        infoBox.add(content);

        Div wsTypeText = new Div();
        wsTypeText.addClassName("text");
        wsTypeText.setText(text);
        wsTypeText.getStyle().set("font-size", "16px");
        wsTypeText.getStyle().set("margin-top", "0");
        content.add(wsTypeText);

        Div wsText = new Div();
        wsText.addClassName("text");
        wsText.setText("КОЛИЧЕСТВО");
        wsText.getStyle().set("margin-top", "0");
        content.add(wsText);

        Div tasksCount = new Div();
        tasksCount.addClassName("number");
        tasksCount.setText(number);
        content.add(tasksCount);

        return colDiv;
    }

}
