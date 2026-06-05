package com.jarena.controller;

import com.jarena.model.Booking;
import com.jarena.model.User;
import com.jarena.service.BookingService;
import com.jarena.service.FieldService;
import com.jarena.util.AuthHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.LocalTime;
import java.util.*;

@Controller
@RequestMapping("/admin/bookings")
public class AdminBookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private FieldService fieldService;

    // ── GET /admin/bookings ──────────────────────────────────────────────────
    @GetMapping
    public String adminBookings(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String weekStart,
            HttpSession session, Model model) {

        if (!AuthHelper.isAdmin(session)) return "redirect:/login";

        User admin = AuthHelper.getLoggedInUser(session);

        List<Booking> bookings;
        if (status != null && !status.isEmpty() && !status.equals("ALL")) {
            bookings = bookingService.getBookingsByStatus(status);
        } else {
            bookings = bookingService.getAllBookings();
        }

        // ── week navigation ──────────────────────────────────────────────────
        LocalDate weekStartDate = resolveWeekStart(weekStart);

        List<String> weekDates    = new ArrayList<>();
        List<String> weekDayNames = new ArrayList<>();
        List<String> weekDayNums  = new ArrayList<>();
        String[] dayNames = {"Sun","Mon","Tue","Wed","Thu","Fri","Sat"};
        DateTimeFormatter dayNumFmt = DateTimeFormatter.ofPattern("MMM d");
        for (int i = 0; i < 7; i++) {
            LocalDate d = weekStartDate.plusDays(i);
            weekDates.add(d.toString());
            weekDayNames.add(dayNames[i]);
            weekDayNums.add(d.format(dayNumFmt));
        }

        List<String> timeSlots = new ArrayList<>();
        for (int h = 6; h <= 22; h++) {
            timeSlots.add(String.format("%02d:00", h));
        }

        Map<String, Booking> bookingMap = buildBookingMap(
                bookingService.getAllBookings(), weekDates);

        model.addAttribute("bookings",       bookings);
        model.addAttribute("fields",         fieldService.getAllFields());
        model.addAttribute("currentUser",    admin);
        model.addAttribute("weekDates",      weekDates);
        model.addAttribute("weekDayNames",   weekDayNames);
        model.addAttribute("weekDayNums",    weekDayNums);
        model.addAttribute("timeSlots",      timeSlots);
        model.addAttribute("bookingMap",     bookingMap);
        model.addAttribute("weekStart",      weekStartDate.toString());
        model.addAttribute("prevWeek",       weekStartDate.minusWeeks(1).toString());
        model.addAttribute("nextWeek",       weekStartDate.plusWeeks(1).toString());
        model.addAttribute("today",          LocalDate.now().toString());
        model.addAttribute("activeStatus",   status != null ? status : "ALL");
        model.addAttribute("selectedStatus", status != null ? status : "ALL");

        return "bookings/admin-bookings";
    }

    // ── POST /admin/bookings/status/{id} ─────────────────────────────────────
    @PostMapping("/status/{id}")
    public String updateStatus(@PathVariable long id,
                               @RequestParam String status,
                               HttpSession session) {
        if (!AuthHelper.isAdmin(session)) return "redirect:/login";
        bookingService.updateBookingStatus(id, status);
        return "redirect:/admin/bookings";
    }

    // ── GET /admin/bookings/delete/{id} ──────────────────────────────────────
    @GetMapping("/delete/{id}")
    public String deleteBooking(@PathVariable long id, HttpSession session) {
        if (!AuthHelper.isAdmin(session)) return "redirect:/login";
        bookingService.deleteBooking(id);
        return "redirect:/admin/bookings";
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private LocalDate resolveWeekStart(String param) {
        if (param != null && !param.isEmpty()) {
            try { return LocalDate.parse(param); } catch (Exception ignored) {}
        }
        LocalDate today = LocalDate.now();
        int shift = today.getDayOfWeek().getValue() % 7;
        return today.minusDays(shift);
    }

    private Map<String, Booking> buildBookingMap(List<Booking> bookings, List<String> weekDates) {
        Map<String, Booking> map = new HashMap<>();
        for (Booking b : bookings) {
            if (b.getBookingDate() == null || b.getStartTime() == null || b.getEndTime() == null) continue;
            if ("CANCELLED".equals(b.getStatus())) continue;
            String dateStr = b.getBookingDate().toString();
            if (!weekDates.contains(dateStr)) continue;
            LocalTime t = b.getStartTime();
            while (t.isBefore(b.getEndTime())) {
                map.put(dateStr + "_" + String.format("%02d:00", t.getHour()), b);
                t = t.plusHours(1);
            }
        }
        return map;
    }
}
