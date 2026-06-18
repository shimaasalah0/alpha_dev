package com.jarena.service;

import com.jarena.dao.TeamDao;
import com.jarena.dao.UserDao;
import com.jarena.model.Team;
import com.jarena.model.TeamMember;
import com.jarena.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TeamServiceImpl implements TeamService {

    @Autowired
    private TeamDao teamDao;

    @Autowired
    private UserDao userDao;

    @Override
    @Transactional
    public String createTeam(Team team, long captainUserId) {
        if (team.getName() == null || team.getName().trim().isEmpty()) {
            return "Team name is required.";
        }
        if (isUserInTeam(captainUserId)) {
            return "You are already a member of another team.";
        }
        User captain = userDao.findById(captainUserId);
        if (captain == null) return "User not found.";

        team.setName(team.getName().trim());
        team.setCaptain(captain);
        team.setWins(0);
        team.setLosses(0);
        team.setDraws(0);
        team.setPoints(0);
        team.setCreatedAt(LocalDateTime.now());
        teamDao.save(team);
        teamDao.addMember(team, captain);
        return null;
    }

    @Override
    public String updateTeam(Team team) {
        if (team.getName() == null || team.getName().trim().isEmpty()) {
            return "Team name is required.";
        }
        team.setName(team.getName().trim());
        teamDao.update(team);
        return null;
    }

    @Override
    public String deleteTeam(long teamId, long requestingUserId) {
        Team team = teamDao.findById(teamId);
        if (team == null) return "Team not found.";
        if (team.getCaptain().getId() != requestingUserId) {
            return "Only the captain can delete the team.";
        }
        teamDao.delete(teamId);
        return null;
    }

    @Override
    public Team getTeamById(long id) {
        return teamDao.findById(id);
    }

    @Override
    public List<Team> getAllTeams() {
        return teamDao.findAll();
    }

    @Override
    public boolean isUserInTeam(long userId) {
        return teamDao.isUserInAnyTeam(userId);
    }

    @Override
    public Team getUserTeam(long userId) {
        return teamDao.findByUser(userId);
    }

    @Override
    public String joinTeam(long teamId, long userId) {
        if (isUserInTeam(userId)) {
            return "You are already a member of a team.";
        }
        Team team = teamDao.findById(teamId);
        if (team == null) return "Team not found.";
        User user = userDao.findById(userId);
        if (user == null) return "User not found.";
        teamDao.addMember(team, user);
        return null;
    }

    @Override
    public String leaveTeam(long teamId, long userId) {
        Team team = teamDao.findById(teamId);
        if (team == null) return "Team not found.";
        if (team.getCaptain().getId() == userId) {
            return "Captain cannot leave the team. Transfer captaincy or delete the team.";
        }
        if (!teamDao.isMember(teamId, userId)) {
            return "You are not a member of this team.";
        }
        teamDao.removeMember(teamId, userId);
        return null;
    }

    @Override
    public List<TeamMember> getTeamMembers(long teamId) {
        return teamDao.findMembersByTeam(teamId);
    }

    @Override
    public boolean isMember(long teamId, long userId) {
        return teamDao.isMember(teamId, userId);
    }

    @Override
    public List<Team> getLeaderboard() {
        return teamDao.findLeaderboard();
    }
}
