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

@Controller
@RequestMapping("/admin/events")
public class AdminEventController {

    @Autowired
    private EventService eventService;

    private static final java.util.Set<String> VALID_STATUSES =
            java.util.Set.of("UPCOMING", "COMPLETED");

    // ── GET /admin/events ─────────────────────────────────────────────────────
    @GetMapping
    public String adminEvents(HttpSession session, Model model) {
        if (!AuthHelper.isAdmin(session)) return "redirect:/login";

        java.util.List<Event> events = eventService.getAllEvents();
        java.util.Map<Long, Long> registrationCounts = new java.util.HashMap<>();
        for (Event e : events) {
            registrationCounts.put(e.getId(), eventService.getRegistrationCount(e.getId()));
        }

        long totalUpcoming = events.stream().filter(e -> "UPCOMING".equals(e.getStatus())).count();
        long totalPast     = events.stream().filter(e -> "COMPLETED".equals(e.getStatus())).count();

        model.addAttribute("events",              events);
        model.addAttribute("totalEvents",         (long) events.size());
        model.addAttribute("totalUpcoming",       totalUpcoming);
        model.addAttribute("totalPast",           totalPast);
        model.addAttribute("registrationCounts",  registrationCounts);
        model.addAttribute("currentUser",         AuthHelper.getLoggedInUser(session));
        return "events/admin-events";
    }

    // ── GET /admin/events/create ──────────────────────────────────────────────
    @GetMapping("/create")
    public String createForm(HttpSession session, Model model) {
        if (!AuthHelper.isAdmin(session)) return "redirect:/login";
        model.addAttribute("event", new Event());
        return "events/create-event";
    }

    // ── POST /admin/events/create ─────────────────────────────────────────────
    @PostMapping("/create")
    public String createEvent(@ModelAttribute Event event,
                              HttpSession session, Model model) {
        if (!AuthHelper.isAdmin(session)) return "redirect:/login";

        User admin = AuthHelper.getLoggedInUser(session);
        event.setCreatedBy(admin);

        String error = eventService.createEvent(event);
        if (error != null) {
            model.addAttribute("error", error);
            return "events/create-event";
        }
        return "redirect:/admin/events";
    }

    // ── GET /admin/events/edit/{id} ───────────────────────────────────────────
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable long id, HttpSession session, Model model) {
        if (!AuthHelper.isAdmin(session)) return "redirect:/login";

        Event event = eventService.getEventById(id);
        if (event == null) return "redirect:/admin/events";

        model.addAttribute("event", event);
        return "events/edit-event";
    }

    // ── POST /admin/events/edit/{id} ──────────────────────────────────────────
    @PostMapping("/edit/{id}")
    public String editEvent(@PathVariable long id,
                            @ModelAttribute Event formEvent,
                            HttpSession session, Model model) {
        if (!AuthHelper.isAdmin(session)) return "redirect:/login";

        Event existing = eventService.getEventById(id);
        if (existing == null) return "redirect:/admin/events";

        existing.setTitle(formEvent.getTitle());
        existing.setDescription(formEvent.getDescription());
        existing.setEventDate(formEvent.getEventDate());
        existing.setLocation(formEvent.getLocation());
        existing.setImageUrl(formEvent.getImageUrl());
        existing.setStatus(formEvent.getStatus());

        String error = eventService.updateEvent(existing);
        if (error != null) {
            model.addAttribute("event", existing);
            model.addAttribute("error", error);
            return "events/edit-event";
        }
        return "redirect:/admin/events";
    }

    // ── POST /admin/events/delete/{id} ────────────────────────────────────────
    @PostMapping("/delete/{id}")
    public String deleteEvent(@PathVariable long id, HttpSession session) {
        if (!AuthHelper.isAdmin(session)) return "redirect:/login";
        eventService.deleteEvent(id);
        return "redirect:/admin/events";
    }

    // ── POST /admin/events/status/{id} ────────────────────────────────────────
    @PostMapping("/status/{id}")
    public String updateStatus(@PathVariable long id,
                               @RequestParam String status,
                               HttpSession session) {
        if (!AuthHelper.isAdmin(session)) return "redirect:/login";

        if (!VALID_STATUSES.contains(status)) return "redirect:/admin/events";
        Event event = eventService.getEventById(id);
        if (event != null) {
            event.setStatus(status);
            eventService.updateEvent(event);
        }
        return "redirect:/admin/events";
    }
}
