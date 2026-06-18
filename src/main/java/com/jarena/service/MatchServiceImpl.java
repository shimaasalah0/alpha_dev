package com.jarena.service;

import com.jarena.dao.MatchDao;
import com.jarena.dao.TeamDao;
import com.jarena.model.Match;
import com.jarena.model.Team;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MatchServiceImpl implements MatchService {

    @Autowired
    private MatchDao matchDao;

    @Autowired
    private TeamDao teamDao;

    @Override
    public String scheduleMatch(Match match) {
        if (match.getHomeTeam().getId() == match.getAwayTeam().getId()) {
            return "A team cannot play against itself.";
        }
        if (match.getMatchDate() == null) return "Match date is required.";
        if (match.getMatchTime() == null) return "Match time is required.";
        match.setStatus("SCHEDULED");
        match.setCreatedAt(LocalDateTime.now());
        matchDao.save(match);
        return null;
    }

    @Override
    @Transactional
    public String updateMatchResult(long matchId, int homeScore, int awayScore) {
        if (homeScore < 0 || awayScore < 0) return "Scores cannot be negative.";

        Match match = matchDao.findById(matchId);
        if (match == null) return "Match not found.";

        match.setHomeScore(homeScore);
        match.setAwayScore(awayScore);
        match.setStatus("COMPLETED");
        matchDao.update(match);

        Team homeTeam = teamDao.findById(match.getHomeTeam().getId());
        Team awayTeam = teamDao.findById(match.getAwayTeam().getId());

        if (homeScore > awayScore) {
            homeTeam.setWins(homeTeam.getWins() + 1);
            homeTeam.setPoints(homeTeam.getPoints() + 3);
            awayTeam.setLosses(awayTeam.getLosses() + 1);
        } else if (awayScore > homeScore) {
            awayTeam.setWins(awayTeam.getWins() + 1);
            awayTeam.setPoints(awayTeam.getPoints() + 3);
            homeTeam.setLosses(homeTeam.getLosses() + 1);
        } else {
            homeTeam.setDraws(homeTeam.getDraws() + 1);
            homeTeam.setPoints(homeTeam.getPoints() + 1);
            awayTeam.setDraws(awayTeam.getDraws() + 1);
            awayTeam.setPoints(awayTeam.getPoints() + 1);
        }

        teamDao.update(homeTeam);
        teamDao.update(awayTeam);
        return null;
    }

    @Override
    public String deleteMatch(long id) {
        Match match = matchDao.findById(id);
        if (match == null) return "Match not found.";
        matchDao.delete(id);
        return null;
    }

    @Override
    public Match getMatchById(long id) {
        return matchDao.findById(id);
    }

    @Override
    public List<Match> getByTournament(long tournamentId) {
        return matchDao.findByTournament(tournamentId);
    }

    @Override
    public List<Match> getByTeam(long teamId) {
        return matchDao.findByTeam(teamId);
    }

    @Override
    public List<Match> getUpcomingMatches() {
        return matchDao.findUpcoming();
    }
}
