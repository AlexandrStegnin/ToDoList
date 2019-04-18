package com.teamdev.todolist.service;

import com.teamdev.todolist.entity.Team;
import com.teamdev.todolist.entity.User;
import com.teamdev.todolist.repository.TeamRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Alexandr Stegnin
 */

@Service
@Transactional(readOnly = true)
public class TeamService {

    private final TeamRepository teamRepository;
    private final UserService userService;

    public TeamService(TeamRepository teamRepository, UserService userService) {
        this.teamRepository = teamRepository;
        this.userService = userService;
    }

    public List<Team> findByMember(List<User> members) {
        return teamRepository.findByMembers(members);
    }

    @Transactional
    public Team update(Team team) {
        return teamRepository.save(team);
    }

    @Transactional
    public Team create(Team team) {
        return teamRepository.save(team);
    }

    @Transactional
    public void delete(Team team) {
        teamRepository.delete(team);
    }

    @Transactional
    public void delete(Long id) {
        teamRepository.deleteById(id);
    }

    public Team findOne(Long id) {
        return teamRepository.getOne(id);
    }

    public List<Team> findAll() {
        return teamRepository.findAll();
    }

    public Team addMember(Team team) {
        Team teamToUpdate = findOne(team.getId());
        team.getMembers().forEach(user -> teamToUpdate.addMember(userService.findOne(user.getId())));
        return update(teamToUpdate);
    }

    public Team removeMember(Team team) {
        Team teamToUpdate = findOne(team.getId());
        team.getMembers().forEach(user -> teamToUpdate.removeMember(userService.findOne(user.getId())));
        return update(teamToUpdate);
    }

    public Team changeTeamTitle(Team team) {
        Team teamToUpdate = findOne(team.getId());
        teamToUpdate.setTitle(team.getTitle());
        return update(teamToUpdate);
    }

}
