package com.jarena.controller;

import com.jarena.model.User;
import com.jarena.service.UserService;
import com.jarena.util.AuthHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class AdminUserController {

    @Autowired
    private UserService userService;

    @GetMapping("/admin/users")
    public String listUsers(HttpSession session, Model model) {
        if (!AuthHelper.isAdmin(session)) return "redirect:/login";

        List<User> users = userService.getAllUsers();

        model.addAttribute("users",       users);
        model.addAttribute("totalUsers",  users.size());
        model.addAttribute("currentUser", AuthHelper.getLoggedInUser(session));

        return "user-management/admin-users";
    }
}
