package com.jarena.controller;

import com.jarena.model.Notification;
import com.jarena.model.User;
import com.jarena.service.NotificationService;
import com.jarena.util.AuthHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/notifications")
    public String notifications(HttpSession session,
                                @RequestParam(value = "type", required = false) String type,
                                Model model) {
        if (!AuthHelper.isLoggedIn(session)) return "redirect:/login";

        User currentUser = AuthHelper.getLoggedInUser(session);
        long userId = currentUser.getId();

        List<Notification> notifications;
        if (type != null && !type.isEmpty()) {
            notifications = notificationService.getByUserAndType(userId, type);
        } else {
            notifications = notificationService.getByUser(userId);
        }

        long unreadCount = notificationService.getUnreadCount(userId);

        model.addAttribute("notifications", notifications);
        model.addAttribute("unreadCount", unreadCount);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("activeType", type != null ? type : "");
        return "notifications/customer-notifications";
    }

    @PostMapping("/notifications/read/{id}")
    public String markAsRead(HttpSession session, @PathVariable("id") long id) {
        if (!AuthHelper.isLoggedIn(session)) return "redirect:/login";
        notificationService.markAsRead(id);
        return "redirect:/notifications";
    }

    @PostMapping("/notifications/read-all")
    public String markAllAsRead(HttpSession session) {
        if (!AuthHelper.isLoggedIn(session)) return "redirect:/login";
        long userId = AuthHelper.getLoggedInUser(session).getId();
        notificationService.markAllAsRead(userId);
        return "redirect:/notifications";
    }

    @PostMapping("/notifications/delete/{id}")
    public String deleteNotification(HttpSession session, @PathVariable("id") long id) {
        if (!AuthHelper.isLoggedIn(session)) return "redirect:/login";
        notificationService.deleteNotification(id);
        return "redirect:/notifications";
    }

    @PostMapping("/notifications/clear-read")
    public String clearRead(HttpSession session) {
        if (!AuthHelper.isLoggedIn(session)) return "redirect:/login";
        long userId = AuthHelper.getLoggedInUser(session).getId();
        notificationService.clearReadNotifications(userId);
        return "redirect:/notifications";
    }
}
