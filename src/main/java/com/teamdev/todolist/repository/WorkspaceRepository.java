package com.teamdev.todolist.repository;

import com.teamdev.todolist.entity.User;
import com.teamdev.todolist.entity.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Alexandr Stegnin
 */

@Repository
public interface WorkspaceRepository extends JpaRepository<Workspace, Long> {

    List<Workspace> findByOwner(User owner);

}
