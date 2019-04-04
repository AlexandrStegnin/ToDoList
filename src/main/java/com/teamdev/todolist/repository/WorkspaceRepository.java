package com.teamdev.todolist.repository;

import com.teamdev.todolist.entity.WorkSpace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Alexandr Stegnin
 */

@Repository
public interface WorkspaceRepository extends JpaRepository<WorkSpace, Long> {
}
