package com.teamdev.todolist.service;

import com.teamdev.todolist.entity.Team;
import com.teamdev.todolist.entity.User;
import com.teamdev.todolist.repository.TeamRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Alexandr Stegnin
 */

@Service
public class TeamService {

    private final TeamRepository teamRepository;

    public TeamService(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    public List<Team> findByMember(List<User> members) {
        return teamRepository.findByMembers(members);
    }

    public Team update(Team team) {
        return teamRepository.save(team);
    }

    public Team create(Team team) {
        return teamRepository.save(team);
    }

    public void delete(Team team) {
        teamRepository.delete(team);
    }

}
