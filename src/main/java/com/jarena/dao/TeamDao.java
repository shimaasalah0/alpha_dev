package com.jarena.dao;

import com.jarena.model.Team;
import com.jarena.model.TeamMember;
import com.jarena.model.User;

import java.util.List;

public interface TeamDao {
    void save(Team team);
    Team findById(long id);
    List<Team> findAll();
    void update(Team team);
    void delete(long id);
    boolean isUserInAnyTeam(long userId);
    Team findByUser(long userId);
    void addMember(Team team, User user);
    void removeMember(long teamId, long userId);
    List<TeamMember> findMembersByTeam(long teamId);
    boolean isMember(long teamId, long userId);
    List<Team> findLeaderboard();
}
