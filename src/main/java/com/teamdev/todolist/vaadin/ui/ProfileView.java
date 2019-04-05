package com.teamdev.todolist.vaadin.ui;

import com.github.appreciated.card.RippleClickableCard;
import com.github.appreciated.card.label.PrimaryLabel;
import com.github.appreciated.card.label.SecondaryLabel;
import com.github.appreciated.card.label.TitleLabel;
import com.teamdev.todolist.configuration.security.SecurityUtils;
import com.teamdev.todolist.entity.User;
import com.teamdev.todolist.entity.UserProfile;
import com.teamdev.todolist.entity.UserProfile_;
import com.teamdev.todolist.entity.Workspace;
import com.teamdev.todolist.service.UserService;
import com.teamdev.todolist.service.WorkspaceService;
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
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.UploadI18N;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.material.Material;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.teamdev.todolist.configuration.support.Constants.*;

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
    private final WorkspaceService workspaceService;
    private List<Workspace> workspaces;
    private MemoryBuffer buffer;
    private Upload uploadAvatar;

    public ProfileView(UserService userService, WorkspaceService workspaceService) {
        super(userService);
        this.buffer = new MemoryBuffer();
        this.userService = userService;
        this.workspaceService = workspaceService;
        this.currentUser = userService.findByLogin(SecurityUtils.getUsername());
        this.binder = new BeanValidationBinder<>(User.class);
        this.profileBinder = new BeanValidationBinder<>(UserProfile.class);
        this.saveChanges = new Button("Save changes");
        init();
    }

    private void init() {
        HorizontalLayout content = new HorizontalLayout();
        VerticalLayout leftContent = new VerticalLayout();
        VerticalLayout rightContent = new VerticalLayout();
        workspaces = workspaceService.getMyWorkspaces(SecurityUtils.getUsername());
        saveChanges.setEnabled(false);
        Span message = new Span("Привет, " + currentUser.getProfile().getName() + "!");
        message.getStyle()
                .set("font-size", "30px");
        Div welcome = new Div(message);
        Div avatar = createAvatarDiv();
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
            leftContent.remove(avatar);
            leftContent.addComponentAtIndex(1, createAvatarDiv());
            pwdField.setValue("");
        });

        Button cancel = new Button("Cancel", e -> {
            binder.readBean(currentUser);
            profileBinder.readBean(currentUser.getProfile());
        });

        formLayout.getStyle()
                .set("width", "50%")
                .set("position", "relative")
                .set("left", "25%");
        formLayout.add(nameField, surnameField, email, pwdField);

        HorizontalLayout buttons = new HorizontalLayout(saveChanges, cancel);

        leftContent.add(welcome, avatar, formLayout, buttons);
        leftContent.setAlignItems(FlexComponent.Alignment.CENTER);
        leftContent.setSpacing(true);
        leftContent.setWidth("50%");

        rightContent.setSpacing(true);
        rightContent.add(workSpacesDiv());
        rightContent.setWidth("50%");
        content.add(leftContent, rightContent);
        setContent(content);
    }

    private void updateUserProfile() {
        if (binder.writeBeanIfValid(currentUser) &&
                profileBinder.writeBeanIfValid(currentUser.getProfile())) {
            userService.saveUserAvatar(currentUser, buffer);
            userService.save(currentUser);
            Notification.show("Changes have been saved successfully", 3000, Notification.Position.TOP_STRETCH);
            reload();
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

    private Div createAvatarDiv() {
        Div content = new Div();
        content.getStyle()
                .set("display", "flex")
                .set("justify-content", "center")
                .set("align-items", "center")
                .set("flex-direction", "column");
        content.setWidth("300px");
        content.setHeight("300px");
        Div avatar = new Div();
        avatar.setWidth("150px");
        avatar.setHeight("150px");
        avatar.getStyle().set("text-align", "center");
        Image userAvatar = VaadinViewUtils.getUserAvatar(currentUser, false);
        avatar.add(userAvatar);

        Div uploadDiv = new Div();
        uploadDiv.setWidth("300px");
        uploadDiv.setHeight("150px");
        uploadDiv.getStyle().set("text-align", "center");
        uploadAvatar = initUpload();
        uploadAvatar.addSucceededListener(e -> enabledSaveButton(true));
        uploadDiv.add(uploadAvatar);
        content.add(avatar, uploadDiv);
        return content;
    }

    private Div workSpacesDiv() {
        Div content = new Div();
        content.getStyle()
                .set("display", "flex")
                .set("flex-direction", "row");
        workspaces.forEach(workSpace -> {
            Div cardItem = new Div();
            cardItem.getStyle().set("border", "1px solid black");
            cardItem.getStyle().set("border-radius", "5px");
            cardItem.add(createCard(workSpace));
            cardItem.getStyle().set("margin", "5px");
            content.add(cardItem);
        });
        return content;
    }

    private RippleClickableCard createCard(Workspace workSpace) {
        RippleClickableCard card = new RippleClickableCard(
                onClick -> {
                    String workSpaceId = workSpace.getId().toString();
                    getUI().ifPresent(ui -> ui.navigate(WORKSPACES_PAGE + PATH_SEPARATOR + workSpaceId));
                },
                new TitleLabel(workSpace.getTitle()),
                new PrimaryLabel(workSpace.getTeam() != null ? workSpace.getTeam().getTitle() : "Личное"),
                new SecondaryLabel("Кол-во задач: " + workSpace.getTasks().size())
        );
        return card;
    }

}
