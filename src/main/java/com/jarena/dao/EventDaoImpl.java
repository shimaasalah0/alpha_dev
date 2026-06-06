package com.jarena.dao;

import com.jarena.model.Event;
import com.jarena.model.EventRegistration;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class EventDaoImpl implements EventDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    @Transactional
    public void save(Event event) {
        sessionFactory.getCurrentSession().save(event);
    }

    @Override
    @Transactional
    public Event findById(long id) {
        return sessionFactory.getCurrentSession().get(Event.class, id);
    }

    @Override
    @Transactional
    public List<Event> findAll() {
        return sessionFactory.getCurrentSession()
                .createQuery("FROM Event e ORDER BY e.eventDate DESC", Event.class)
                .getResultList();
    }

    @Override
    @Transactional
    public List<Event> findByStatus(String status) {
        return sessionFactory.getCurrentSession()
                .createQuery("FROM Event e WHERE e.status = :status ORDER BY e.eventDate ASC", Event.class)
                .setParameter("status", status)
                .getResultList();
    }

    @Override
    @Transactional
    public void update(Event event) {
        sessionFactory.getCurrentSession().update(event);
    }

    @Override
    @Transactional
    public void delete(long id) {
        Event e = sessionFactory.getCurrentSession().get(Event.class, id);
        if (e != null) {
            sessionFactory.getCurrentSession().delete(e);
        }
    }

    @Override
    @Transactional
    public List<Event> findUpcoming() {
        return sessionFactory.getCurrentSession()
                .createQuery("FROM Event e WHERE e.status = 'UPCOMING' ORDER BY e.eventDate ASC", Event.class)
                .getResultList();
    }

    @Override
    @Transactional
    public List<Event> findPast() {
        return sessionFactory.getCurrentSession()
                .createQuery("FROM Event e WHERE e.status = 'COMPLETED' ORDER BY e.eventDate DESC", Event.class)
                .getResultList();
    }

    @Override
    @Transactional
    public long countAll() {
        Long c = sessionFactory.getCurrentSession()
                .createQuery("SELECT COUNT(e) FROM Event e", Long.class).uniqueResult();
        return c != null ? c : 0L;
    }

    @Override
    @Transactional
    public long countUpcoming() {
        Long c = sessionFactory.getCurrentSession()
                .createQuery("SELECT COUNT(e) FROM Event e WHERE e.status = 'UPCOMING'", Long.class).uniqueResult();
        return c != null ? c : 0L;
    }

    @Override
    @Transactional
    public long countPast() {
        Long c = sessionFactory.getCurrentSession()
                .createQuery("SELECT COUNT(e) FROM Event e WHERE e.status = 'COMPLETED'", Long.class).uniqueResult();
        return c != null ? c : 0L;
    }

    @Override
    @Transactional
    public void saveRegistration(EventRegistration registration) {
        sessionFactory.getCurrentSession().save(registration);
    }

    @Override
    @Transactional
    public boolean isUserRegistered(long eventId, long userId) {
        Long c = sessionFactory.getCurrentSession()
                .createQuery(
                        "SELECT COUNT(r) FROM EventRegistration r " +
                        "WHERE r.event.id = :eventId AND r.user.id = :userId", Long.class)
                .setParameter("eventId", eventId)
                .setParameter("userId", userId)
                .uniqueResult();
        return c != null && c > 0;
    }

    @Override
    @Transactional
    public long countRegistrations(long eventId) {
        Long c = sessionFactory.getCurrentSession()
                .createQuery(
                        "SELECT COUNT(r) FROM EventRegistration r WHERE r.event.id = :eventId", Long.class)
                .setParameter("eventId", eventId)
                .uniqueResult();
        return c != null ? c : 0L;
    }

    @Override
    @Transactional
    public List<EventRegistration> findRegistrationsByEvent(long eventId) {
        return sessionFactory.getCurrentSession()
                .createQuery(
                        "FROM EventRegistration r WHERE r.event.id = :eventId ORDER BY r.registeredAt ASC",
                        EventRegistration.class)
                .setParameter("eventId", eventId)
                .getResultList();
    }

    @Override
    @Transactional
    public void deleteRegistration(long eventId, long userId) {
        sessionFactory.getCurrentSession()
                .createQuery(
                        "DELETE FROM EventRegistration r " +
                        "WHERE r.event.id = :eventId AND r.user.id = :userId")
                .setParameter("eventId", eventId)
                .setParameter("userId", userId)
                .executeUpdate();
    }
}
