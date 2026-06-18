package com.jarena.controller;

import com.jarena.model.Match;
import com.jarena.model.Team;
import com.jarena.model.Tournament;
import com.jarena.model.TournamentRegistration;
import com.jarena.service.FieldService;
import com.jarena.service.MatchService;
import com.jarena.service.TeamService;
import com.jarena.service.TournamentService;
import com.jarena.model.User;
import com.jarena.util.AuthHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/admin/tournaments")
public class AdminTournamentController {

    @Autowired
    private TournamentService tournamentService;

    @Autowired
    private TeamService teamService;

    @Autowired
    private MatchService matchService;

    @Autowired
    private FieldService fieldService;

    // ── List ─────────────────────────────────────────────────────────────────

    @GetMapping
    public String adminTournaments(HttpSession session, Model model) {
        if (!AuthHelper.isAdmin(session)) return "redirect:/login";
        User currentUser = AuthHelper.getLoggedInUser(session);

        model.addAttribute("tournaments", tournamentService.getAllTournaments());
        model.addAttribute("upcomingMatches", matchService.getUpcomingMatches());
        model.addAttribute("currentUser", currentUser);
        return "teams/admin-tournaments";
    }

    // ── Create ───────────────────────────────────────────────────────────────

    @GetMapping("/create")
    public String createForm(HttpSession session, Model model) {
        if (!AuthHelper.isAdmin(session)) return "redirect:/login";
        model.addAttribute("tournament", new Tournament());
        model.addAttribute("currentUser", AuthHelper.getLoggedInUser(session));
        return "teams/create-tournament";
    }

    @PostMapping("/create")
    public String createTournament(HttpSession session,
                                   @ModelAttribute("tournament") Tournament tournament,
                                   Model model) {
        if (!AuthHelper.isAdmin(session)) return "redirect:/login";

        String error = tournamentService.createTournament(tournament);
        if (error != null) {
            model.addAttribute("tournament", tournament);
            model.addAttribute("error", error);
            model.addAttribute("currentUser", AuthHelper.getLoggedInUser(session));
            return "teams/create-tournament";
        }
        return "redirect:/admin/tournaments";
    }

    // ── Edit ─────────────────────────────────────────────────────────────────

    @GetMapping("/edit/{id}")
    public String editForm(HttpSession session,
                           @PathVariable("id") long id,
                           Model model) {
        if (!AuthHelper.isAdmin(session)) return "redirect:/login";
        Tournament tournament = tournamentService.getTournamentById(id);
        if (tournament == null) return "redirect:/admin/tournaments";
        model.addAttribute("tournament", tournament);
        model.addAttribute("currentUser", AuthHelper.getLoggedInUser(session));
        return "teams/edit-tournament";
    }

    @PostMapping("/edit/{id}")
    public String editTournament(HttpSession session,
                                 @PathVariable("id") long id,
                                 @ModelAttribute("tournament") Tournament form,
                                 Model model) {
        if (!AuthHelper.isAdmin(session)) return "redirect:/login";

        Tournament existing = tournamentService.getTournamentById(id);
        if (existing == null) return "redirect:/admin/tournaments";

        existing.setName(form.getName());
        existing.setDescription(form.getDescription());
        existing.setStartDate(form.getStartDate());
        existing.setEndDate(form.getEndDate());
        existing.setMaxTeams(form.getMaxTeams());
        existing.setPrize(form.getPrize());

        String error = tournamentService.updateTournament(existing);
        if (error != null) {
            model.addAttribute("tournament", existing);
            model.addAttribute("error", error);
            model.addAttribute("currentUser", AuthHelper.getLoggedInUser(session));
            return "teams/edit-tournament";
        }
        return "redirect:/admin/tournaments";
    }

    // ── Delete ───────────────────────────────────────────────────────────────

    @PostMapping("/delete/{id}")
    public String deleteTournament(HttpSession session, @PathVariable("id") long id) {
        if (!AuthHelper.isAdmin(session)) return "redirect:/login";
        tournamentService.deleteTournament(id);
        return "redirect:/admin/tournaments";
    }

    // ── Update Status ────────────────────────────────────────────────────────

    @PostMapping("/status/{id}")
    public String updateStatus(HttpSession session,
                               @PathVariable("id") long id,
                               @RequestParam("status") String status) {
        if (!AuthHelper.isAdmin(session)) return "redirect:/login";
        tournamentService.updateStatus(id, status);
        return "redirect:/admin/tournaments";
    }

    // ── Tournament Detail (admin) ────────────────────────────────────────────

    @GetMapping("/detail/{id}")
    public String tournamentDetail(HttpSession session,
                                   @PathVariable("id") long id,
                                   Model model) {
        if (!AuthHelper.isAdmin(session)) return "redirect:/login";
        Tournament tournament = tournamentService.getTournamentById(id);
        if (tournament == null) return "redirect:/admin/tournaments";

        List<TournamentRegistration> registrations = tournamentService.getRegistrations(id);
        List<Match> matches = matchService.getByTournament(id);

        model.addAttribute("tournament", tournament);
        model.addAttribute("registrations", registrations);
        model.addAttribute("matches", matches);
        model.addAttribute("registrationCount", tournamentService.getRegistrationCount(id));
        model.addAttribute("currentUser", AuthHelper.getLoggedInUser(session));
        model.addAttribute("isAdmin", true);
        return "teams/tournament-detail";
    }

    // ── Schedule Match ───────────────────────────────────────────────────────

    @GetMapping("/schedule/{tournamentId}")
    public String scheduleMatchForm(HttpSession session,
                                    @PathVariable("tournamentId") long tournamentId,
                                    Model model) {
        if (!AuthHelper.isAdmin(session)) return "redirect:/login";

        Tournament tournament = tournamentService.getTournamentById(tournamentId);
        if (tournament == null) return "redirect:/admin/tournaments";

        List<TournamentRegistration> registrations = tournamentService.getRegistrations(tournamentId);

        model.addAttribute("tournament", tournament);
        model.addAttribute("registrations", registrations);
        model.addAttribute("fields", fieldService.getAllFields());
        model.addAttribute("currentUser", AuthHelper.getLoggedInUser(session));
        return "teams/schedule-match";
    }

    @PostMapping("/schedule/{tournamentId}")
    public String scheduleMatch(HttpSession session,
                                @PathVariable("tournamentId") long tournamentId,
                                @RequestParam("homeTeamId") long homeTeamId,
                                @RequestParam("awayTeamId") long awayTeamId,
                                @RequestParam(value = "fieldId", required = false) Long fieldId,
                                @RequestParam("matchDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate matchDate,
                                @RequestParam("matchTime") @DateTimeFormat(pattern = "HH:mm") LocalTime matchTime,
                                Model model) {
        if (!AuthHelper.isAdmin(session)) return "redirect:/login";

        Match match = new Match();
        match.setTournament(tournamentService.getTournamentById(tournamentId));
        match.setHomeTeam(teamService.getTeamById(homeTeamId));
        match.setAwayTeam(teamService.getTeamById(awayTeamId));
        if (fieldId != null) {
            match.setField(fieldService.getFieldById(fieldId));
        }
        match.setMatchDate(matchDate);
        match.setMatchTime(matchTime);

        String error = matchService.scheduleMatch(match);
        if (error != null) {
            Tournament tournament = tournamentService.getTournamentById(tournamentId);
            model.addAttribute("tournament", tournament);
            model.addAttribute("registrations", tournamentService.getRegistrations(tournamentId));
            model.addAttribute("fields", fieldService.getAllFields());
            model.addAttribute("error", error);
            model.addAttribute("currentUser", AuthHelper.getLoggedInUser(session));
            return "teams/schedule-match";
        }
        return "redirect:/admin/tournaments/detail/" + tournamentId;
    }

    // ── Match Result ─────────────────────────────────────────────────────────

    @PostMapping("/matches/result/{id}")
    public String updateResult(HttpSession session,
                               @PathVariable("id") long matchId,
                               @RequestParam("homeScore") int homeScore,
                               @RequestParam("awayScore") int awayScore) {
        if (!AuthHelper.isAdmin(session)) return "redirect:/login";

        Match match = matchService.getMatchById(matchId);
        if (match == null) return "redirect:/admin/tournaments";

        long tournamentId = match.getTournament().getId();
        matchService.updateMatchResult(matchId, homeScore, awayScore);
        return "redirect:/admin/tournaments/detail/" + tournamentId;
    }

    // ── Delete Match ─────────────────────────────────────────────────────────

    @PostMapping("/matches/delete/{id}")
    public String deleteMatch(HttpSession session, @PathVariable("id") long id) {
        if (!AuthHelper.isAdmin(session)) return "redirect:/login";

        Match match = matchService.getMatchById(id);
        if (match == null) return "redirect:/admin/tournaments";

        long tournamentId = match.getTournament().getId();
        matchService.deleteMatch(id);
        return "redirect:/admin/tournaments/detail/" + tournamentId;
    }
}
