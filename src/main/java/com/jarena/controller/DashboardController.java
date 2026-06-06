package com.jarena.controller;

import com.jarena.model.Booking;
import com.jarena.model.User;
import com.jarena.service.BookingService;
import com.jarena.service.FieldService;
import com.jarena.service.UserService;
import com.jarena.util.AuthHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class DashboardController {

    @Autowired private BookingService bookingService;
    @Autowired private FieldService fieldService;
    @Autowired private UserService userService;

    @GetMapping("/dashboard")
    public String customerDashboard(HttpSession session, Model model) {
        if (!AuthHelper.isLoggedIn(session)) return "redirect:/login";

        User user = AuthHelper.getLoggedInUser(session);
        List<Booking> allBookings = bookingService.getBookingsByUser(user.getId());

        List<Booking> upcoming = allBookings.stream()
                .filter(b -> !b.getBookingDate().isBefore(LocalDate.now()))
                .filter(b -> !"CANCELLED".equals(b.getStatus()))
                .sorted((a, b2) -> a.getBookingDate().compareTo(b2.getBookingDate()))
                .collect(Collectors.toList());

        model.addAttribute("user",            user);
        model.addAttribute("totalBookings",   allBookings.size());
        model.addAttribute("availableFields", fieldService.getAvailableFields().size());
        model.addAttribute("upcomingBookings", upcoming);
        return "user-management/customer-dashboard";
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard(HttpSession session, Model model) {
        if (!AuthHelper.isAdmin(session)) return "redirect:/login";

        User user = AuthHelper.getLoggedInUser(session);
        List<Booking> allBookings = bookingService.getAllBookings();

        // Monthly revenue — sum totalPrice of bookings created this month
        LocalDate now = LocalDate.now();
        double monthlyRevenue = allBookings.stream()
                .filter(b -> b.getCreatedAt() != null
                        && b.getCreatedAt().getYear()  == now.getYear()
                        && b.getCreatedAt().getMonth() == now.getMonth()
                        && !"CANCELLED".equals(b.getStatus()))
                .mapToDouble(Booking::getTotalPrice)
                .sum();

        // Recent bookings for activity feed (latest 5)
        List<Booking> recentBookings = allBookings.stream()
                .sorted((a, b2) -> {
                    if (a.getCreatedAt() == null) return 1;
                    if (b2.getCreatedAt() == null) return -1;
                    return b2.getCreatedAt().compareTo(a.getCreatedAt());
                })
                .limit(5)
                .collect(Collectors.toList());

        model.addAttribute("user",            user);
        model.addAttribute("totalBookings",   allBookings.size());
        model.addAttribute("monthlyRevenue",  String.format("RM %.0f", monthlyRevenue));
        model.addAttribute("totalUsers",      userService.countAll());
        model.addAttribute("availableFields", fieldService.getAvailableFields().size());
        model.addAttribute("recentBookings",  recentBookings);
        return "user-management/admin-dashboard";
    }
}
