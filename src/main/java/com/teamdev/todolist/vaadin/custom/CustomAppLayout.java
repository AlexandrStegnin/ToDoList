package com.teamdev.todolist.vaadin.custom;

import com.teamdev.todolist.configurations.security.SecurityUtils;
import com.teamdev.todolist.configurations.support.Constants;
import com.teamdev.todolist.repositories.AuthRepository;
import com.teamdev.todolist.vaadin.ui.LoginView;
import com.teamdev.todolist.vaadin.ui.ProfileView;
import com.teamdev.todolist.vaadin.ui.TaskListView;
import com.teamdev.todolist.vaadin.ui.admin.AdminView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.AppLayoutMenu;
import com.vaadin.flow.component.applayout.AppLayoutMenuItem;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import org.springframework.beans.factory.annotation.Autowired;

import static com.teamdev.todolist.configurations.support.Constants.ROLE_ADMIN;

public class CustomAppLayout extends AppLayout {

    @Autowired
    private AuthRepository auth;

    public CustomAppLayout() {
        AppLayoutMenu menu = createMenu();
        Image img = new Image("images/todo-list-logo.png", "ToDo List Logo");
        img.setHeight("44px");
        setBranding(img);

        this.getElement().getStyle().set("margin-top", "10px");

        AppLayoutMenuItem taskListItem = new AppLayoutMenuItem(VaadinIcon.TASKS.create(), "Task List", e -> goToPage(TaskListView.class));
        AppLayoutMenuItem logoutItem = new AppLayoutMenuItem(VaadinIcon.SIGN_OUT.create(), "Logout", e -> logout());
        AppLayoutMenuItem loginItem = new AppLayoutMenuItem(VaadinIcon.SIGN_IN.create(), "Login", e -> goToPage(LoginView.class));
        AppLayoutMenuItem adminItem = new AppLayoutMenuItem(VaadinIcon.COGS.create(), "Admin", e -> goToPage(AdminView.class));
        AppLayoutMenuItem profileItem = new AppLayoutMenuItem(VaadinIcon.USER.create(), "Profile", e -> goToPage(ProfileView.class));

        if (SecurityUtils.isUserInRole(ROLE_ADMIN)) menu.addMenuItems(adminItem);
        if (SecurityUtils.isUserLoggedIn()) {
            menu.addMenuItem(taskListItem);
            menu.addMenuItem(profileItem);
            menu.addMenuItem(logoutItem);
        } else {
            menu.addMenuItem(loginItem);
        }
    }

    public CustomAppLayout(AuthRepository auth) {
        this.auth = auth;
    }

    private void logout() {
        Notification.show("You have been Log Out successful!", 3000, Notification.Position.TOP_END);
        this.getUI().ifPresent(ui -> ui.navigate(Constants.LOGIN_URL.replace("/", "")));
        auth.logout();
    }

    private void goToPage(Class<? extends Component> clazz) {
        getUI().ifPresent(ui -> ui.navigate(clazz));
    }
}
