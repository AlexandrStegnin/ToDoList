package com.teamdev.todolist.repository;

import com.teamdev.todolist.entity.Task;
import com.teamdev.todolist.entity.User;
import com.teamdev.todolist.entity.WorkSpace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Leonid Lebidko
 */

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findAllByAuthor(User author);

    List<Task> findAllByPerformers(List<User> performers);

    List<Task> findByWorkSpace(WorkSpace workSpace);

}
