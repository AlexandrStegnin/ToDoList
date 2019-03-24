package com.teamdev.todolist.repository;

import com.teamdev.todolist.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Leonid Lebidko
 */

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

//    List<Task> findAllByAuthorId(User author);
//
//    List<Task> findAllByPerfomerId(User perfomer);

}
