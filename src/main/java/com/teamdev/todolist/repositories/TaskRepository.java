package com.teamdev.todolist.repositories;

import com.teamdev.todolist.entities.Task;
import com.teamdev.todolist.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Leonid Lebidko
 */

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

//    List<Task> findAllByAuthorId(User author);
//
//    List<Task> findAllByPerfomerId(User perfomer);

}
