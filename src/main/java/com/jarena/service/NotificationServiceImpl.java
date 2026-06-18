package com.jarena.service;

import com.jarena.dao.NotificationDao;
import com.jarena.dao.UserDao;
import com.jarena.model.Notification;
import com.jarena.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationDao notificationDao;

    @Autowired
    private UserDao userDao;

    @Override
    public void createNotification(long userId, String title, String message, String type) {
        User user = userDao.findById(userId);
        if (user == null) return;

        Notification n = new Notification();
        n.setUser(user);
        n.setTitle(title);
        n.setMessage(message);
        n.setType(type);
        n.setRead(false);
        n.setCreatedAt(LocalDateTime.now());
        notificationDao.save(n);
    }

    @Override
    public Notification getById(long id) {
        return notificationDao.findById(id);
    }

    @Override
    public List<Notification> getByUser(long userId) {
        return notificationDao.findByUser(userId);
    }

    @Override
    public List<Notification> getUnreadByUser(long userId) {
        return notificationDao.findUnreadByUser(userId);
    }

    @Override
    public List<Notification> getByUserAndType(long userId, String type) {
        return notificationDao.findByUserAndType(userId, type);
    }

    @Override
    public void markAsRead(long id) {
        notificationDao.markAsRead(id);
    }

    @Override
    public void markAllAsRead(long userId) {
        notificationDao.markAllAsRead(userId);
    }

    @Override
    public void deleteNotification(long id) {
        notificationDao.delete(id);
    }

    @Override
    public long getUnreadCount(long userId) {
        return notificationDao.countUnreadByUser(userId);
    }

    @Override
    public List<Notification> getAllNotifications() {
        return notificationDao.findAll();
    }

    @Override
    public void clearReadNotifications(long userId) {
        notificationDao.deleteAllReadByUser(userId);
    }

    @Override
    public void sendToAllUsers(String title, String message, String type, List<Long> userIds) {
        for (Long userId : userIds) {
            createNotification(userId, title, message, type);
        }
    }
}
