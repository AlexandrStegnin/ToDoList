package com.teamdev.todolist.service;

import com.teamdev.todolist.entity.WorkSpace;
import com.teamdev.todolist.repository.WorkspaceRepository;
import org.springframework.stereotype.Service;

/**
 * @author Alexandr Stegnin
 */

@Service
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepository;

    public WorkspaceService(WorkspaceRepository workspaceRepository) {
        this.workspaceRepository = workspaceRepository;
    }

    public WorkSpace findById(Long id) {
        return workspaceRepository.getOne(id);
    }

}
