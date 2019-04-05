package com.teamdev.todolist.vaadin.ui;

import com.github.appreciated.card.RippleClickableCard;
import com.github.appreciated.card.action.ActionButton;
import com.github.appreciated.card.content.HorizontalCardComponentContainer;
import com.github.appreciated.card.label.PrimaryLabel;
import com.github.appreciated.card.label.SecondaryLabel;
import com.github.appreciated.card.label.TitleLabel;
import com.teamdev.todolist.configuration.security.SecurityUtils;
import com.teamdev.todolist.configuration.support.OperationEnum;
import com.teamdev.todolist.entity.Task;
import com.teamdev.todolist.entity.Workspace;
import com.teamdev.todolist.service.TeamService;
import com.teamdev.todolist.service.UserService;
import com.teamdev.todolist.service.WorkspaceService;
import com.teamdev.todolist.vaadin.custom.CustomAppLayout;
import com.teamdev.todolist.vaadin.form.WorkspaceForm;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.router.*;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.material.Material;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.teamdev.todolist.configuration.support.Constants.*;

/**
 * @author Alexandr Stegnin
 */

@Route(WORKSPACES_PAGE)
@PageTitle("My workspaces")
@Theme(value = Material.class, variant = Material.LIGHT)
public class MyWorkspacesView extends CustomAppLayout implements HasUrlParameter<String> {

    private String userLogin;
    private final WorkspaceService workspaceService;
    private final TeamService teamService;
    private List<Workspace> workspaces;
    private WorkspaceForm workspaceForm;

    public MyWorkspacesView(UserService userService, WorkspaceService workspaceService, TeamService teamService) {
        super(userService);
        this.workspaceService = workspaceService;
        this.teamService = teamService;
    }

    private void init() {
        this.workspaces = workspaceService.getMyWorkspaces(userLogin);
        HorizontalCardComponentContainer cardContainer = new HorizontalCardComponentContainer();
        cardContainer.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        cardContainer.getStyle().set("flex-wrap", "wrap");
        cardContainer.getStyle().set("display", "flex");
        cardContainer.setSpacing(false);
        cardContainer.setPadding(true);
        workspaces.forEach(workspace -> {
            RippleClickableCard card = createCard(workspace);
            cardContainer.add(card);
        });
        cardContainer.add(createAddNewCard());
        setContent(cardContainer);
    }

    private RippleClickableCard createCard(Workspace workspace) {
        Icon trashIcon = VaadinIcon.TRASH.create();
        trashIcon.getStyle()
                .set("display", "inline-block")
                .set("margin-bottom", "2px")
                .set("margin-left", "2px");
        ActionButton deleteBtn = new ActionButton("Удалить", trashIcon,
                e -> showDialog(OperationEnum.DELETE, workspace));
        deleteBtn.setIconAfterText(true);
        deleteBtn.getStyle()
                .set("position", "absolute")
                .set("right", "0")
                .set("bottom", "0")
                .set("color", "red");
        TitleLabel titleLabel = new TitleLabel(workspace.getTitle());
        titleLabel.setFlexGrow(1);

        PrimaryLabel teamLabel = new PrimaryLabel(workspace.getTeam() == null ? "Личное" : workspace.getTeam().getTitle());

        SecondaryLabel taskCountLabel = new SecondaryLabel(String.format("Кол-во активных задач: %d", activeTasksCount(workspace.getTasks())));

        RippleClickableCard card = new RippleClickableCard(
                onClick -> getUI().ifPresent(ui -> ui.navigate(
                        WORKSPACES_PAGE + PATH_SEPARATOR + SecurityUtils.getUsername() + PATH_SEPARATOR + workspace.getId())),
                // if you don't want the title to wrap you can set the whitespace = nowrap
                titleLabel,
                teamLabel,
                taskCountLabel,
                deleteBtn
        );
        stylizeCard(card);
        return card;
    }

    private RippleClickableCard createAddNewCard() {
        Image plusImg = new Image("images/plus.png", "Добавить рабочую область");
        plusImg.setMaxWidth("250px");
        plusImg.setMaxHeight("140px");
        RippleClickableCard card = new RippleClickableCard(
                onClick -> showDialog(OperationEnum.CREATE, new Workspace()),
                new PrimaryLabel("Добавить рабочую область"),
                plusImg
        );
        card.getChildren().findFirst().ifPresent(component -> component.getElement().getStyle().set("align-items", "center"));
        stylizeCard(card);
        return card;
    }

    private void stylizeCard(RippleClickableCard card) {
        card.setFlexGrow(1);
        card.setWidth("250px");
        card.setHeight("200px");
        card.getStyle().set("margin", "10px");
        card.getStyle().set("border", "1px solid black");
        card.getStyle().set("border-radius", "5px");
    }

    private Long activeTasksCount(Set<Task> tasks) {
        return tasks.stream()
                .filter(task -> !task.getStatus().getTitle().equalsIgnoreCase(TASK_STATUS_COMPLETED))
                .count();
    }

    private void showDialog(final OperationEnum operation, final Workspace workspace) {
        WorkspaceForm workspaceForm = new WorkspaceForm(workspaceService, workspace, teamService, operation, getCurrentDbUser());
        this.workspaceForm = workspaceForm;
        workspaceForm.addOpenedChangeListener(event -> reload(!event.isOpened(), !workspaceForm.isCanceled()));
        workspaceForm.open();
    }

    private void reload(final boolean isClosed, final boolean isNotCanceled) {
        if (isClosed && isNotCanceled) init();
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, String s) {
        Location location = beforeEvent.getLocation();
        userLogin = location.getSegments().get(1);
        if (Objects.equals(SecurityUtils.getUsername(), userLogin)) {
            init();
        }
    }

}
