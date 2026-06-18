package com.jarena.service;

import com.jarena.model.Tournament;
import com.jarena.model.TournamentRegistration;

import java.util.List;

public interface TournamentService {
    String createTournament(Tournament tournament);
    String updateTournament(Tournament tournament);
    String deleteTournament(long id);
    Tournament getTournamentById(long id);
    List<Tournament> getAllTournaments();
    List<Tournament> getByStatus(String status);
    String registerTeam(long tournamentId, long teamId);
    String unregisterTeam(long tournamentId, long teamId);
    boolean isTeamRegistered(long tournamentId, long teamId);
    long getRegistrationCount(long tournamentId);
    List<TournamentRegistration> getRegistrations(long tournamentId);
    String updateStatus(long tournamentId, String status);
}
