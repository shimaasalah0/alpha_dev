package com.jarena.controller;

import com.jarena.model.Notification;
import com.jarena.model.User;
import com.jarena.service.NotificationService;
import com.jarena.service.UserService;
import com.jarena.util.AuthHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/notifications")
public class AdminNotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String adminNotifications(HttpSession session, Model model) {
        if (!AuthHelper.isAdmin(session)) return "redirect:/login";

        User currentUser = AuthHelper.getLoggedInUser(session);
        List<Notification> notifications = notificationService.getAllNotifications();

        List<User> allUsers = userService.getAllUsers();

        model.addAttribute("notifications", notifications);
        model.addAttribute("totalNotifications", notifications.size());
        model.addAttribute("allUsers", allUsers);
        model.addAttribute("currentUser", currentUser);
        return "notifications/admin-notifications";
    }

    @PostMapping("/send")
    public String sendNotification(HttpSession session,
                                   @RequestParam("title") String title,
                                   @RequestParam("message") String message,
                                   @RequestParam("type") String type,
                                   @RequestParam(value = "userIds", required = false) List<Long> userIds) {
        if (!AuthHelper.isAdmin(session)) return "redirect:/login";

        if (userIds == null || userIds.isEmpty()) {
            List<User> allUsers = userService.getAllUsers();
            userIds = allUsers.stream()
                    .map(User::getId)
                    .collect(Collectors.toList());
        }

        notificationService.sendToAllUsers(title, message, type, userIds);
        return "redirect:/admin/notifications";
    }

    @PostMapping("/delete/{id}")
    public String deleteNotification(HttpSession session, @PathVariable("id") long id) {
        if (!AuthHelper.isAdmin(session)) return "redirect:/login";
        notificationService.deleteNotification(id);
        return "redirect:/admin/notifications";
    }
}
