package com.teamdev.todolist.vaadin.ui;

import com.teamdev.todolist.configuration.security.SecurityUtils;
import com.teamdev.todolist.configuration.support.OperationEnum;
import com.teamdev.todolist.entity.*;
import com.teamdev.todolist.service.TeamService;
import com.teamdev.todolist.service.UserService;
import com.teamdev.todolist.service.WorkspaceService;
import com.teamdev.todolist.vaadin.custom.CustomAppLayout;
import com.teamdev.todolist.vaadin.form.TeamForm;
import com.teamdev.todolist.vaadin.support.VaadinViewUtils;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.UploadI18N;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.teamdev.todolist.configuration.support.Constants.*;

/**
 * @author stegnin
 */

@Route(value = PROFILE_PAGE, layout = MainLayout.class)
@PageTitle("Profile")
public class ProfileView extends CustomAppLayout {
    // TODO добавить создание нового РП/удаление/изменение
    private final String MY_WORKSPACE =  WORKSPACES_PAGE + PATH_SEPARATOR + SecurityUtils.getUsername() + PATH_SEPARATOR;

    private final UserService userService;
    private final TeamService teamService;
    private final User currentUser;
    private final Binder<User> binder; // отвечает за привязку данных с полей формы
    private final Binder<UserProfile> profileBinder;
    private final Button saveChanges;
    private final WorkspaceService workspaceService;
    private TeamForm teamForm;
    private List<Workspace> workspaces;
    private MemoryBuffer buffer;
    private Upload uploadAvatar;

    public ProfileView(UserService userService, WorkspaceService workspaceService, TeamService teamService) {
        super(userService);
        this.buffer = new MemoryBuffer();
        this.teamService = teamService;
        this.userService = userService;
        this.workspaceService = workspaceService;
        this.currentUser = userService.findByLogin(SecurityUtils.getUsername());
        this.binder = new BeanValidationBinder<>(User.class);
        this.profileBinder = new BeanValidationBinder<>(UserProfile.class);
        this.saveChanges = new Button("СОХРАНИТЬ");
        init();
    }

    private void init() {
        saveChanges.setEnabled(false);
        workspaces = workspaceService.getMyWorkspaces(SecurityUtils.getUsername());

        Div profileDiv = profilePage();
        profileDiv.getStyle().set("margin-top", "16px");
        setContent(profileDiv);
    }

    private Div profilePage() {
        Div container = new Div();
        container.addClassName("container-fluid");
        Div rowClearFix = new Div();
        rowClearFix.addClassNames("row", "clearfix");
        container.add(rowClearFix);
        rowClearFix.add(profileDiv());
        rowClearFix.add(settingsDiv());
        return container;
    }

    private Div profileDiv() {

        // TODO подсчёт задач

        int totalTasks = 20;
        int completed = 16;
        int active = 4;
        int expired = 2;

        Div cols = new Div();
        cols.addClassNames("col-xs-12", "col-sm-3");
        Div profileCard = new Div();
        profileCard.addClassNames("card", "profile-card");
        cols.add(profileCard);
        Div profileHeader = new Div();
        profileHeader.addClassName("profile-header");
        profileCard.add(profileHeader);
        Div profileBody = new Div();
        profileBody.addClassName("profile-body");
        profileCard.add(profileBody);
        Div imageArea = new Div();
        imageArea.addClassName("image-area");
        Image userAvatar = VaadinViewUtils.getUserAvatar(currentUser, false);
        imageArea.add(userAvatar);
        profileBody.add(imageArea);
        Div contentArea = new Div();
        contentArea.addClassName("content-area");
        Html h3 = new Html("<h3>" + currentUser.getProfile().getSurname() + " " + currentUser.getProfile().getName() + "</h3>");
        contentArea.add(h3);
        Html p = new Html("<p>&nbsp</p>");
        contentArea.add(p);
        p = new Html("<p>" + getRolesAsString() + "</p>");
        contentArea.add(p);
        profileBody.add(contentArea);
        Div profileFooter = new Div();
        profileFooter.addClassName("profile-footer");

        Html ul = new Html("<ul>" +
                "<li>" +
                "<span>Всего задач</span>" +
                "<span>" + totalTasks + "</span>" +
                "</li>" +
                "<li>" +
                "<span>Решённых</span>" +
                "<span>" + completed + "</span>" +
                "</li>" +
                "<li>" +
                "<span>Активных</span>" +
                "<span>" + active + "</span>" +
                "</li>" +
                "<li>" +
                "<span>Просроченных</span>" +
                "<span>" + expired + "</span>" +
                "</li>" +
                "</ul>");

        uploadAvatar = initUpload();
        profileFooter.add(ul);
        profileFooter.add(uploadAvatar);
        profileCard.add(profileFooter);
        return cols;
    }

    private Div settingsDiv() {
        Div cols = new Div();
        cols.addClassNames("col-xs-12", "col-sm-9");

        Div card = new Div();
        card.addClassName("card");
        cols.add(card);

        Div body = new Div();
        body.addClassName("body");
        card.add(body);

        body.add(tabs(settings()));
        return cols;
    }

    private Map<Integer, Div> settings() {
        Map<Integer, Div> divMap = new HashMap<>();

        Div myWorkspacesPage = new Div();
        myWorkspacesPage.add(workspaceDiv());

        Div profilePage = new Div();
        profilePage.add(profileForm());
        profilePage.setVisible(false);

        Div changePassPage = new Div();
        changePassPage.add(changePasswordForm());
        changePassPage.setVisible(false);

        divMap.put(1, myWorkspacesPage);
        divMap.put(2, profilePage);
        divMap.put(3, changePassPage);

        return divMap;
    }

    private Div tabs(Map<Integer, Div> divMap) {
        Tab myWorkspaces = new Tab("Рабочие области");
        myWorkspaces.getStyle().set("color", "black");

        Tab profileSettings = new Tab("Настройки профиля");
        profileSettings.getStyle().set("color", "black");

        Tab changePassword = new Tab("Изменить пароль");
        changePassword.getStyle().set("color", "black");

        Map<Tab, Component> tabsToPages = new HashMap<>();

        tabsToPages.put(myWorkspaces, divMap.get(1));
        tabsToPages.put(profileSettings, divMap.get(2));
        tabsToPages.put(changePassword, divMap.get(3));
        Tabs tabs = new Tabs(myWorkspaces, profileSettings, changePassword);
        Set<Component> pagesShown = new HashSet<>(divMap.values());
        tabs.addSelectedChangeListener(event -> {
            pagesShown.forEach(page -> page.setVisible(false));
            pagesShown.clear();
            Component selectedPage = tabsToPages.get(tabs.getSelectedTab());
            selectedPage.setVisible(true);
            pagesShown.add(selectedPage);
        });

        Div pages = new Div();
        pages.add(divMap.get(1));
        pages.add(divMap.get(2));
        pages.add(divMap.get(3));

        return new Div(tabs, pages);
    }

    private VerticalLayout profileForm() {
        VerticalLayout content = new VerticalLayout();
        FormLayout formLayout = new FormLayout();

        TextField nameField = new TextField("Имя");
        nameField.setValue(currentUser.getProfile().getName());
        nameField.addValueChangeListener(event ->
                enabledSaveButton(!event.getValue().equals(currentUser.getProfile().getName())));
        profileBinder.forField(nameField)
                .bind(UserProfile_.NAME);

        TextField surnameField = new TextField("Фамилия");
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
                .withValidator(this::emailIsFree, "Email занят, введите другой email")
                .bind(UserProfile_.EMAIL);

        saveChanges.addClickListener(e -> {
            updateUserProfile();
            init(); // TODO продумать обновление информации в блоке с аватаром пользователя
        });

        saveChanges.addClassNames("btn", "bg-pink", "waves-effect");
        saveChanges.getStyle().set("padding", "8px 0 25px 0");
        saveChanges.setWidthFull();

        HorizontalLayout buttons = new HorizontalLayout(saveChanges);
        buttons.getStyle().set("width", "100px");
        formLayout.add(nameField, surnameField, email);
        content.add(formLayout, buttons);

        return content;
    }

    private VerticalLayout changePasswordForm() {
        VerticalLayout content = new VerticalLayout();
        FormLayout formLayout = new FormLayout();

        TextField oldPassField = new TextField("Старый пароль");

        TextField newPassField = new TextField("Новый пароль");

        TextField confirmNewPassField = new TextField("Подтвердите новый пароль");

        Button submit = new Button("СОХРАНИТЬ");
        submit.setEnabled(false);
        submit.addClickListener(e -> {
            updateUserPassword(oldPassField.getValue(), newPassField.getValue());
            Notification.show("Пароль успешно изменён", 3000, Notification.Position.TOP_STRETCH);
            oldPassField.setValue("");
            newPassField.setValue("");
            confirmNewPassField.setValue("");
        });

        confirmNewPassField.addValueChangeListener(event ->
                submit.setEnabled(event.getValue().equals(newPassField.getValue())));

        submit.addClassNames("bg-red", "waves-effect");

        HorizontalLayout buttons = new HorizontalLayout(submit);
        formLayout.add(oldPassField, newPassField, confirmNewPassField);
        content.add(formLayout, buttons);

        return content;
    }

    private String getRolesAsString() {
        return currentUser.getRoles()
                .stream()
                .map(Role::getTitle)
                .collect(Collectors.joining(", "));
    }

    private Div workspaceDiv() {
        Div row = new Div();
        row.addClassName("row");
        row.getStyle().set("padding-top", "10px");

        workspaces.forEach(workspace -> {

            String iconType = workspace.getTeam() != null ? "people" : "person";
            String bgColor = workspace.getTeam() != null ? "bg-blue" : "bg-deep-orange";

            Div colDiv = new Div();

            colDiv.addClickListener(onClick -> getUI().ifPresent(
                    ui -> ui.navigate(MY_WORKSPACE + workspace.getId()))
            );

            colDiv.addClassNames("col-lg-3", "col-md-3", "col-sm-6", "col-xs-12");
            Div infoBox = new Div();
            infoBox.addClassNames("info-box-3", bgColor, "hover-zoom-effect");
            colDiv.add(infoBox);
            Div icon = new Div();
            icon.addClassName("icon");
            infoBox.add(icon);
            Html i = new Html("<i class=\"material-icons\">" + iconType + "</i>");
            icon.add(i);
            Div content = new Div();
            content.addClassName("content");
            infoBox.add(content);

            Div wsTypeText = new Div();
            wsTypeText.addClassName("text");
            wsTypeText.setText(workspace.getTitle());
            wsTypeText.getStyle().set("font-size", "16px");
            wsTypeText.getStyle().set("margin-top", "0");
            content.add(wsTypeText);

            Div wsText = new Div();
            wsText.addClassName("text");
            wsText.setText("Активных задач");
            wsText.getStyle().set("margin-top", "0");
            content.add(wsText);

            Div tasksCount = new Div();
            tasksCount.addClassName("number");
            tasksCount.setText(String.valueOf(workspace.getTasks().size()));
            content.add(tasksCount);
            row.add(colDiv);
        });

        return row;
    }

    private void updateUserProfile() {
        if (binder.writeBeanIfValid(currentUser) &&
                profileBinder.writeBeanIfValid(currentUser.getProfile())) {
            userService.saveUserAvatar(currentUser, buffer);
            userService.save(currentUser);
            Notification.show("Изменения успешно сохранены", 3000, Notification.Position.TOP_STRETCH);
            reload();
        }
    }

    private void updateUserPassword(String oldPass, String newPass) {
        if (userService.matchesPasswords(oldPass, currentUser.getPasswordHash())) {
            userService.changePassword(currentUser.getId(), newPass);
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

    private Upload initUpload() {
        Upload upload = new Upload(buffer);
        upload.setId("i18n-upload");
        UploadI18N i18n = new UploadI18N();
        i18n.setDropFiles(
                new UploadI18N.DropFiles()
                        .setOne("Или перетащи его сюда...")
                        .setMany("Или перетащи их сюда..."))
                .setAddFiles(new UploadI18N.AddFiles()
                        .setOne("Выбрать файл")
                        .setMany("Добавить файлы"))
                .setCancel("Отменить")
                .setError(new UploadI18N.Error()
                        .setTooManyFiles("Слишком много файлов.")
                        .setFileIsTooBig("Слишком большой файл.")
                        .setIncorrectFileType("Некорректный тип файла."))
                .setUploading(new UploadI18N.Uploading()
                        .setStatus(new UploadI18N.Uploading.Status()
                                .setConnecting("Соединение...")
                                .setStalled("Загрузка застопорилась.")
                                .setProcessing("Обработка файла..."))
                        .setRemainingTime(
                                new UploadI18N.Uploading.RemainingTime()
                                        .setPrefix("оставшееся время: ")
                                        .setUnknown(
                                                "оставшееся время неизвестно"))
                        .setError(new UploadI18N.Uploading.Error()
                                .setServerUnavailable("Сервер недоступен")
                                .setUnexpectedServerError(
                                        "Неожиданная ошибка сервера")
                                .setForbidden("Загрузка запрещена")))
                .setUnits(Stream
                        .of("Б", "Кбайт", "Мбайт", "Гбайт", "Тбайт", "Пбайт",
                                "Эбайт", "Збайт", "Ибайт")
                        .collect(Collectors.toList()));

        upload.setI18n(i18n);
        return upload;
    }

    private List<Team> getMyTeams() {
        return teamService.findByMember(Collections.singletonList(currentUser));
    }

    private void showDialog(final OperationEnum operation, final Team team) {
        TeamForm teamForm = new TeamForm(userService, teamService, team, operation);
        this.teamForm = teamForm;
        teamForm.addOpenedChangeListener(event -> reload(!event.isOpened(), !this.teamForm.isCanceled()));
        teamForm.open();
    }

    private void reload(final boolean isClosed, final boolean isNotCanceled) {
        if (isClosed && isNotCanceled) init();
    }

}
