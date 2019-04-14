package com.teamdev.todolist.vaadin.ui.admin;

import com.teamdev.todolist.service.UserService;
import com.teamdev.todolist.vaadin.custom.CustomAppLayout;
import com.teamdev.todolist.vaadin.ui.MainLayout;
import com.teamdev.todolist.vaadin.ui.TaskStatusView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import static com.teamdev.todolist.configuration.support.Constants.ADMIN_PAGE;

@Route(value = ADMIN_PAGE, layout = MainLayout.class)
@PageTitle("АДМИНИСТРИРОВАНИЕ")
public class AdminView extends CustomAppLayout {
        
    public AdminView(UserService userService) {
        super(userService);
        init();
    }

    private void init() {
        HorizontalLayout content = new HorizontalLayout();
        content.setAlignItems(FlexComponent.Alignment.CENTER);
        content.setSizeFull();

        Image usersImg = createImage("images/users-png.png", "ПОЛЬЗОВАТЕЛИ");
        Image rolesImg = createImage("images/manage-roles.png", "РОЛИ");
        Image statusesImg = createImage("images/users-png.png", "СТАТУСЫ ЗАДАЧ");
        Image tagImg = createImage("images/users-png.png", "ТЭГИ");

        HorizontalLayout btnLayout = new HorizontalLayout();
        btnLayout.setSizeFull();
        btnLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        btnLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        btnLayout.setSpacing(true);

        Button usersBtn = new Button(" ПОЛЬЗОВАТЕЛИ", usersImg, e -> goToPage(UserView.class));
        Button rolesBtn = new Button("РОЛИ", rolesImg, e -> goToPage(RoleView.class));
        Button taskStatusesBtn = new Button("СТАТУСЫ ЗАДАЧ", statusesImg, e -> goToPage(TaskStatusView.class));
        Button tagsBtn = new Button("ТЭГИ", tagImg, e -> goToPage(TagView.class));

        btnLayout.add(usersBtn, rolesBtn, taskStatusesBtn, tagsBtn);
        setContent(btnLayout);
    }

    private void goToPage(Class<? extends Component> clazz) {
        getUI().ifPresent(ui -> ui.navigate(clazz));
    }

    private Image createImage(String src, String alt) {
        Image image = new Image(src, alt);
        image.setHeight("150px");
        image.setWidth("150px");
        return image;
    }

}
