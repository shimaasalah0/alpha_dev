package com.jarena.dao;

import com.jarena.model.Match;

import java.util.List;

public interface MatchDao {
    void save(Match match);
    Match findById(long id);
    List<Match> findByTournament(long tournamentId);
    List<Match> findByTeam(long teamId);
    void update(Match match);
    void delete(long id);
    List<Match> findUpcoming();
}
