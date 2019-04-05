package com.teamdev.todolist.repository;

import com.teamdev.todolist.entity.Team;
import com.teamdev.todolist.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Alexandr Stegnin
 */

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

    List<Team> findByMembers(List<User> members);

}
