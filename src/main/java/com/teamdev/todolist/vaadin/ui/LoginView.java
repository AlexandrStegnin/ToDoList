package com.teamdev.todolist.vaadin.ui;


import com.teamdev.todolist.configuration.security.SecurityUtils;
import com.teamdev.todolist.repository.AuthRepository;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Input;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import static com.teamdev.todolist.configuration.support.Constants.*;

@PageTitle("Login page")
@Route(value = LOGIN_PAGE, layout = MainLayout.class)
@HtmlImport("../VAADIN/shared-styles.html")
public class LoginView extends Div {

    // TODO Добавить кнопку для просмотра API

    private final AuthRepository authRepository;

    public LoginView(AuthRepository authRepository) {
        this.authRepository = authRepository;
        init();
    }

    private void init() {
        createSignInForm();
    }

    private boolean authenticated(String login, String password) {
        return authRepository.authenticate(login, password).isAuthenticated();
    }

    private void createSignInForm() {
        Div loginPage = new Div();
        loginPage.addClassNames("login-page", "ls-closed");
        Div loginBox = new Div();
        loginBox.addClassName("login-box");
        loginPage.add(loginBox);

        Div logo = new Div();
        logo.addClassName("logo");
        Html link = new Html("<a href=\"javascript:void(0);\">TODO LIST</a>");

        Html smallHtml = new Html("<small>Помогаем решать любые задачи</small>");
        logo.add(link, smallHtml);
        loginBox.add(logo);

        Div card = new Div();
        card.addClassName("card");
        loginBox.add(card);

        Div body = new Div();
        body.addClassName("body");
        card.add(body);

        body.add(createForm());
        add(loginPage);
    }

    private Div createForm() {

        Div form = new Div();
        Div msg = new Div();
        msg.addClassName("msg");
        msg.setText(" ");
        form.add(msg);

        Div usernameGroupDiv = new Div();
        usernameGroupDiv.addClassName("input-group");
        Span inputGroupAddon = new Span();
        inputGroupAddon.addClassName("input-group-addon");
        Html personIcon = new Html("<i class=\"material-icons\">person</i>");
        inputGroupAddon.add(personIcon);
        usernameGroupDiv.add(inputGroupAddon);

        Div usernameInline = new Div();
        usernameInline.addClassName("form-line");
        usernameGroupDiv.add(usernameInline);

        Input loginInput = new Input();
        loginInput.setType("text");
        loginInput.setPlaceholder("Имя пользователя");
        loginInput.addClassName("form-control");
        loginInput.isRequiredIndicatorVisible();

        usernameInline.add(loginInput);

        form.add(usernameGroupDiv);

        Div passwordGroupDiv = new Div();
        passwordGroupDiv.addClassName("input-group");
        Span inputGroupAddonPass = new Span();
        inputGroupAddonPass.addClassName("input-group-addon");
        Html lockIcon = new Html("<i class=\"material-icons\">lock</i>");
        inputGroupAddonPass.add(lockIcon);
        passwordGroupDiv.add(inputGroupAddonPass);

        Div passwordInline = new Div();
        passwordInline.addClassName("form-line");
        passwordGroupDiv.add(passwordInline);

        Input passwordInput = new Input();
        passwordInput.setType("password");
        passwordInput.setPlaceholder("Пароль");
        passwordInput.addClassName("form-control");
        passwordInput.isRequiredIndicatorVisible();

        passwordInline.add(passwordInput);

        form.add(passwordGroupDiv);

        Div rowSubmit = new Div();
        rowSubmit.addClassName("row");
        form.add(rowSubmit);

        Div colSubmit = new Div();
        colSubmit.addClassNames("col-xs-offset-8", "col-xs-4");
        rowSubmit.add(colSubmit);

        Button signIn = new Button("ВОЙТИ", e -> {
            if (authenticated(loginInput.getValue(), passwordInput.getValue())) {
                this.getUI().ifPresent(ui -> {
                    if (SecurityUtils.isUserInRole(ROLE_ADMIN)) {
                        ui.navigate(ADMIN_PAGE);
                    } else {
                        ui.navigate(PROFILE_PAGE);
                    }
                });
            } else {
                System.out.println("ERROR");
            }
        });

        signIn.addClassNames("btn", "btn-block", "bg-pink", "waves-effect");
        signIn.getStyle().set("padding", "8px 0 25px 0");
        colSubmit.add(signIn);

        Div rowRegister = new Div();
        rowRegister.addClassName("row");
        form.add(rowRegister);

        Div colRegister = new Div();
        colRegister.addClassNames("col-xs-offset-6", "col-xs-6");
        rowRegister.add(colRegister);

        Button registration = new Button("РЕГИСТРАЦИЯ", e -> getUI().ifPresent(ui -> ui.navigate(RegistrationView.class)));

        registration.addClassNames("btn", "btn-block", "bg-blue-grey", "waves-effect");
        registration.getStyle().set("padding", "8px 0 25px 0");
        colRegister.add(registration);

        return form;
    }

}
