package com.jarena.controller;

import com.jarena.model.Team;
import com.jarena.model.Tournament;
import com.jarena.model.User;
import com.jarena.service.MatchService;
import com.jarena.service.NotificationService;
import com.jarena.service.TeamService;
import com.jarena.service.TournamentService;
import com.jarena.util.AuthHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
public class TeamController {

    @Autowired
    private TeamService teamService;

    @Autowired
    private TournamentService tournamentService;

    @Autowired
    private MatchService matchService;

    @Autowired
    private NotificationService notificationService;

    // ── My Teams / All Teams ─────────────────────────────────────────────────

    @GetMapping("/teams")
    public String customerTeams(HttpSession session, Model model) {
        if (!AuthHelper.isLoggedIn(session)) return "redirect:/login";
        User currentUser = AuthHelper.getLoggedInUser(session);
        long userId = currentUser.getId();

        boolean isInTeam = teamService.isUserInTeam(userId);
        Team userTeam = isInTeam ? teamService.getUserTeam(userId) : null;

        List<Team> allTeams = teamService.getAllTeams();

        List<Tournament> openTournaments = new ArrayList<>();
        openTournaments.addAll(tournamentService.getByStatus("UPCOMING"));
        openTournaments.addAll(tournamentService.getByStatus("ACTIVE"));

        long unreadCount = notificationService.getUnreadCount(userId);

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("isInTeam", isInTeam);
        model.addAttribute("userTeam", userTeam);
        model.addAttribute("allTeams", allTeams);
        model.addAttribute("tournaments", openTournaments);
        model.addAttribute("upcomingMatches", matchService.getUpcomingMatches());
        model.addAttribute("unreadCount", unreadCount);
        return "teams/customer-teams";
    }

    // ── Create Team ──────────────────────────────────────────────────────────

    @GetMapping("/teams/create")
    public String createTeamForm(HttpSession session, Model model) {
        if (!AuthHelper.isLoggedIn(session)) return "redirect:/login";
        User currentUser = AuthHelper.getLoggedInUser(session);

        if (teamService.isUserInTeam(currentUser.getId())) {
            return "redirect:/teams?error=already_in_team";
        }

        model.addAttribute("team", new Team());
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("unreadCount", notificationService.getUnreadCount(currentUser.getId()));
        return "teams/create-team";
    }

    @PostMapping("/teams/create")
    public String createTeam(HttpSession session,
                             @ModelAttribute("team") Team team,
                             Model model) {
        if (!AuthHelper.isLoggedIn(session)) return "redirect:/login";
        User currentUser = AuthHelper.getLoggedInUser(session);

        String error = teamService.createTeam(team, currentUser.getId());
        if (error != null) {
            model.addAttribute("team", team);
            model.addAttribute("error", error);
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("unreadCount", notificationService.getUnreadCount(currentUser.getId()));
            return "teams/create-team";
        }
        return "redirect:/teams";
    }

    // ── Team Detail ──────────────────────────────────────────────────────────

    @GetMapping("/teams/{id}")
    public String teamDetail(HttpSession session,
                             @PathVariable("id") long id,
                             Model model) {
        if (!AuthHelper.isLoggedIn(session)) return "redirect:/login";
        User currentUser = AuthHelper.getLoggedInUser(session);
        long userId = currentUser.getId();

        Team team = teamService.getTeamById(id);
        if (team == null) return "redirect:/teams";

        boolean isUserMember = teamService.isMember(id, userId);
        boolean isCaptain = team.getCaptain().getId() == userId;
        boolean userHasTeam = teamService.isUserInTeam(userId);

        model.addAttribute("team", team);
        model.addAttribute("members", teamService.getTeamMembers(id));
        model.addAttribute("matches", matchService.getByTeam(id));
        model.addAttribute("isUserMember", isUserMember);
        model.addAttribute("isCaptain", isCaptain);
        model.addAttribute("userHasTeam", userHasTeam);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("unreadCount", notificationService.getUnreadCount(userId));
        return "teams/team-detail";
    }

    // ── Delete Team (captain only) ───────────────────────────────────────────

    @PostMapping("/teams/delete/{id}")
    public String deleteTeam(HttpSession session, @PathVariable("id") long id) {
        if (!AuthHelper.isLoggedIn(session)) return "redirect:/login";
        User currentUser = AuthHelper.getLoggedInUser(session);

        String error = teamService.deleteTeam(id, currentUser.getId());
        if (error != null) {
            return "redirect:/teams/" + id + "?error=" + error.replace(" ", "+");
        }
        return "redirect:/teams";
    }

    // ── Join / Leave ─────────────────────────────────────────────────────────

    @PostMapping("/teams/join/{id}")
    public String joinTeam(HttpSession session, @PathVariable("id") long id) {
        if (!AuthHelper.isLoggedIn(session)) return "redirect:/login";
        User currentUser = AuthHelper.getLoggedInUser(session);

        String error = teamService.joinTeam(id, currentUser.getId());
        if (error != null) {
            return "redirect:/teams/" + id + "?error=" + error.replace(" ", "+");
        }
        return "redirect:/teams/" + id;
    }

    @PostMapping("/teams/leave/{id}")
    public String leaveTeam(HttpSession session, @PathVariable("id") long id) {
        if (!AuthHelper.isLoggedIn(session)) return "redirect:/login";
        User currentUser = AuthHelper.getLoggedInUser(session);

        String error = teamService.leaveTeam(id, currentUser.getId());
        if (error != null) {
            return "redirect:/teams/" + id + "?error=" + error.replace(" ", "+");
        }
        return "redirect:/teams";
    }

    // ── Tournament Detail (customer view) ────────────────────────────────────

    @GetMapping("/teams/tournaments/{id}")
    public String tournamentDetail(HttpSession session,
                                   @PathVariable("id") long id,
                                   Model model) {
        if (!AuthHelper.isLoggedIn(session)) return "redirect:/login";
        User currentUser = AuthHelper.getLoggedInUser(session);
        long userId = currentUser.getId();

        Tournament tournament = tournamentService.getTournamentById(id);
        if (tournament == null) return "redirect:/teams";

        boolean isInTeam = teamService.isUserInTeam(userId);
        Team userTeam = isInTeam ? teamService.getUserTeam(userId) : null;
        boolean isRegistered = (userTeam != null) && tournamentService.isTeamRegistered(id, userTeam.getId());
        boolean isCaptain = (userTeam != null) && userTeam.getCaptain().getId() == userId;
        long registrationCount = tournamentService.getRegistrationCount(id);

        model.addAttribute("tournament", tournament);
        model.addAttribute("registrations", tournamentService.getRegistrations(id));
        model.addAttribute("matches", matchService.getByTournament(id));
        model.addAttribute("userTeam", userTeam);
        model.addAttribute("isRegistered", isRegistered);
        model.addAttribute("isCaptain", isCaptain);
        model.addAttribute("registrationCount", registrationCount);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("unreadCount", notificationService.getUnreadCount(userId));
        return "teams/tournament-detail";
    }

    // ── Register / Unregister for Tournament ─────────────────────────────────

    @PostMapping("/teams/tournaments/register/{id}")
    public String registerForTournament(HttpSession session,
                                        @PathVariable("id") long tournamentId) {
        if (!AuthHelper.isLoggedIn(session)) return "redirect:/login";
        User currentUser = AuthHelper.getLoggedInUser(session);
        long userId = currentUser.getId();

        Team userTeam = teamService.getUserTeam(userId);
        if (userTeam == null) {
            return "redirect:/teams/tournaments/" + tournamentId + "?error=You+must+have+a+team+to+register";
        }
        if (userTeam.getCaptain().getId() != userId) {
            return "redirect:/teams/tournaments/" + tournamentId + "?error=Only+the+captain+can+register";
        }

        String error = tournamentService.registerTeam(tournamentId, userTeam.getId());
        if (error != null) {
            return "redirect:/teams/tournaments/" + tournamentId + "?error=" + error.replace(" ", "+");
        }
        return "redirect:/teams/tournaments/" + tournamentId;
    }

    @PostMapping("/teams/tournaments/unregister/{id}")
    public String unregisterFromTournament(HttpSession session,
                                           @PathVariable("id") long tournamentId) {
        if (!AuthHelper.isLoggedIn(session)) return "redirect:/login";
        User currentUser = AuthHelper.getLoggedInUser(session);
        long userId = currentUser.getId();

        Team userTeam = teamService.getUserTeam(userId);
        if (userTeam == null) return "redirect:/teams/tournaments/" + tournamentId;
        if (userTeam.getCaptain().getId() != userId) {
            return "redirect:/teams/tournaments/" + tournamentId + "?error=Only+the+captain+can+unregister";
        }

        String error = tournamentService.unregisterTeam(tournamentId, userTeam.getId());
        if (error != null) {
            return "redirect:/teams/tournaments/" + tournamentId + "?error=" + error.replace(" ", "+");
        }
        return "redirect:/teams/tournaments/" + tournamentId;
    }

    // ── Leaderboard ──────────────────────────────────────────────────────────

    @GetMapping("/leaderboard")
    public String leaderboard(HttpSession session, Model model) {
        if (!AuthHelper.isLoggedIn(session)) return "redirect:/login";
        User currentUser = AuthHelper.getLoggedInUser(session);
        long userId = currentUser.getId();

        Team userTeam = teamService.isUserInTeam(userId) ? teamService.getUserTeam(userId) : null;

        model.addAttribute("leaderboard", teamService.getLeaderboard());
        model.addAttribute("userTeam", userTeam);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("unreadCount", notificationService.getUnreadCount(userId));
        return "teams/leaderboard";
    }
}
