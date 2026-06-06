package com.jarena.controller;

import com.jarena.model.Event;
import com.jarena.model.User;
import com.jarena.service.EventService;
import com.jarena.util.AuthHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class EventController {

    @Autowired
    private EventService eventService;

    // ── GET /events ──────────────────────────────────────────────────────────
    @GetMapping("/events")
    public String customerEvents(HttpSession session, Model model) {
        if (!AuthHelper.isLoggedIn(session)) return "redirect:/login";

        User user   = AuthHelper.getLoggedInUser(session);
        long userId = user.getId();

        List<Event> upcomingEvents = eventService.getUpcomingEvents();
        List<Event> pastEvents     = eventService.getPastEvents();

        List<Long>       registeredEventIds  = new ArrayList<>();
        Map<Long, Long>  registrationCounts  = new HashMap<>();

        for (Event e : upcomingEvents) {
            if (eventService.isUserRegistered(e.getId(), userId)) {
                registeredEventIds.add(e.getId());
            }
            registrationCounts.put(e.getId(), eventService.getRegistrationCount(e.getId()));
        }

        model.addAttribute("upcomingEvents",     upcomingEvents);
        model.addAttribute("pastEvents",         pastEvents);
        model.addAttribute("totalEvents",        eventService.getTotalEvents());
        model.addAttribute("totalUpcoming",      eventService.getTotalUpcoming());
        model.addAttribute("totalPast",          eventService.getTotalPast());
        model.addAttribute("currentUser",        user);
        model.addAttribute("registeredEventIds", registeredEventIds);
        model.addAttribute("registrationCounts", registrationCounts);
        return "events/customer-events";
    }

    // ── GET /events/{id} ─────────────────────────────────────────────────────
    @GetMapping("/events/{id}")
    public String eventDetail(@PathVariable long id, HttpSession session, Model model) {
        if (!AuthHelper.isLoggedIn(session)) return "redirect:/login";

        User  user  = AuthHelper.getLoggedInUser(session);
        Event event = eventService.getEventById(id);
        if (event == null) return "redirect:/events";

        model.addAttribute("event",             event);
        model.addAttribute("registrationCount", eventService.getRegistrationCount(id));
        model.addAttribute("isRegistered",      eventService.isUserRegistered(id, user.getId()));
        model.addAttribute("currentUser",       user);
        return "events/event-detail";
    }

    // ── POST /events/register/{id} ───────────────────────────────────────────
    @PostMapping("/events/register/{id}")
    public String register(@PathVariable long id, HttpSession session) {
        if (!AuthHelper.isLoggedIn(session)) return "redirect:/login";
        User user = AuthHelper.getLoggedInUser(session);
        eventService.registerUserForEvent(id, user.getId());
        return "redirect:/events/" + id;
    }

    // ── POST /events/unregister/{id} ─────────────────────────────────────────
    @PostMapping("/events/unregister/{id}")
    public String unregister(@PathVariable long id, HttpSession session) {
        if (!AuthHelper.isLoggedIn(session)) return "redirect:/login";
        User user = AuthHelper.getLoggedInUser(session);
        eventService.unregisterUserFromEvent(id, user.getId());
        return "redirect:/events/" + id;
    }
}
