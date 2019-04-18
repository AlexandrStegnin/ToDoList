package com.teamdev.todolist.repository;

import com.teamdev.todolist.entity.TaskLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Alexandr Stegnin
 */

@Repository
public interface TaskLogRepository extends JpaRepository<TaskLog, Long> {

}
