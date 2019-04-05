package com.teamdev.todolist.service;

import com.teamdev.todolist.entity.User;
import com.teamdev.todolist.entity.Workspace;
import com.teamdev.todolist.repository.WorkspaceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Alexandr Stegnin
 */

@Service
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepository;

    public WorkspaceService(WorkspaceRepository workspaceRepository) {
        this.workspaceRepository = workspaceRepository;
    }

    public Workspace findById(Long id) {
        return workspaceRepository.getOne(id);
    }

    public List<Workspace> getMyWorkspaces(User owner) {
        return workspaceRepository.findByOwner(owner);
    }

}
