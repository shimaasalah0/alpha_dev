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

    // ── GET /admin/events ─────────────────────────────────────────────────────
    @GetMapping
    public String adminEvents(HttpSession session, Model model) {
        if (!AuthHelper.isAdmin(session)) return "redirect:/login";

        model.addAttribute("events",        eventService.getAllEvents());
        model.addAttribute("totalEvents",   eventService.getTotalEvents());
        model.addAttribute("totalUpcoming", eventService.getTotalUpcoming());
        model.addAttribute("totalPast",     eventService.getTotalPast());
        model.addAttribute("currentUser",   AuthHelper.getLoggedInUser(session));
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
                            @ModelAttribute Event event,
                            HttpSession session, Model model) {
        if (!AuthHelper.isAdmin(session)) return "redirect:/login";

        event.setId(id);

        String error = eventService.updateEvent(event);
        if (error != null) {
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

        Event event = eventService.getEventById(id);
        if (event != null) {
            event.setStatus(status);
            eventService.updateEvent(event);
        }
        return "redirect:/admin/events";
    }
}
