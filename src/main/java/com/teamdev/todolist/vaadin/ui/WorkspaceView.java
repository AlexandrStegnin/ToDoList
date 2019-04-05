package com.teamdev.todolist.vaadin.ui;

import com.teamdev.todolist.configuration.security.SecurityUtils;
import com.teamdev.todolist.service.UserService;
import com.teamdev.todolist.service.WorkspaceService;
import com.teamdev.todolist.vaadin.custom.CustomAppLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.*;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.material.Material;

import static com.teamdev.todolist.configuration.support.Constants.WORKSPACE_PAGE;

/**
 * @author Alexandr Stegnin
 */

@Route(WORKSPACE_PAGE)
@PageTitle("Work space")
@Theme(value = Material.class, variant = Material.LIGHT)
public class WorkspaceView extends CustomAppLayout implements HasUrlParameter<String> {

    private Long workspaceId;
    private final WorkspaceService workspaceService;

    public WorkspaceView(UserService userService, WorkspaceService workspaceService) {
        super(userService);
        this.workspaceService = workspaceService;
    }

    private void init() {
        setContent(new Div(new Span(workspaceService.getMyWorkspaceTasks(SecurityUtils.getUsername(), workspaceId)
                .getTasks().toString())));
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, String s) {
        Location location = beforeEvent.getLocation();
        workspaceId = Long.valueOf(location.getSegments().get(1));
        init();
    }
}
