package com.jarena.controller;

import com.jarena.model.User;
import com.jarena.service.UserService;
import com.jarena.util.AuthHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String root(HttpSession session) {
        return AuthHelper.isLoggedIn(session)
                ? (AuthHelper.isAdmin(session) ? "redirect:/admin/dashboard" : "redirect:/dashboard")
                : "redirect:/login";
    }

    @GetMapping("/login")
    public String showLogin(HttpSession session) {
        if (AuthHelper.isLoggedIn(session)) {
            return AuthHelper.isAdmin(session) ? "redirect:/admin/dashboard" : "redirect:/dashboard";
        }
        return "user-management/login";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam String email,
                               @RequestParam String password,
                               HttpSession session,
                               Model model) {
        User user = userService.login(email, password);
        if (user == null) {
            model.addAttribute("error", "Invalid email or password.");
            return "user-management/login";
        }
        AuthHelper.setLoggedInUser(session, user);
        return "ADMIN".equals(user.getRole()) ? "redirect:/admin/dashboard" : "redirect:/dashboard";
    }

    @GetMapping("/register")
    public String showRegister(HttpSession session, Model model) {
        if (AuthHelper.isLoggedIn(session)) {
            return "redirect:/dashboard";
        }
        model.addAttribute("user", new User());
        return "user-management/register";
    }

    @PostMapping("/register")
    public String processRegister(@ModelAttribute("user") User user,
                                  HttpSession session,
                                  Model model) {
        if (userService.emailExists(user.getEmail())) {
            model.addAttribute("error", "An account with this email already exists.");
            return "user-management/register";
        }
        userService.register(user);
        return "redirect:/login?registered=true";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        AuthHelper.logout(session);
        return "redirect:/login";
    }
}
