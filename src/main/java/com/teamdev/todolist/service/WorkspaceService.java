package com.teamdev.todolist.service;

import com.teamdev.todolist.entity.Team;
import com.teamdev.todolist.entity.Workspace;
import com.teamdev.todolist.repository.WorkspaceRepository;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Alexandr Stegnin
 */

@Service
@Transactional(readOnly = true)
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepository;
    private final UserService userService;
    private final TeamService teamService;

    public WorkspaceService(WorkspaceRepository workspaceRepository, UserService userService, TeamService teamService) {
        this.workspaceRepository = workspaceRepository;
        this.userService = userService;
        this.teamService = teamService;
    }

    public Workspace findById(Long id) {
        Workspace workspace = workspaceRepository.getOne(id);
        workspace.getTasks().forEach(task -> {
            Hibernate.initialize(task.getPerformers());
            Hibernate.initialize(task.getTags());
        });
        Hibernate.initialize(workspace.getTags());
        return workspace;
    }

    public List<Workspace> getMyWorkspaces(String login) {
        List<Workspace> workspaces = workspaceRepository.findByOwnerLogin(login);
        workspaces.forEach(workspace -> Hibernate.initialize(workspace.getTasks()));
        return workspaces;
    }

    public List<Workspace> findByTeam(Team team) {
        List<Workspace> workspaces = workspaceRepository.findDistinctByTeam(team);
        workspaces.forEach(workspace -> Hibernate.initialize(workspace.getTasks()));
        return workspaces;
    }

    @Transactional
    public void delete(Workspace workspace) {
        workspaceRepository.delete(workspace);
    }

    @Transactional
    public Workspace create(Workspace workspace) {
        if (workspace.getOwner() != null && workspace.getOwner().getLogin() == null)
            workspace.setOwner(userService.findOne(workspace.getOwner().getId()));
        return workspaceRepository.save(workspace);
    }

    @Transactional
    public List<Workspace> saveAll(List<Workspace> workspaces) {
        return workspaceRepository.saveAll(workspaces);
    }

    public Workspace update(Workspace workspace) {
        return create(workspace);
    }

    public List<Workspace> findAll() {
        return workspaceRepository.findAll();
    }

    public Workspace changeWsTitle(Workspace workspace) {
        Workspace wsToUpdate = findById(workspace.getId());
        wsToUpdate.setTitle(workspace.getTitle());
        return update(wsToUpdate);
    }

    @Transactional
    public void delete(Long workspaceId) {
        workspaceRepository.deleteById(workspaceId);
    }

    public Workspace addTeam(Workspace workspace) {
        Workspace wsToUpdate = findById(workspace.getId());
        Team team = teamService.findOne(workspace.getTeam().getId());
        wsToUpdate.setTeam(team);
        return update(wsToUpdate);
    }

    public Workspace removeTeam(Workspace workspace) {
        Workspace wsToUpdate = findById(workspace.getId());
        wsToUpdate.setTeam(null);
        return update(wsToUpdate);
    }
}
