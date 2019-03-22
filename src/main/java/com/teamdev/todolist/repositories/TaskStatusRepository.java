package com.teamdev.todolist.repositories;

import com.teamdev.todolist.entities.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Alexandr Stegnin
 */

@Repository
public interface TaskStatusRepository extends JpaRepository<TaskStatus, Long> {
}
