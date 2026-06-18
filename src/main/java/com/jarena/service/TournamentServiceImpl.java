package com.jarena.service;

import com.jarena.dao.TeamDao;
import com.jarena.dao.TournamentDao;
import com.jarena.model.Team;
import com.jarena.model.Tournament;
import com.jarena.model.TournamentRegistration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TournamentServiceImpl implements TournamentService {

    @Autowired
    private TournamentDao tournamentDao;

    @Autowired
    private TeamDao teamDao;

    @Override
    public String createTournament(Tournament tournament) {
        if (tournament.getName() == null || tournament.getName().trim().isEmpty()) {
            return "Tournament name is required.";
        }
        if (tournament.getStartDate() == null || tournament.getEndDate() == null) {
            return "Start and end dates are required.";
        }
        if (tournament.getEndDate().isBefore(tournament.getStartDate())) {
            return "End date cannot be before start date.";
        }
        if (tournament.getMaxTeams() < 2) {
            return "Maximum teams must be at least 2.";
        }
        tournament.setName(tournament.getName().trim());
        tournament.setStatus("UPCOMING");
        tournament.setCreatedAt(LocalDateTime.now());
        tournamentDao.save(tournament);
        return null;
    }

    @Override
    public String updateTournament(Tournament tournament) {
        if (tournament.getName() == null || tournament.getName().trim().isEmpty()) {
            return "Tournament name is required.";
        }
        if (tournament.getEndDate().isBefore(tournament.getStartDate())) {
            return "End date cannot be before start date.";
        }
        tournament.setName(tournament.getName().trim());
        tournamentDao.update(tournament);
        return null;
    }

    @Override
    public String deleteTournament(long id) {
        Tournament t = tournamentDao.findById(id);
        if (t == null) return "Tournament not found.";
        tournamentDao.delete(id);
        return null;
    }

    @Override
    public Tournament getTournamentById(long id) {
        return tournamentDao.findById(id);
    }

    @Override
    public List<Tournament> getAllTournaments() {
        return tournamentDao.findAll();
    }

    @Override
    public List<Tournament> getByStatus(String status) {
        return tournamentDao.findByStatus(status);
    }

    @Override
    public String registerTeam(long tournamentId, long teamId) {
        Tournament tournament = tournamentDao.findById(tournamentId);
        if (tournament == null) return "Tournament not found.";
        if (!"UPCOMING".equals(tournament.getStatus())) {
            return "Registration is only open for upcoming tournaments.";
        }
        if (tournamentDao.isTeamRegistered(tournamentId, teamId)) {
            return "Your team is already registered for this tournament.";
        }
        long currentCount = tournamentDao.countRegistrations(tournamentId);
        if (currentCount >= tournament.getMaxTeams()) {
            return "Tournament is full.";
        }
        Team team = teamDao.findById(teamId);
        if (team == null) return "Team not found.";

        TournamentRegistration reg = new TournamentRegistration();
        reg.setTournament(tournament);
        reg.setTeam(team);
        reg.setRegisteredAt(LocalDateTime.now());
        tournamentDao.saveRegistration(reg);
        return null;
    }

    @Override
    public String unregisterTeam(long tournamentId, long teamId) {
        Tournament tournament = tournamentDao.findById(tournamentId);
        if (tournament == null) return "Tournament not found.";
        if (!"UPCOMING".equals(tournament.getStatus())) {
            return "Cannot unregister from an active or completed tournament.";
        }
        if (!tournamentDao.isTeamRegistered(tournamentId, teamId)) {
            return "Team is not registered for this tournament.";
        }
        tournamentDao.deleteRegistration(tournamentId, teamId);
        return null;
    }

    @Override
    public boolean isTeamRegistered(long tournamentId, long teamId) {
        return tournamentDao.isTeamRegistered(tournamentId, teamId);
    }

    @Override
    public long getRegistrationCount(long tournamentId) {
        return tournamentDao.countRegistrations(tournamentId);
    }

    @Override
    public List<TournamentRegistration> getRegistrations(long tournamentId) {
        return tournamentDao.findRegistrationsByTournament(tournamentId);
    }

    @Override
    public String updateStatus(long tournamentId, String status) {
        Tournament tournament = tournamentDao.findById(tournamentId);
        if (tournament == null) return "Tournament not found.";
        tournament.setStatus(status);
        tournamentDao.update(tournament);
        return null;
    }
}
