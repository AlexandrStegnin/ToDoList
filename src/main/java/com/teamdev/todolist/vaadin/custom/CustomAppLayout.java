package com.teamdev.todolist.vaadin.custom;

import com.teamdev.todolist.configuration.security.SecurityUtils;
import com.teamdev.todolist.configuration.support.Constants;
import com.teamdev.todolist.repository.AuthRepository;
import com.teamdev.todolist.service.UserService;
import com.teamdev.todolist.vaadin.support.VaadinViewUtils;
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

import static com.teamdev.todolist.configuration.support.Constants.ROLE_ADMIN;

public class CustomAppLayout extends AppLayout {

    @Autowired
    private AuthRepository auth;

    private UserService userService;

    private AppLayoutMenu menu;

    public CustomAppLayout() {
        init();
    }

    private void init() {
        menu = createMenu();
        Image img = new Image("images/todo-list-logo.png", "ToDo List Logo");
        img.setHeight("44px");
        setBranding(img);

        this.getElement().getStyle().set("margin-top", "10px");

        AppLayoutMenuItem taskListItem = new AppLayoutMenuItem(VaadinIcon.TASKS.create(), "Task List", e -> goToPage(TaskListView.class));
        AppLayoutMenuItem logoutItem = new AppLayoutMenuItem(VaadinIcon.SIGN_OUT.create(), "Logout", e -> logout());
        AppLayoutMenuItem loginItem = new AppLayoutMenuItem(VaadinIcon.SIGN_IN.create(), "Login", e -> goToPage(LoginView.class));
        AppLayoutMenuItem adminItem = new AppLayoutMenuItem(VaadinIcon.COGS.create(), "Admin", e -> goToPage(AdminView.class));
        AppLayoutMenuItem profileItem = new AppLayoutMenuItem(createAvatarDiv(), SecurityUtils.getUsername() + " / profile", e -> goToPage(ProfileView.class));

        if (SecurityUtils.isUserInRole(ROLE_ADMIN)) menu.addMenuItems(adminItem);
        if (SecurityUtils.isUserLoggedIn()) {
            menu.addMenuItem(taskListItem);
            menu.addMenuItem(profileItem);
            menu.addMenuItem(logoutItem);
        } else {
            menu.addMenuItem(loginItem);
        }
    }

    public CustomAppLayout(UserService userService) {
        this.userService = userService;
        init();
    }

    private void logout() {
        Notification.show("You have been Log Out successful!", 3000, Notification.Position.TOP_END);
        this.getUI().ifPresent(ui -> ui.navigate(Constants.LOGIN_PAGE));
        auth.logout();
    }

    private void goToPage(Class<? extends Component> clazz) {
        getUI().ifPresent(ui -> ui.navigate(clazz));
    }

    private Component createAvatarDiv () {
        Image userAvatar = VaadinViewUtils.getUserAvatar(userService.findByLogin(SecurityUtils.getUsername()), true);
        userAvatar.setMaxHeight("24px");
        userAvatar.getStyle().set("margin-bottom", "8px");
        return userAvatar;
    }

    public void reload() {
        menu.clearMenuItems();
        init();
    }
}
