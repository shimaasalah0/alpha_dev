package com.jarena.controller;

import com.jarena.model.User;
import com.jarena.util.AuthHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;

@Controller
public class DashboardController {

    @GetMapping("/dashboard")
    public String customerDashboard(HttpSession session, Model model) {
        if (!AuthHelper.isLoggedIn(session)) {
            return "redirect:/login";
        }
        User user = AuthHelper.getLoggedInUser(session);
        model.addAttribute("user", user);
        return "user-management/customer-dashboard";
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard(HttpSession session, Model model) {
        if (!AuthHelper.isAdmin(session)) {
            return "redirect:/login";
        }
        User user = AuthHelper.getLoggedInUser(session);
        model.addAttribute("user", user);
        return "user-management/admin-dashboard";
    }
}
