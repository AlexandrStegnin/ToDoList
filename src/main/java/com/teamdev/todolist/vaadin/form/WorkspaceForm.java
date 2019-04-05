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
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author Alexandr Stegnin
 */
public class WorkspaceForm extends Dialog {

    private static final String PRIVATE_WS = "Личная";
    private static final String TEAM_WS = "Командная";

    private final WorkspaceService workspaceService;

    private final TeamService teamService;

    private final TextField title;
    private final Button cancel;
    private final OperationEnum operation;
    private final HorizontalLayout buttons;
    private Button submit;
    private final User owner;

    private Select<Team> team;

    private Workspace workspace;

    private Binder<Workspace> workspaceBinder;

    private boolean canceled = false;

    public WorkspaceForm(WorkspaceService workspaceService, Workspace workspace, TeamService teamService,
                         OperationEnum operation, User owner) {
        this.workspaceBinder = new BeanValidationBinder<>(Workspace.class);
        this.workspaceService = workspaceService;
        this.teamService = teamService;
        this.workspace = workspace;
        this.title = new TextField("Название");
        this.team = new Select<>();
        this.buttons = new HorizontalLayout();
        this.cancel = new Button("Отменить", e -> {
            this.canceled = true;
            this.close();
        });
        this.submit = new Button(operation.name);
        this.operation = operation;
        this.owner = owner;
        init();
    }

    private void init() {
        prepareSubmitButton(operation);
        workspace.setOwner(owner);
        team.setItems(getMyTeams());
        team.setTextRenderer(Team::getTitle);
        team.setEmptySelectionAllowed(true);
        team.setEmptySelectionCaption("Выберите команду");
        team.setVisible(false);

        Select<String> privateOrTeam = new Select<>();
        privateOrTeam.setEmptySelectionAllowed(false);
        privateOrTeam.setItems(PRIVATE_WS, TEAM_WS);
        privateOrTeam.setValue(PRIVATE_WS);
        privateOrTeam.setPlaceholder("Вид рабочей области");
        privateOrTeam.addValueChangeListener(event -> {
            if (PRIVATE_WS.equalsIgnoreCase(privateOrTeam.getValue())) {
                team.setVisible(false);
                workspace.setTeam(null);
            } else {
                team.setVisible(true);
                workspaceBinder.forField(team)
                        .withValidator(t -> !team.isVisible() || !Objects.equals(null, t),
                                "Для командной РО необходимо выбрать команду")
                        .bind(Workspace_.TEAM);
            }
        });

        buttons.add(submit, cancel);
        VerticalLayout content = new VerticalLayout(title, privateOrTeam, team, buttons);
        add(content);
        workspaceBinder.setBean(workspace);
        workspaceBinder.bindInstanceFields(this);
    }

    private List<Team> getMyTeams() {
        return teamService.findByMember(Collections.singletonList(owner));
    }

    private void prepareSubmitButton(OperationEnum operation) {
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
