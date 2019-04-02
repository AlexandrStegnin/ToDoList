package com.teamdev.todolist.vaadin.ui;

import com.teamdev.todolist.configuration.security.SecurityUtils;
import com.teamdev.todolist.entity.User;
import com.teamdev.todolist.entity.UserProfile;
import com.teamdev.todolist.entity.UserProfile_;
import com.teamdev.todolist.service.UserService;
import com.teamdev.todolist.vaadin.custom.CustomAppLayout;
import com.teamdev.todolist.vaadin.support.VaadinViewUtils;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
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

import static com.teamdev.todolist.configuration.support.Constants.PROFILE_PAGE;

/**
 * @author stegnin
 */

@Route(PROFILE_PAGE)
@PageTitle("Profile")
@Theme(value = Material.class, variant = Material.LIGHT)
public class ProfileView extends CustomAppLayout {

    private final UserService userService;
    private final User currentUser;
    private final Binder<User> binder; // отвечает за привязку данных с полей формы
    private final Binder<UserProfile> profileBinder;
    private final Button saveChanges;

    public ProfileView(UserService userService) {
        this.userService = userService;
        this.currentUser = userService.findByLogin(SecurityUtils.getUsername());
        this.binder = new BeanValidationBinder<>(User.class);
        this.profileBinder = new BeanValidationBinder<>(UserProfile.class);
        this.saveChanges = new Button("Save changes");
        init();
    }

    private void init() {
        saveChanges.setEnabled(false);
        Span message = new Span("Привет, " + currentUser.getProfile().getName() + "!");
        message.getStyle()
                .set("font-size", "30px");
        Div welcome = new Div(message);

        Div avatar = new Div();
        avatar.setWidth("150px");
        avatar.setHeight("150px");
        avatar.getStyle().set("text-align", "center");
        Image userAvatar = VaadinViewUtils.getUserAvatar(currentUser);
        avatar.add(userAvatar);

        FormLayout formLayout = new FormLayout();

        TextField nameField = new TextField("Name");
        nameField.setValue(currentUser.getProfile().getName());
        nameField.addValueChangeListener(event ->
                enabledSaveButton(!event.getValue().equals(currentUser.getProfile().getName())));
        profileBinder.forField(nameField)
                .bind(UserProfile_.NAME);

        TextField surnameField = new TextField("Surname");
        surnameField.setValue(currentUser.getProfile().getSurname());
        surnameField.addValueChangeListener(event -> {
            enabledSaveButton(!event.getValue().equals(currentUser.getProfile().getSurname()));
        });
        profileBinder.forField(surnameField)
                .bind(UserProfile_.SURNAME);

        TextField email = new TextField("Email");
        email.setValue(currentUser.getProfile().getEmail());
        email.addValueChangeListener(event ->
                enabledSaveButton(!event.getValue().equals(currentUser.getProfile().getEmail())));
        profileBinder.forField(email)
                .withValidator(this::emailIsFree, "Email busy, input another email")
                .bind(UserProfile_.EMAIL);

        TextField pwdField = new TextField("Password");
        pwdField.setValue("");
        pwdField.addValueChangeListener(event -> enabledSaveButton(
                event.getValue().length() > 2));
        binder.forField(pwdField)
                .withValidator(pwd -> pwd.isEmpty() || pwd.length() > 2, "Password must be greater then 2 characters")
                .bind("password");

        saveChanges.addClickListener(e -> {
            updateUserProfile();
            pwdField.setValue("");
        });

        Button cancel = new Button("Cancel", e -> {
            binder.readBean(currentUser);
            profileBinder.readBean(currentUser.getProfile());
        });

        formLayout.getStyle()
                .set("width", "50%")
                .set("position", "relative")
                .set("left", "25%")
                .set("top", "25%");
        formLayout.add(nameField, surnameField, email, pwdField);

        HorizontalLayout buttons = new HorizontalLayout(saveChanges, cancel);

        VerticalLayout content = new VerticalLayout(welcome, avatar, formLayout, buttons);
        content.setAlignItems(FlexComponent.Alignment.CENTER);
        content.setSpacing(true);
        setContent(content);
    }

    private void updateUserProfile() {
        if (binder.writeBeanIfValid(currentUser) &&
                profileBinder.writeBeanIfValid(currentUser.getProfile())) {
            userService.save(currentUser);
            Notification.show("Changes have been saved successfully", 3000, Notification.Position.TOP_STRETCH);
        }
    }

    private void enabledSaveButton(boolean enabled) {
        saveChanges.setEnabled(enabled);
    }

    private boolean emailIsFree(String email) {
        if (currentUser.getProfile().getEmail().equalsIgnoreCase(email)) {
            return true;
        } else {
            return userService.emailIsBusy(email);
        }
    }

}
