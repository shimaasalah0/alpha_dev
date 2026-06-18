package com.jarena.service;

import com.jarena.model.Team;
import com.jarena.model.TeamMember;

import java.util.List;

public interface TeamService {
    String createTeam(Team team, long captainUserId);
    String updateTeam(Team team);
    String deleteTeam(long teamId, long requestingUserId);
    Team getTeamById(long id);
    List<Team> getAllTeams();
    boolean isUserInTeam(long userId);
    Team getUserTeam(long userId);
    String joinTeam(long teamId, long userId);
    String leaveTeam(long teamId, long userId);
    List<TeamMember> getTeamMembers(long teamId);
    boolean isMember(long teamId, long userId);
    List<Team> getLeaderboard();
}
