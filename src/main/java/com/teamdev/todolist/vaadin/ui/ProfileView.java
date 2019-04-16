package com.teamdev.todolist.vaadin.ui;

import com.teamdev.todolist.configuration.security.SecurityUtils;
import com.teamdev.todolist.configuration.support.OperationEnum;
import com.teamdev.todolist.entity.*;
import com.teamdev.todolist.service.TeamService;
import com.teamdev.todolist.service.UserService;
import com.teamdev.todolist.service.WorkspaceService;
import com.teamdev.todolist.vaadin.custom.CustomAppLayout;
import com.teamdev.todolist.vaadin.form.TeamForm;
import com.teamdev.todolist.vaadin.form.WorkspaceForm;
import com.teamdev.todolist.vaadin.support.VaadinViewUtils;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.HtmlImport;
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

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.teamdev.todolist.configuration.support.Constants.*;

/**
 * @author stegnin
 */

@PageTitle("ПРОФИЛЬ")
@HtmlImport("../VAADIN/form-elements-style.html")
@Route(value = PROFILE_PAGE, layout = MainLayout.class)
public class ProfileView extends CustomAppLayout {
    // TODO добавить удаление РО
    private final String MY_WORKSPACE = WORKSPACES_PAGE + PATH_SEPARATOR + SecurityUtils.getUsername() + PATH_SEPARATOR;

    private final UserService userService;
    private final TeamService teamService;
    private final User currentUser;
    private final Binder<User> binder; // отвечает за привязку данных с полей формы
    private final Binder<UserProfile> profileBinder;
    private final Button saveChanges;
    private final WorkspaceService workspaceService;
    private WorkspaceForm workspaceForm;
    private TeamForm teamForm;
    private Set<Workspace> workspaces;
    private Set<Team> teams;
    private MemoryBuffer buffer;
    private Upload uploadAvatar;
    private AtomicInteger totalTasks;
    private AtomicInteger completedTasks;
    private AtomicInteger activeTasks;
    private AtomicInteger expiredTasks;

    public ProfileView(UserService userService, WorkspaceService workspaceService, TeamService teamService) {
        super(userService);
        this.buffer = new MemoryBuffer();
        this.teamService = teamService;
        this.userService = userService;
        this.workspaceService = workspaceService;
        this.currentUser = userService.findByLogin(SecurityUtils.getUsername());
        this.binder = new BeanValidationBinder<>(User.class);
        this.profileBinder = new BeanValidationBinder<>(UserProfile.class);
        this.saveChanges = VaadinViewUtils.createButton("СОХРАНИТЬ", "", "submit", "8px 12px 21px 8px");
        this.totalTasks = new AtomicInteger(0);
        this.completedTasks = new AtomicInteger(0);
        this.activeTasks = new AtomicInteger(0);
        this.expiredTasks = new AtomicInteger(0);
        init();
    }

    private void init() {
        saveChanges.setEnabled(false);
        workspaces = new HashSet<>(workspaceService.getMyWorkspaces(SecurityUtils.getUsername()));
        teams = getMyTeams();
        teams.forEach(team -> workspaces.addAll(workspaceService.findByTeam(team)));
        totalTasks.set(0);
        completedTasks.set(0);
        activeTasks.set(0);
        expiredTasks.set(0);
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
        calculateTasks();
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
        Html h3 = new Html("<h3>" + getUserFullName() + "</h3>");
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
                "<span>ВСЕГО ЗАДАЧ</span>" +
                "<span class=\"badge bg-blue\">" + totalTasks + "</span>" +
                "</li>" +
                "<li>" +
                "<span>РЕШЁННЫХ</span>" +
                "<span class=\"badge bg-green\">" + completedTasks + "</span>" +
                "</li>" +
                "<li>" +
                "<span>АКТИВНЫХ</span>" +
                "<span class=\"badge bg-amber\">" + activeTasks + "</span>" +
                "</li>" +
                "<li>" +
                "<span>ПРОСРОЧЕННЫХ</span>" +
                "<span class=\"badge bg-red\">" + expiredTasks + "</span>" +
                "</li>" +
                "</ul>");

        uploadAvatar = initUpload();
        profileFooter.add(ul);
        profileFooter.add(uploadAvatar);
        profileCard.add(profileFooter);
        return cols;
    }

    private String getUserFullName() {
        return (currentUser.getProfile().getName() + " " + currentUser.getProfile().getSurname()).toUpperCase();
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

        Div myTeamsPage = new Div();
        myTeamsPage.add(teamDiv());
        myTeamsPage.setVisible(false);

        Div profilePage = new Div();
        profilePage.add(profileForm());
        profilePage.setVisible(false);

        Div changePassPage = new Div();
        changePassPage.add(changePasswordForm());
        changePassPage.setVisible(false);

        divMap.put(1, myWorkspacesPage);
        divMap.put(2, myTeamsPage);
        divMap.put(3, profilePage);
        divMap.put(4, changePassPage);

        return divMap;
    }

    private Div tabs(Map<Integer, Div> divMap) {
        Tab myWorkspaces = new Tab("РАБОЧИЕ ОБЛАСТИ");
        myWorkspaces.getStyle().set("color", "black");

        Tab myTeams = new Tab("КОМАНДЫ");
        myTeams.getStyle().set("color", "black");

        Tab profileSettings = new Tab("НАСТРОЙКИ ПРОФИЛЯ");
        profileSettings.getStyle().set("color", "black");

        Tab changePassword = new Tab("ИЗМЕНИТЬ ПАРОЛЬ");
        changePassword.getStyle().set("color", "black");

        Map<Tab, Component> tabsToPages = new HashMap<>();

        tabsToPages.put(myWorkspaces, divMap.get(1));
        tabsToPages.put(myTeams, divMap.get(2));
        tabsToPages.put(profileSettings, divMap.get(3));
        tabsToPages.put(changePassword, divMap.get(4));
        Tabs tabs = new Tabs(myWorkspaces, myTeams, profileSettings, changePassword);
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
        pages.add(divMap.get(4));

        return new Div(tabs, pages);
    }

    private VerticalLayout profileForm() {
        VerticalLayout content = new VerticalLayout();
        FormLayout formLayout = new FormLayout();

        TextField nameField = new TextField("ИМЯ");
        nameField.setValue(currentUser.getProfile().getName());
        nameField.addValueChangeListener(event ->
                enabledSaveButton(!event.getValue().equals(currentUser.getProfile().getName())));
        profileBinder.forField(nameField)
                .bind(UserProfile_.NAME);

        TextField surnameField = new TextField("ФАМИЛИЯ");
        surnameField.setValue(currentUser.getProfile().getSurname());
        surnameField.addValueChangeListener(event ->
                enabledSaveButton(!event.getValue().equals(currentUser.getProfile().getSurname())));
        profileBinder.forField(surnameField)
                .bind(UserProfile_.SURNAME);

        TextField email = new TextField("EMAIL");
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

        TextField oldPassField = new TextField("СТАРЫЙ ПАРОЛЬ");

        TextField newPassField = new TextField("НОВЫЙ ПАРОЛЬ");

        TextField confirmNewPassField = new TextField("ПОДТВЕРДИТЕ НОВЫЙ ПАРОЛЬ");

        Button submit = VaadinViewUtils.createButton("СОХРАНИТЬ", "", "submit", "8px 12px 21px 8px");
        submit.setEnabled(false);
        submit.addClickListener(e -> {
            updateUserPassword(oldPassField.getValue(), newPassField.getValue());
            Notification.show("ПАРОЛЬ УСПЕШНО ИЗМЕНЁН", 3000, Notification.Position.TOP_STRETCH);
            oldPassField.setValue("");
            newPassField.setValue("");
            confirmNewPassField.setValue("");
        });

        confirmNewPassField.addValueChangeListener(event ->
                submit.setEnabled(event.getValue().equals(newPassField.getValue())));

        submit.setWidthFull();

        HorizontalLayout buttons = new HorizontalLayout(submit);
        buttons.setWidth("100px");
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
        row.addClassNames("row", "animated", "fadeInRight");
        row.getStyle().set("padding-top", "10px");

        workspaces.forEach(workspace -> {

            String iconType = workspace.getTeam() != null ? "people" : "person";
            String bgColor = workspace.getTeam() != null ? "bg-blue" : "bg-deep-orange";

            Div colDiv = new Div();
            if (workspace.getOwner().getId().compareTo(currentUser.getId()) == 0) {
                Button edit = new Button();
                Html i = new Html("<i class=\"material-icons\" style=\"\n" +
                        "top: 2px;\n" +
                        "\">settings</i>");
                edit.setIcon(i);
                edit.addClassNames("btn", "btn-xs", "bg-grey", "waves-effect");
                edit.addClickListener(event -> showWorkspaceForm(OperationEnum.UPDATE, workspace));
                edit.getStyle()
                        .set("position", "absolute")
                        .set("z-index", "1")
                        .set("right", "0")
                        .set("bottom", "0")
                        .set("margin", "0 15px 30px 0")
                        .set("box-shadow", "none")
                        .set("border-radius", "0");
                colDiv.add(edit);
            }
            colDiv.addClassNames("col-lg-3", "col-md-3", "col-sm-6", "col-xs-12");
            Div infoBox = new Div();
            infoBox.addClickListener(onClick -> getUI().ifPresent(
                    ui -> ui.navigate(MY_WORKSPACE + workspace.getId())));
            infoBox.getStyle().set("cursor", "pointer");
            infoBox.addClassNames("info-box-3", bgColor, "hover-zoom-effect");
            colDiv.add(infoBox);
            Div icon = new Div();
            icon.addClassName("icon");
            infoBox.add(icon);
            Html ic = new Html("<i class=\"material-icons\">" + iconType + "</i>");
            icon.add(ic);
            Div content = new Div();
            content.addClassName("content");
            infoBox.add(content);

            Div wsTypeText = new Div();
            wsTypeText.addClassName("text");
            wsTypeText.setText(workspace.getTitle().toUpperCase());
            wsTypeText.getStyle().set("font-size", "16px");
            wsTypeText.getStyle().set("margin-top", "0");
            content.add(wsTypeText);

            Div wsText = new Div();
            wsText.addClassName("text");
            wsText.setText("АКТИВНЫХ ЗАДАЧ");
            wsText.getStyle().set("margin-top", "0");
            content.add(wsText);

            Div tasksCount = new Div();
            tasksCount.addClassName("number");
            tasksCount.setText(String.valueOf(workspace.getTasks().size()));
            content.add(tasksCount);
            row.add(colDiv);
        });
        row.add(addNewWorkspaceDiv());
        return row;
    }

    private Div teamDiv() {
        Div row = new Div();
        row.addClassNames("row", "animated", "fadeInRight");
        row.getStyle().set("padding-top", "10px");

        teams.forEach(team -> {

            String iconType = "people";
            String bgColor = "bg-blue";

            Div colDiv = new Div();

            colDiv.addClassNames("col-lg-3", "col-md-3", "col-sm-6", "col-xs-12");
            Div infoBox = new Div();
            infoBox.addClickListener(onClick -> showTeamForm(OperationEnum.UPDATE, team));
            infoBox.getStyle().set("cursor", "pointer");
            infoBox.addClassNames("info-box-2", bgColor, "hover-zoom-effect");
            colDiv.add(infoBox);
            Div icon = new Div();
            icon.addClassName("icon");
            infoBox.add(icon);
            Html ic = new Html("<i class=\"material-icons\">" + iconType + "</i>");
            icon.add(ic);
            Div content = new Div();
            content.addClassName("content");
            infoBox.add(content);

            Div teamTypeText = new Div();
            teamTypeText.addClassName("text");
            teamTypeText.setText(team.getTitle().toUpperCase());
            teamTypeText.getStyle().set("font-size", "16px");
            teamTypeText.getStyle().set("margin-top", "0");
            content.add(teamTypeText);

            Div teamText = new Div();
            teamText.addClassName("text");
            teamText.setText("УЧАСТНИКОВ");
            teamText.getStyle().set("margin-top", "0");
            content.add(teamText);

            Div membersCount = new Div();
            membersCount.addClassName("number");
            membersCount.setText(String.valueOf(team.getMembers().size()));
            content.add(membersCount);
            row.add(colDiv);
        });
        row.add(addNewTeamDiv());
        return row;
    }

    private Div addNewWorkspaceDiv() {

        String iconType = "add";
        String bgColor = "bg-green";

        Div colDiv = new Div();

        colDiv.addClickListener(onClick -> getUI().ifPresent(ui -> showWorkspaceForm(OperationEnum.CREATE, new Workspace())));

        colDiv.addClassNames("col-lg-3", "col-md-3", "col-sm-6", "col-xs-12");
        Div infoBox = new Div();
        infoBox.getStyle().set("cursor", "pointer");
        infoBox.addClassNames("info-box-2", bgColor, "hover-zoom-effect");
        colDiv.add(infoBox);
        Div icon = new Div();
        icon.addClassName("icon");
        infoBox.add(icon);
        Html i = new Html("<i class=\"material-icons\">" + iconType + "</i>");
        icon.add(i);
        Div content = new Div();
        content.addClassName("content");
        content.getStyle()
                .set("display", "flex")
                .set("align-items", "center");
        infoBox.add(content);

        Div wsTypeText = new Div();
        wsTypeText.addClassName("text");
        wsTypeText.setText("ДОБАВИТЬ РАБОЧУЮ ОБЛАСТЬ");
        wsTypeText.getStyle().set("font-size", "16px");
        wsTypeText.getStyle().set("margin", "0");
        content.add(wsTypeText);

        return colDiv;
    }

    private Div addNewTeamDiv() {

        String iconType = "add";
        String bgColor = "bg-green";

        Div colDiv = new Div();

        colDiv.addClickListener(onClick -> getUI().ifPresent(ui -> showTeamForm(OperationEnum.CREATE, new Team())));

        colDiv.addClassNames("col-lg-3", "col-md-3", "col-sm-6", "col-xs-12");
        Div infoBox = new Div();
        infoBox.getStyle().set("cursor", "pointer");
        infoBox.addClassNames("info-box-2", bgColor, "hover-zoom-effect");
        colDiv.add(infoBox);
        Div icon = new Div();
        icon.addClassName("icon");
        infoBox.add(icon);
        Html i = new Html("<i class=\"material-icons\">" + iconType + "</i>");
        icon.add(i);
        Div content = new Div();
        content.addClassName("content");
        content.getStyle()
                .set("display", "flex")
                .set("align-items", "center");
        infoBox.add(content);

        Div wsTypeText = new Div();
        wsTypeText.addClassName("text");
        wsTypeText.setText("ДОБАВИТЬ КОМАНДУ");
        wsTypeText.getStyle().set("font-size", "16px");
        wsTypeText.getStyle().set("margin", "0");
        content.add(wsTypeText);

        return colDiv;
    }

    private void updateUserProfile() {
        if (binder.writeBeanIfValid(currentUser) &&
                profileBinder.writeBeanIfValid(currentUser.getProfile())) {
            userService.saveUserAvatar(currentUser, buffer);
            userService.save(currentUser);
            Notification.show("ИЗМЕНЕНИЯ УСПЕШНО СОХРАНЕНЫ", 3000, Notification.Position.TOP_STRETCH);
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
                        .setOne("ИЛИ ПЕРЕТАЩИ ЕГО СЮДА...")
                        .setMany("ИЛИ ПЕРЕТАЩИ ИХ СЮДА..."))
                .setAddFiles(new UploadI18N.AddFiles()
                        .setOne("ВЫБЕРИ ФАЙЛ")
                        .setMany("ДОБАВЬ ФАЙЛ"))
                .setCancel("ОМЕНИТЬ")
                .setError(new UploadI18N.Error()
                        .setTooManyFiles("СЛИШКОМ МНОГО ФАЙЛОВ.")
                        .setFileIsTooBig("СЛИШКОМ БОЛЬШОЙ ФАЙЛ.")
                        .setIncorrectFileType("НЕКОРРЕКТНЫЙ ТИП ФАЙЛА."))
                .setUploading(new UploadI18N.Uploading()
                        .setStatus(new UploadI18N.Uploading.Status()
                                .setConnecting("СОЕДИНЕНИЕ...")
                                .setStalled("ЗАГРУЗКА ЗАСТОПОРИЛАСЬ.")
                                .setProcessing("ОБРАБОТКА ФАЙЛА..."))
                        .setRemainingTime(
                                new UploadI18N.Uploading.RemainingTime()
                                        .setPrefix("ОСТАВШЕЕСЯ ВРЕМЯ: ")
                                        .setUnknown(
                                                "ОСТАВШЕЕСЯ ВРЕМЯ НЕИЗВЕСТНО"))
                        .setError(new UploadI18N.Uploading.Error()
                                .setServerUnavailable("СЕРВЕР НЕДОСТУПЕН")
                                .setUnexpectedServerError(
                                        "НЕОЖИДАННАЯ ОШИБКА СЕРВЕРА")
                                .setForbidden("ЗАГРУЗКА ЗАПРЕЩЕНА")))
                .setUnits(Stream
                        .of("Б", "КБАЙТ", "МБАЙТ", "ГБАЙТ", "ТБАЙТ", "ПБАЙТ",
                                "ЭБАЙТ", "ЗБАЙТ", "ИБАЙТ")
                        .collect(Collectors.toList()));

        upload.setI18n(i18n);
        upload.addSucceededListener(e -> enabledSaveButton(true));
        upload.setAcceptedFileTypes("image/jpeg", "image/png", "image/gif");
        return upload;
    }

    private Set<Team> getMyTeams() {
        return new HashSet<>(teamService.findByMember(Collections.singletonList(currentUser)));
    }

    private void showTeamForm(final OperationEnum operation, final Team team) {
        TeamForm teamForm = new TeamForm(userService, teamService, team, operation);
        this.teamForm = teamForm;
        teamForm.addOpenedChangeListener(event -> reload(!event.isOpened(), !this.teamForm.isCanceled()));
        teamForm.open();
    }

    private void showWorkspaceForm(final OperationEnum operation, final Workspace workspace) {
        WorkspaceForm workspaceForm = new WorkspaceForm(workspaceService, workspace, teamService, operation, currentUser);
        this.workspaceForm = workspaceForm;
        workspaceForm.addOpenedChangeListener(event -> reload(!event.isOpened(), !this.workspaceForm.isCanceled()));
        workspaceForm.open();
    }

    private void reload(final boolean isClosed, final boolean isNotCanceled) {
        if (isClosed && isNotCanceled) init();
    }

    private void calculateTasks() {
        workspaces.forEach(workspace -> workspace.getTasks().forEach(task -> {
            if (task.getAuthor().getId().compareTo(currentUser.getId()) == 0 ||
                    task.getPerformers()
                            .stream()
                            .anyMatch(user -> user.getId().compareTo(currentUser.getId()) == 0)) {
                totalTasks.incrementAndGet();
                switch (task.getStatus().getTitle()) {
                    case TASK_STATUS_COMPLETED:
                        completedTasks.incrementAndGet();
                        break;
                    case TASK_STATUS_NEW:
                    case TASK_STATUS_IN_PROGRESS:
                        activeTasks.incrementAndGet();
                        break;
                }
                if (!task.getStatus().getTitle().equalsIgnoreCase(TASK_STATUS_COMPLETED) &&
                        task.getExecutionDate().toLocalDate().isBefore(LocalDate.now())) {
                    expiredTasks.incrementAndGet();
                }
            }
        }));
    }

}
