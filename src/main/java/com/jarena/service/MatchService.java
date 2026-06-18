package com.jarena.service;

import com.jarena.model.Match;

import java.util.List;

public interface MatchService {
    String scheduleMatch(Match match);
    String updateMatchResult(long matchId, int homeScore, int awayScore);
    String deleteMatch(long id);
    Match getMatchById(long id);
    List<Match> getByTournament(long tournamentId);
    List<Match> getByTeam(long teamId);
    List<Match> getUpcomingMatches();
}
