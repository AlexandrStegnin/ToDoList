package com.teamdev.todolist.repository;

import com.teamdev.todolist.entity.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Alexandr Stegnin
 */

@Repository
public interface WorkspaceRepository extends JpaRepository<Workspace, Long> {

    @Query("SELECT DISTINCT ws FROM Workspace ws LEFT JOIN FETCH ws.tasks WHERE ws.owner.login = :login")
    List<Workspace> findByOwnerLogin(@Param("login") String login);

    @Query("SELECT DISTINCT ws FROM Workspace ws LEFT JOIN FETCH ws.tasks " +
            "WHERE ws.owner.login = :login AND ws.id = :workspaceId")
    Workspace findByOwnerLoginAndWorkspaceId(@Param("login") String login, @Param("workspaceId") Long workspaceId);


}
