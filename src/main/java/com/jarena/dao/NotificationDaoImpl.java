package com.jarena.dao;

import com.jarena.model.Notification;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class NotificationDaoImpl implements NotificationDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    @Transactional
    public void save(Notification notification) {
        sessionFactory.getCurrentSession().save(notification);
    }

    @Override
    @Transactional
    public Notification findById(long id) {
        return sessionFactory.getCurrentSession().get(Notification.class, id);
    }

    @Override
    @Transactional
    public List<Notification> findByUser(long userId) {
        return sessionFactory.getCurrentSession()
                .createQuery("FROM Notification n WHERE n.user.id = :userId ORDER BY n.createdAt DESC",
                        Notification.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    @Override
    @Transactional
    public List<Notification> findUnreadByUser(long userId) {
        return sessionFactory.getCurrentSession()
                .createQuery("FROM Notification n WHERE n.user.id = :userId AND n.read = false ORDER BY n.createdAt DESC",
                        Notification.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    @Override
    @Transactional
    public List<Notification> findByUserAndType(long userId, String type) {
        return sessionFactory.getCurrentSession()
                .createQuery("FROM Notification n WHERE n.user.id = :userId AND n.type = :type ORDER BY n.createdAt DESC",
                        Notification.class)
                .setParameter("userId", userId)
                .setParameter("type", type)
                .getResultList();
    }

    @Override
    @Transactional
    public void markAsRead(long id) {
        sessionFactory.getCurrentSession()
                .createQuery("UPDATE Notification n SET n.read = true WHERE n.id = :id")
                .setParameter("id", id)
                .executeUpdate();
    }

    @Override
    @Transactional
    public void markAllAsRead(long userId) {
        sessionFactory.getCurrentSession()
                .createQuery("UPDATE Notification n SET n.read = true WHERE n.user.id = :userId AND n.read = false")
                .setParameter("userId", userId)
                .executeUpdate();
    }

    @Override
    @Transactional
    public void delete(long id) {
        Notification n = sessionFactory.getCurrentSession().get(Notification.class, id);
        if (n != null) {
            sessionFactory.getCurrentSession().delete(n);
        }
    }

    @Override
    @Transactional
    public long countUnreadByUser(long userId) {
        Long c = sessionFactory.getCurrentSession()
                .createQuery("SELECT COUNT(n) FROM Notification n WHERE n.user.id = :userId AND n.read = false",
                        Long.class)
                .setParameter("userId", userId)
                .uniqueResult();
        return c != null ? c : 0L;
    }

    @Override
    @Transactional
    public List<Notification> findAll() {
        return sessionFactory.getCurrentSession()
                .createQuery("FROM Notification n ORDER BY n.createdAt DESC", Notification.class)
                .getResultList();
    }

    @Override
    @Transactional
    public void deleteAllReadByUser(long userId) {
        sessionFactory.getCurrentSession()
                .createQuery("DELETE FROM Notification n WHERE n.user.id = :userId AND n.read = true")
                .setParameter("userId", userId)
                .executeUpdate();
    }
}
