package com.teamdev.todolist.vaadin.ui.admin;

import com.teamdev.todolist.service.UserService;
import com.teamdev.todolist.vaadin.custom.CustomAppLayout;
import com.teamdev.todolist.vaadin.ui.TaskStatusView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.material.Material;

import static com.teamdev.todolist.configuration.support.Constants.ADMIN_PAGE;

@Route(ADMIN_PAGE)
@PageTitle("Administration")
@Theme(value = Material.class, variant = Material.LIGHT)
public class AdminView extends CustomAppLayout {
        
    public AdminView(UserService userService) {
        super(userService);
        init();
    }

    private void init() {
//        System.out.println(userService.findByLogin("ivan").getLogin());
        HorizontalLayout content = new HorizontalLayout();
        content.setAlignItems(FlexComponent.Alignment.CENTER);
        content.setSizeFull();

        Image usersImg = createImage("images/users-png.png", "Manage users");
        Image rolesImg = createImage("images/manage-roles.png", "Manage roles");
        Image statusesImg = createImage("images/users-png.png", "Manage task statuses");
        Image tagImg = createImage("images/users-png.png", "Manage tags");

        HorizontalLayout btnLayout = new HorizontalLayout();
        btnLayout.setSizeFull();
        btnLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        btnLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        btnLayout.setSpacing(true);

        Button usersBtn = new Button(" Users", usersImg, e -> goToPage(UserView.class));
        Button rolesBtn = new Button("Roles", rolesImg, e -> goToPage(RoleView.class));
        Button taskStatusesBtn = new Button("Task statuses", statusesImg, e -> goToPage(TaskStatusView.class));
        Button tagsBtn = new Button("Tags", tagImg, e -> goToPage(TagView.class));

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
