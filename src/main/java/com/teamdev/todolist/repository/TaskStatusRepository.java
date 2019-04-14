package com.teamdev.todolist.repository;

import com.teamdev.todolist.entity.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Alexandr Stegnin
 */

@Repository
public interface TaskStatusRepository extends JpaRepository<TaskStatus, Long> {

    TaskStatus findByTitle(String title);

    long count();

}
