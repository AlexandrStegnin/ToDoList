package com.teamdev.todolist.vaadin.ui;


import com.teamdev.todolist.configurations.security.SecurityUtils;
import com.teamdev.todolist.repositories.AuthRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.material.Material;

import static com.teamdev.todolist.configurations.support.Constants.*;

@PageTitle("Login page")
@Route(LOGIN_PAGE)
@Theme(value = Material.class, variant = Material.LIGHT)
public class LoginView extends VerticalLayout {

    private final AuthRepository authRepository;

    public LoginView(AuthRepository authRepository) {
        this.authRepository = authRepository;
        init();
    }

    private void init() {
        LoginForm loginForm = new LoginForm();
        loginForm.addLoginListener(e -> {
            if (authenticated(e.getUsername(), e.getPassword())) {
                this.getUI().ifPresent(ui -> {
                    if (SecurityUtils.isUserInRole(ROLE_ADMIN)) {
                        ui.navigate(ADMIN_PAGE);
                    } else {
                        ui.navigate(PROFILE_PAGE);
                    }
                });
            } else {
                loginForm.setError(true);
            }
        });
        loginForm.setForgotPasswordButtonVisible(false);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);
        setSizeFull();
        add(loginForm);

        Button updateI18nButton = new Button("Switch to Russian",
                event -> loginForm.setI18n(createRussianI18n()));
        Anchor apiLink = new Anchor("./api/info", "See info about this API");
        add(updateI18nButton);
        apiLink.getStyle().set("color", "green");
        add(apiLink);
    }

    private boolean authenticated(String login, String password) {
        return authRepository.authenticate(login, password).isAuthenticated();
    }

    private LoginI18n createRussianI18n() {
        final LoginI18n i18n = LoginI18n.createDefault();

        i18n.setHeader(new LoginI18n.Header());
        i18n.getHeader().setTitle("Название приложения");
        i18n.getHeader().setDescription("Описание приложения");
        i18n.getForm().setUsername("Имя пользователя");
        i18n.getForm().setTitle("Форма входа");
        i18n.getForm().setSubmit("Войти");
        i18n.getForm().setPassword("Пароль");
        i18n.getForm().setForgotPassword("Забыли пароль?");
        i18n.getErrorMessage().setTitle("Имя пользователя/пароль указаны неверно");
        i18n.getErrorMessage()
                .setMessage("Проверьте правильность ввода имени пользователя и пароля.");
//        i18n.setAdditionalInformation(
//                "Дополнительная информация.");
        return i18n;
    }

}
