package com.teamdev.todolist.vaadin.ui;

import com.teamdev.todolist.entity.User;
import com.teamdev.todolist.entity.UserProfile;
import com.teamdev.todolist.entity.UserProfile_;
import com.teamdev.todolist.entity.User_;
import com.teamdev.todolist.service.UserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.material.Material;

import static com.teamdev.todolist.configuration.support.Constants.REGISTRATION_PAGE;

/**
 * @author Alexandr Stegnin
 */

@Route(REGISTRATION_PAGE)
@PageTitle("Registration page")
@Theme(value = Material.class, variant = Material.LIGHT)
public class RegistrationView extends VerticalLayout {

    private final UserService userService;
    private final Binder<User> binder;
    private final Binder<UserProfile> profileBinder;
    private final Button registerUser;
    private User newUser;

    public RegistrationView(UserService userService) {
        this.userService = userService;
        this.binder = new BeanValidationBinder<>(User.class);
        this.profileBinder = new BeanValidationBinder<>(UserProfile.class);
        this.registerUser = new Button("Register");
        this.newUser = new User();
        init();
    }

    private void init() {
        FormLayout formLayout = new FormLayout();
        binder.setBean(newUser);
        profileBinder.setBean(newUser.getProfile());
        TextField loginField = new TextField("Login");
        binder.forField(loginField)
                .withValidator(this::loginIsFree, "Login busy, input another login")
                .bind(User_.LOGIN);

        TextField nameField = new TextField("Name");
        nameField.setRequiredIndicatorVisible(true);
        profileBinder.forField(nameField)
                .bind(UserProfile_.NAME);

        TextField surnameField = new TextField("Surname");
        surnameField.setRequiredIndicatorVisible(true);
        profileBinder.forField(surnameField)
                .bind(UserProfile_.SURNAME);

        TextField email = new TextField("Email");
        email.setRequiredIndicatorVisible(true);
        profileBinder.forField(email)
                .withValidator(this::emailIsFree, "Email busy, input another email")
                .bind(UserProfile_.EMAIL);

        TextField pwdField = new TextField("Password");
        pwdField.setRequiredIndicatorVisible(true);
        binder.forField(pwdField)
                .withValidator(pwd -> !pwd.isEmpty() && pwd.length() > 2, "Password must be greater then 2 characters")
                .bind("password");

        registerUser.addClickListener(e -> registerUser());

        Button cancel = new Button("Cancel", e -> getUI().ifPresent(ui -> ui.navigate(LoginView.class)));

        formLayout.getStyle()
                .set("width", "50%")
                .set("position", "relative")
                .set("left", "25%")
                .set("top", "25%");
        formLayout.add(loginField, nameField, surnameField, email, pwdField);

        HorizontalLayout buttons = new HorizontalLayout(registerUser, cancel);

        VerticalLayout content = new VerticalLayout(formLayout, buttons);
        content.setAlignItems(FlexComponent.Alignment.CENTER);
        content.setSpacing(true);
        add(content);
        getStyle()
                .set("position", "relative")
                .set("top", "25%");
    }

    private void registerUser() {
        if (binder.writeBeanIfValid(binder.getBean()) &&
                profileBinder.writeBeanIfValid(profileBinder.getBean())) {
            userService.registerNewUser(binder.getBean());
            Notification.show("Registration successfully", 3000, Notification.Position.TOP_STRETCH);
            getUI().ifPresent(ui -> ui.navigate(LoginView.class));
        }
    }

    private boolean emailIsFree(String email) {
        return userService.emailIsBusy(email);
    }

    private boolean loginIsFree(String login) {
        return userService.isLoginFree(login);
    }

}
