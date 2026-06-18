package com.jarena.dao;

import com.jarena.model.Notification;
import java.util.List;

public interface NotificationDao {
    void save(Notification notification);
    Notification findById(long id);
    List<Notification> findByUser(long userId);
    List<Notification> findUnreadByUser(long userId);
    List<Notification> findByUserAndType(long userId, String type);
    void markAsRead(long id);
    void markAllAsRead(long userId);
    void delete(long id);
    long countUnreadByUser(long userId);
    List<Notification> findAll();
    void deleteAllReadByUser(long userId);
}
