package com.teamdev.todolist.repository;

import com.teamdev.todolist.entity.Team;
import com.teamdev.todolist.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Alexandr Stegnin
 */

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByLogin(String login);

    void deleteById(Long id);

    long count();

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.profile p WHERE :team MEMBER OF p.teams")
    List<User> findByTeam(@Param("team") Team team);

}
