package com.teamdev.todolist.vaadin.form;

import com.teamdev.todolist.command.Command;
import com.teamdev.todolist.command.team.CreateTeamCommand;
import com.teamdev.todolist.command.team.DeleteTeamCommand;
import com.teamdev.todolist.command.team.UpdateTeamCommand;
import com.teamdev.todolist.configuration.security.SecurityUtils;
import com.teamdev.todolist.configuration.support.OperationEnum;
import com.teamdev.todolist.entity.Team;
import com.teamdev.todolist.entity.User;
import com.teamdev.todolist.entity.Workspace;
import com.teamdev.todolist.service.TeamService;
import com.teamdev.todolist.service.UserService;
import com.teamdev.todolist.service.WorkspaceService;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import org.vaadin.gatanaso.MultiselectComboBox;

import java.util.Collections;
import java.util.List;

/**
 * @author Alexandr Stegnin
 */
public class TeamForm extends Dialog {

    private final UserService userService;
    private final TeamService teamService;
    private final WorkspaceService workspaceService;
    private final TextField title;

    private final MultiselectComboBox<User> members;
    private final VerticalLayout content;
    private final Div alert;

    private OperationEnum operation;
    private HorizontalLayout buttons;
    private final User currentUser;
    private Binder<Team> teamBinder;
    private Team team;
    private final Button cancel;
    private Button submit;
    private boolean canceled = false;

    public TeamForm(final UserService userService, final TeamService teamService, final WorkspaceService workspaceService,
                    Team team, OperationEnum operation) {
        this.userService = userService;
        this.teamService = teamService;
        this.workspaceService = workspaceService;
        this.teamBinder = new BeanValidationBinder<>(Team.class);
        this.title = new TextField("НАЗВАНИЕ");
        this.members = new MultiselectComboBox<>();
        this.content = new VerticalLayout();
        this.alert = new Div(new Text("ВНИМАНИЕ! ВСЕ СВЯЗАННЫЕ ЗАДАЧИ БУДУТ ПЕРЕМЕЩЕНЫ НА ВАС!"));
        this.team = team;
        this.operation = operation;
        this.currentUser = userService.findByLogin(SecurityUtils.getUsername());
        this.cancel = new Button("ОТМЕНИТЬ", e -> {
            this.canceled = true;
            this.close();
        });
        this.buttons = new HorizontalLayout();
        init();
    }

    private void init() {
        prepareSubmitButton();
        stylizeForm();
        members.setItems(getAllUsers());
        members.setRequired(true);
        members.setRequiredIndicatorVisible(true);
        members.setItemLabelGenerator(User::getLogin);

        buttons.add(submit, cancel);
        content.add(alert, title, members, buttons);
        add(content);
        team.addMember(currentUser);
        team.setOwner(currentUser);
        teamBinder.setBean(team);
        teamBinder.bindInstanceFields(this);
    }

    public boolean isCanceled() {
        return canceled;
    }

    private List<User> getAllUsers() {
        return userService.findAll();
    }

    private void prepareSubmitButton() {
        this.submit = new Button(operation.name.toUpperCase());
        switch (operation) {
            case CREATE:
                submit.addClickListener(e -> executeCommand(new CreateTeamCommand(teamService, team), team));
                break;
            case UPDATE:
                submit.addClickListener(e -> executeCommand(new UpdateTeamCommand(teamService, team), team));
                break;
            case DELETE:
                submit.addClickListener(e -> executeCommand(new DeleteTeamCommand(teamService, team), team));
                break;
        }
    }

    private void executeCommand(Command command, Team team) {
        if (!team.getMembers().contains(currentUser)) team.addMember(currentUser);
        if (command instanceof DeleteTeamCommand) {
            List<Workspace> wsToEdit = workspaceService.findByTeam(team);
            wsToEdit.forEach(workspace -> {
                workspace.setTeam(null);
                workspace.getTasks().forEach(task -> task.setPerformers(Collections.singleton(currentUser)));
            });
            workspaceService.saveAll(wsToEdit);
            command.execute();
            this.close();
        } else if (teamBinder.writeBeanIfValid(team)) {
            command.execute();
            this.close();
        }
    }

    private void stylizeForm() {
        title.setPlaceholder("ВВЕДИТЕ НАЗВАНИЕ");
        title.setRequiredIndicatorVisible(true);
        title.setWidthFull();
        title.getStyle().set("font-size", "11px");

        members.getElement().getStyle().set("font-size", "11px");

        submit.addClassNames("btn", "bg-green", "waves-effect");
        submit.getStyle().set("padding", "8px 10px 25px");

        cancel.addClassNames("btn", "bg-red", "waves-effect");
        cancel.getStyle().set("padding", "8px 10px 25px");

        buttons.setWidthFull();
        buttons.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        content.setHeightFull();

        alert.getStyle()
                .set("color", "orange")
                .set("text-align", "center")
                .set("font-size", "14px");
        alert.setVisible(operation.compareTo(OperationEnum.DELETE) == 0);
        setWidth("400px");
        setHeightFull();
    }

    public void allowEditForm(final boolean allowEdit) {
        title.setReadOnly(!allowEdit);
        members.setReadOnly(!allowEdit);
        submit.setEnabled(allowEdit);
    }

}
