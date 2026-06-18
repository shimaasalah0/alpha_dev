package com.jarena.service;

import com.jarena.model.Notification;
import java.util.List;

public interface NotificationService {
    void createNotification(long userId, String title, String message, String type);
    Notification getById(long id);
    List<Notification> getByUser(long userId);
    List<Notification> getUnreadByUser(long userId);
    List<Notification> getByUserAndType(long userId, String type);
    void markAsRead(long id);
    void markAllAsRead(long userId);
    void deleteNotification(long id);
    long getUnreadCount(long userId);
    List<Notification> getAllNotifications();
    void clearReadNotifications(long userId);
    void sendToAllUsers(String title, String message, String type, List<Long> userIds);
}
