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
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private FieldService fieldService;

    // ── GET /bookings ────────────────────────────────────────────────────────
    @GetMapping("/bookings")
    public String customerBookings(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String weekStart,
            HttpSession session, Model model) {

        if (!AuthHelper.isLoggedIn(session)) return "redirect:/login";

        User user = AuthHelper.getLoggedInUser(session);
        long userId = user.getId();

        List<Booking> bookings;
        if (status != null && !status.isEmpty() && !status.equals("ALL")) {
            bookings = bookingService.getBookingsByUserAndStatus(userId, status);
        } else {
            bookings = bookingService.getBookingsByUser(userId);
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

        // ── time slots 06:00–22:00 ───────────────────────────────────────────
        List<String> timeSlots = new ArrayList<>();
        for (int h = 6; h <= 22; h++) {
            timeSlots.add(String.format("%02d:00", h));
        }

        // ── booking map for week grid ────────────────────────────────────────
        Map<String, Booking> bookingMap = buildBookingMap(
                bookingService.getBookingsByUser(userId), weekDates);

        model.addAttribute("bookings",       bookings);
        model.addAttribute("fields",         fieldService.getAllFields());
        model.addAttribute("currentUser",    user);
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

        return "bookings/customer-bookings";
    }

    // ── GET /bookings/new ────────────────────────────────────────────────────
    @GetMapping("/bookings/new")
    public String newBookingForm(
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            HttpSession session, Model model) {
        if (!AuthHelper.isLoggedIn(session)) return "redirect:/login";
        model.addAttribute("fields",   fieldService.getAvailableFields());
        model.addAttribute("booking",  new Booking());
        model.addAttribute("preDate",  date      != null ? date      : LocalDate.now().toString());
        model.addAttribute("preStart", startTime != null ? startTime : "09:00");
        model.addAttribute("preEnd",   endTime   != null ? endTime   : "11:00");
        return "bookings/new-booking";
    }

    // ── POST /bookings/create ────────────────────────────────────────────────
    @PostMapping("/bookings/create")
    public String createBooking(
            @RequestParam long fieldId,
            @RequestParam String bookingDate,
            @RequestParam String startTime,
            @RequestParam String endTime,
            HttpSession session, Model model) {

        if (!AuthHelper.isLoggedIn(session)) return "redirect:/login";

        User user = AuthHelper.getLoggedInUser(session);

        Booking b = new Booking();
        b.setUser(user);
        b.setField(fieldService.getFieldById(fieldId));
        b.setBookingDate(LocalDate.parse(bookingDate));
        b.setStartTime(LocalTime.parse(startTime));
        b.setEndTime(LocalTime.parse(endTime));

        String error = bookingService.createBooking(b);
        if (error != null) {
            model.addAttribute("error",  error);
            model.addAttribute("fields", fieldService.getAvailableFields());
            model.addAttribute("booking", b);
            return "bookings/new-booking";
        }

        return "redirect:/bookings";
    }

    // ── GET /bookings/cancel/{id} ────────────────────────────────────────────
    @GetMapping("/bookings/cancel/{id}")
    public String cancelBooking(@PathVariable long id, HttpSession session) {
        if (!AuthHelper.isLoggedIn(session)) return "redirect:/login";
        User user    = AuthHelper.getLoggedInUser(session);
        Booking booking = bookingService.getBookingById(id);
        if (booking != null && booking.getUser().getId() == user.getId()) {
            bookingService.cancelBooking(id);
        }
        return "redirect:/bookings";
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private LocalDate resolveWeekStart(String param) {
        if (param != null && !param.isEmpty()) {
            try { return LocalDate.parse(param); } catch (Exception ignored) {}
        }
        LocalDate today = LocalDate.now();
        int shift = today.getDayOfWeek().getValue() % 7; // Sunday = 0 shift
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
