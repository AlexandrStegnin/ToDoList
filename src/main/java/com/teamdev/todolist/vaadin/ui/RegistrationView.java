package com.teamdev.todolist.vaadin.ui;

import com.teamdev.todolist.entity.User;
import com.teamdev.todolist.entity.UserProfile;
import com.teamdev.todolist.entity.UserProfile_;
import com.teamdev.todolist.entity.User_;
import com.teamdev.todolist.service.UserService;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import static com.teamdev.todolist.configuration.support.Constants.REGISTRATION_PAGE;

/**
 * @author Alexandr Stegnin
 */

@PageTitle("СТРАНИЦА РЕГИСТРАЦИИ")
@Route(value = REGISTRATION_PAGE, layout = MainLayout.class)
@HtmlImport("../VAADIN/shared-styles.html")
public class RegistrationView extends Div {

    private final UserService userService;
    private final Binder<User> binder;
    private final Binder<UserProfile> profileBinder;
    private final Button registerUser;
    private User newUser;

    public RegistrationView(UserService userService) {
        this.userService = userService;
        this.binder = new BeanValidationBinder<>(User.class);
        this.profileBinder = new BeanValidationBinder<>(UserProfile.class);
        this.registerUser = new Button("Зарегистрироваться");
        this.newUser = new User();
        init();
    }

    private void init() {
        createSignUpForm();
    }

    private void registerUser() {
        if (binder.writeBeanIfValid(binder.getBean()) &&
                profileBinder.writeBeanIfValid(profileBinder.getBean())) {
            userService.registerNewUser(binder.getBean());
            Notification.show("Вы успешно зарегистрированы в системе", 3000, Notification.Position.TOP_STRETCH);
            getUI().ifPresent(ui -> ui.navigate(LoginView.class));
        }
    }

    private boolean emailIsFree(String email) {
        return userService.emailIsBusy(email);
    }

    private boolean loginIsFree(String login) {
        return userService.isLoginFree(login);
    }

    private void createSignUpForm() {
        Div signUpPage = new Div();
        signUpPage.addClassNames("signup-page", "ls-closed");
        Div signUpBox = new Div();
        signUpBox.addClassName("signup-box");
        signUpPage.add(signUpBox);

        Div logo = new Div();
        logo.addClassName("logo");
        Html link = new Html("<a href=\"javascript:void(0);\">TODO LIST</a>");

        Html smallHtml = new Html("<small>Помогаем решать любые задачи</small>");
        logo.add(link, smallHtml);
        signUpBox.add(logo);

        Div card = new Div();
        card.addClassName("card");
        signUpBox.add(card);

        Div body = new Div();
        body.addClassName("body");
        card.add(body);

        body.add(createForm());
        add(signUpPage);
    }

    private VerticalLayout createForm() {
        FormLayout formLayout = new FormLayout();
        binder.setBean(newUser);
        profileBinder.setBean(newUser.getProfile());
        TextField loginField = new TextField("Имя пользователя");
        loginField.setPrefixComponent(VaadinIcon.USER.create());
        binder.forField(loginField)
                .withValidator(this::loginIsFree, "Имя пользователя занято, введите другое имя")
                .bind(User_.LOGIN);

        TextField nameField = new TextField("Имя");
        nameField.setRequiredIndicatorVisible(true);
        nameField.setPrefixComponent(VaadinIcon.USER.create());
        profileBinder.forField(nameField)
                .bind(UserProfile_.NAME);

        TextField surnameField = new TextField("Фамилия");
        surnameField.setRequiredIndicatorVisible(true);
        surnameField.setPrefixComponent(VaadinIcon.USER.create());
        profileBinder.forField(surnameField)
                .bind(UserProfile_.SURNAME);

        TextField email = new TextField("Email");
        email.setRequiredIndicatorVisible(true);
        email.setPrefixComponent(VaadinIcon.ENVELOPE.create());
        profileBinder.forField(email)
                .withValidator(this::emailIsFree, "Email занят, введите другой email")
                .bind(UserProfile_.EMAIL);

        TextField pwdField = new TextField("Пароль");
        pwdField.setRequiredIndicatorVisible(true);
        pwdField.setPrefixComponent(VaadinIcon.LOCK.create());
        binder.forField(pwdField)
                .withValidator(pwd -> !pwd.isEmpty() && pwd.length() > 2, "Пароль должен быть более 2 символов")
                .bind("password");

        TextField confirmPwdField = new TextField("Подтверждение пароля");
        confirmPwdField.setRequiredIndicatorVisible(true);
        confirmPwdField.setPrefixComponent(VaadinIcon.LOCK.create());

        binder.forField(confirmPwdField)
                .withValidator(confirmPwd -> confirmPwd.equals(pwdField.getValue()), "Пароль и подтверждение не совпадают")
                .bind("password");

        registerUser.addClickListener(e -> registerUser());
        registerUser.addClassNames("btn", "btn-block", "btn-lg", "bg-pink", "waves-effect");
        registerUser.getStyle().set("padding", "8px 0 25px 0");

        Anchor signInLink = new Anchor("./login", "У меня есть аккаунт!");
        Div signInDiv = new Div();
        signInDiv.addClassNames("m-t-25", "m-b--5", "align-center>");
        signInDiv.add(signInLink);

        formLayout.add(loginField, nameField, surnameField, email, pwdField, confirmPwdField);

        VerticalLayout content = new VerticalLayout(formLayout, registerUser, signInDiv);
        content.setAlignItems(FlexComponent.Alignment.CENTER);
        content.setSpacing(true);
        return content;
    }

}
