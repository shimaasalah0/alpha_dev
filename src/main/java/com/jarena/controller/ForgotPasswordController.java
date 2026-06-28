package com.jarena.controller;

import com.jarena.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ForgotPasswordController {

    @Autowired
    private UserService userService;

    @GetMapping("/forgot-password")
    public String showForgotPassword() {
        return "user-management/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam String email, Model model) {
        if (!userService.emailExists(email)) {
            model.addAttribute("error", "No account found with that email address.");
            return "user-management/forgot-password";
        }
        return "redirect:/reset-password?email=" + email;
    }

    @GetMapping("/reset-password")
    public String showResetPassword(@RequestParam String email, Model model) {
        if (!userService.emailExists(email)) {
            return "redirect:/forgot-password";
        }
        model.addAttribute("email", email);
        return "user-management/reset-password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam String email,
                                       @RequestParam String password,
                                       @RequestParam String confirmPassword,
                                       Model model) {
        if (!password.equals(confirmPassword)) {
            model.addAttribute("email", email);
            model.addAttribute("error", "Passwords do not match.");
            return "user-management/reset-password";
        }
        if (password.length() < 6) {
            model.addAttribute("email", email);
            model.addAttribute("error", "Password must be at least 6 characters.");
            return "user-management/reset-password";
        }
        userService.resetPassword(email, password);
        return "redirect:/login?resetSuccess=true";
    }
}
