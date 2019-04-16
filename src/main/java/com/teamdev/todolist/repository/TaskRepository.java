package com.teamdev.todolist.repository;

import com.teamdev.todolist.entity.Task;
import com.teamdev.todolist.entity.User;
import com.teamdev.todolist.entity.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Leonid Lebidko
 */

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findAllByAuthor(User author);

    List<Task> findAllByPerformersAndWorkspace(List<User> performers, Workspace workspace);

    List<Task> findDistinctByPerformers(List<User> performers);

    @Query("SELECT t FROM Task t WHERE t.workspace.owner.id = :ownerId AND t.workspace.id = :workspaceId")
    List<Task> getTasksByOwnerIdAndWorkspaceId(@Param("ownerId") Long ownerId, @Param("workspaceId") Long workspaceId);

    @Query("SELECT t FROM Task t WHERE t.author.login = :login AND t.workspace.id = :workspaceId")
    List<Task> getTasksByAuthorLoginAndWorkspaceId(@Param("login") String login, @Param("workspaceId") Long workspaceId);

}
