package com.teamdev.todolist.vaadin.custom;

import com.teamdev.todolist.configuration.security.SecurityUtils;
import com.teamdev.todolist.configuration.support.Constants;
import com.teamdev.todolist.entity.User;
import com.teamdev.todolist.repository.AuthRepository;
import com.teamdev.todolist.service.UserService;
import com.teamdev.todolist.vaadin.support.VaadinViewUtils;
import com.teamdev.todolist.vaadin.ui.LoginView;
import com.teamdev.todolist.vaadin.ui.ProfileView;
import com.teamdev.todolist.vaadin.ui.admin.AdminView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.AppLayoutMenu;
import com.vaadin.flow.component.applayout.AppLayoutMenuItem;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.dom.Element;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

import static com.teamdev.todolist.configuration.support.Constants.ROLE_ADMIN;

public class CustomAppLayout extends AppLayout {

    @Autowired
    private AuthRepository auth;

    private UserService userService;

    private AppLayoutMenu menu;

    private User currentDbUser;

    private void init() {
        menu = createMenu();
        menu.getElement().getStyle().set("padding", "10px");
        Image img = new Image("images/todo-list-logo.png", "ToDo List Logo");
        img.setHeight("44px");
        setBranding(img);

        AppLayoutMenuItem logoutItem = new AppLayoutMenuItem(VaadinIcon.SIGN_OUT.create(), "ВЫЙТИ", e -> logout());
        AppLayoutMenuItem loginItem = new AppLayoutMenuItem(VaadinIcon.SIGN_IN.create(), "ВОЙТИ", e -> goToPage(LoginView.class));
        AppLayoutMenuItem adminItem = new AppLayoutMenuItem(VaadinIcon.COGS.create(), "АДМИНИСТРИРОВАНИЕ", e -> goToPage(AdminView.class));
        AppLayoutMenuItem profileItem = new AppLayoutMenuItem(createAvatarDiv(),
                Objects.requireNonNull(SecurityUtils.getUsername()).toUpperCase() + " / ПРОФИЛЬ",
                e -> goToPage(ProfileView.class));

        if (SecurityUtils.isUserInRole(ROLE_ADMIN)) menu.addMenuItems(adminItem);
        if (SecurityUtils.isUserLoggedIn()) {
            menu.addMenuItem(profileItem);
            menu.addMenuItem(logoutItem);
        } else {
            menu.addMenuItem(loginItem);
        }

        menu.getElement().getChildren().forEach(this::stylizeItem);

    }

    private void stylizeItem(Element item) {
        item.getStyle().set("font-size", "16px");
        item.getStyle().set("font-weight", "bold");
        item.getStyle().set("color", "#ac1455");
    }

    public CustomAppLayout(UserService userService) {
        this.userService = userService;
        this.currentDbUser = userService.findByLogin(SecurityUtils.getUsername());
        init();
    }

    private void logout() {
        Notification.show("ВЫ УСПЕШНО ВЫШЛИ ИЗ СИСТЕМЫ!", 3000, Notification.Position.TOP_END);
        this.getUI().ifPresent(ui -> ui.navigate(Constants.LOGIN_PAGE));
        auth.logout();
    }

    private void goToPage(Class<? extends Component> clazz) {
        getUI().ifPresent(ui -> ui.navigate(clazz));
    }

    private Component createAvatarDiv() {
        Image userAvatar = VaadinViewUtils.getUserAvatar(userService.findByLogin(SecurityUtils.getUsername()), true);
        userAvatar.setMaxHeight("30px");
        userAvatar.getStyle().set("margin-right", "8px");
        return userAvatar;
    }

    protected void reload() {
        menu.clearMenuItems();
        init();
    }

    protected User getCurrentDbUser() {
        return currentDbUser;
    }

}
