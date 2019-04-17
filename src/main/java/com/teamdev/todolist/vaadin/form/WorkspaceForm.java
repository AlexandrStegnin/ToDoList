package com.teamdev.todolist.vaadin.form;

import com.teamdev.todolist.command.Command;
import com.teamdev.todolist.command.workspace.CreateWorkspaceCommand;
import com.teamdev.todolist.command.workspace.DeleteWorkspaceCommand;
import com.teamdev.todolist.command.workspace.UpdateWorkspaceCommand;
import com.teamdev.todolist.configuration.support.OperationEnum;
import com.teamdev.todolist.entity.Team;
import com.teamdev.todolist.entity.User;
import com.teamdev.todolist.entity.Workspace;
import com.teamdev.todolist.entity.Workspace_;
import com.teamdev.todolist.service.TeamService;
import com.teamdev.todolist.service.WorkspaceService;
import com.teamdev.todolist.vaadin.support.VaadinViewUtils;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author Alexandr Stegnin
 */
public class WorkspaceForm extends Dialog {

    private static final String PRIVATE_WS = "ЛИЧНАЯ";
    private static final String TEAM_WS = "КОМАНДНАЯ";

    private final WorkspaceService workspaceService;
    private final TeamService teamService;

    private final TextField title;
    private final Button cancel;
    private final OperationEnum operation;
    private final HorizontalLayout buttons;
    private final VerticalLayout content;
    private Button submit;
    private final User owner;

    private Select<Team> team;
    private Select<String> privateOrTeam;
    private Workspace workspace;

    private Binder<Workspace> workspaceBinder;

    private boolean canceled = false;

    public WorkspaceForm(WorkspaceService workspaceService, Workspace workspace, TeamService teamService,
                         OperationEnum operation, User owner) {
        this.workspaceBinder = new BeanValidationBinder<>(Workspace.class);
        this.workspaceService = workspaceService;
        this.teamService = teamService;
        this.workspace = workspace;
        this.title = new TextField("НАЗВАНИЕ");
        this.team = new Select<>();
        this.privateOrTeam = new Select<>();
        this.buttons = new HorizontalLayout();
        this.content = new VerticalLayout();
        this.cancel = VaadinViewUtils.createButton("ОТМЕНИТЬ", "", "cancel", "8px 10px 21px 8px");
        this.submit = VaadinViewUtils.createButton(
                operation.name.toUpperCase(), "", "submit", "8px 10px 21px 8px");
        this.operation = operation;
        this.owner = owner;
        init();
    }

    private void init() {
        prepareButtons(operation);
        workspace.setOwner(owner);
        team.setItems(getMyTeams());
        team.setTextRenderer(Team::getTitle);
        team.setEmptySelectionAllowed(true);
        team.setEmptySelectionCaption("ВЫБЕРИТЕ КОМАНДУ");
        team.setVisible(workspace.getTeam() != null);
        stylizeForm();

        privateOrTeam.setEmptySelectionAllowed(false);
        privateOrTeam.setItems(PRIVATE_WS, TEAM_WS);
        privateOrTeam.setValue(workspace.getTeam() == null ? PRIVATE_WS : TEAM_WS);
        privateOrTeam.setPlaceholder("ВИД РАБОЧЕЙ ОБЛАСТИ");
        privateOrTeam.addValueChangeListener(event -> {
            if (PRIVATE_WS.equalsIgnoreCase(privateOrTeam.getValue())) {
                team.setVisible(false);
                workspace.setTeam(null);
                setHeight("150px");
            } else {
                team.setVisible(true);
                workspaceBinder.forField(team)
                        .withValidator(t -> !team.isVisible() || !Objects.equals(null, t),
                                "ДЛЯ КОМАНДНОЙ РАБОЧЕЙ ОБЛАСТИ НАДО ВЫБРАТЬ КОМАНДУ")
                        .bind(Workspace_.TEAM);
                setHeight("220px");
            }
        });
        Div alert = new Div();
        alert.setText("ВНИМАНИЕ! ВСЕ СВЯЗАННЫЕ ЗАДАЧИ БУДУТ БЕЗВОЗВРАТНО УДАЛЕНЫ!");
        alert.getStyle()
                .set("color", "red")
                .set("text-align", "center")
                .set("font-size", "14px");
        buttons.add(submit, cancel);
        if (operation.compareTo(OperationEnum.DELETE) == 0) {
            setHeight("200px");
            content.add(alert, title, privateOrTeam, team, buttons);
        } else {
            setHeight("150px");
            content.add(title, privateOrTeam, team, buttons);
        }
        add(content);
        workspaceBinder.setBean(workspace);
        workspaceBinder.bindInstanceFields(this);
    }

    private Set<Team> getMyTeams() {
        return new HashSet<>(teamService.findByMember(Collections.singletonList(owner)));
    }

    private void prepareButtons(OperationEnum operation) {
        switch (operation) {
            case CREATE:
                submit.addClickListener(e -> executeCommand(new CreateWorkspaceCommand(workspaceService, workspace)));
                break;
            case UPDATE:
                submit.addClickListener(e -> executeCommand(new UpdateWorkspaceCommand(workspaceService, workspace)));
                break;
            case DELETE:
                submit.addClickListener(e -> executeCommand(new DeleteWorkspaceCommand(workspaceService, workspace)));
                break;
        }
        cancel.addClickListener(e -> {
            this.canceled = true;
            this.close();
        });
    }

    private void stylizeForm() {
        setWidth("400px");
        setHeight(team.isVisible() ? "220px" : "150px");
        title.setPlaceholder("ВВЕДИТЕ НАЗВАНИЕ");
        title.setRequiredIndicatorVisible(true);
        title.setWidthFull();

        privateOrTeam.setWidthFull();

        team.setWidthFull();

        buttons.setWidthFull();
        buttons.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        content.setHeightFull();
    }

    private void executeCommand(Command command) {
        if (command instanceof DeleteWorkspaceCommand) {
            command.execute();
            this.close();
        } else if (workspaceBinder.writeBeanIfValid(workspace)) {
            command.execute();
            this.close();
        }
    }

    public boolean isCanceled() {
        return canceled;
    }

}
