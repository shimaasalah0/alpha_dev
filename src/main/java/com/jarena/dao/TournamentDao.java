package com.jarena.dao;

import com.jarena.model.Tournament;
import com.jarena.model.TournamentRegistration;

import java.util.List;

public interface TournamentDao {
    void save(Tournament tournament);
    Tournament findById(long id);
    List<Tournament> findAll();
    List<Tournament> findByStatus(String status);
    void update(Tournament tournament);
    void delete(long id);
    void saveRegistration(TournamentRegistration registration);
    boolean isTeamRegistered(long tournamentId, long teamId);
    long countRegistrations(long tournamentId);
    List<TournamentRegistration> findRegistrationsByTournament(long tournamentId);
    void deleteRegistration(long tournamentId, long teamId);
}
