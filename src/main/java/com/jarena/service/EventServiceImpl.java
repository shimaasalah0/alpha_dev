package com.jarena.service;

import com.jarena.dao.EventDao;
import com.jarena.dao.UserDao;
import com.jarena.model.Event;
import com.jarena.model.EventRegistration;
import com.jarena.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class EventServiceImpl implements EventService {

    @Autowired private EventDao eventDao;
    @Autowired private UserDao  userDao;

    @Override
    public String createEvent(Event event) {
        if (event.getTitle() == null || event.getTitle().trim().isEmpty()) {
            return "Event title is required.";
        }
        if (event.getEventDate() == null || event.getEventDate().isBefore(LocalDate.now())) {
            return "Event date cannot be in the past.";
        }
        event.setStatus("UPCOMING");
        event.setCreatedAt(LocalDateTime.now());
        eventDao.save(event);
        return null;
    }

    @Override
    public Event getEventById(long id) {
        return eventDao.findById(id);
    }

    @Override
    public List<Event> getAllEvents() {
        return eventDao.findAll();
    }

    @Override
    public List<Event> getEventsByStatus(String status) {
        return eventDao.findByStatus(status);
    }

    @Override
    public String updateEvent(Event event) {
        if (event.getTitle() == null || event.getTitle().trim().isEmpty()) {
            return "Event title is required.";
        }
        if (event.getEventDate() == null || event.getEventDate().isBefore(LocalDate.now())) {
            return "Event date cannot be in the past.";
        }
        eventDao.update(event);
        return null;
    }

    @Override
    public void deleteEvent(long id) {
        eventDao.delete(id);
    }

    @Override
    public List<Event> getUpcomingEvents() {
        return eventDao.findUpcoming();
    }

    @Override
    public List<Event> getPastEvents() {
        return eventDao.findPast();
    }

    @Override
    public long getTotalEvents()   { return eventDao.countAll(); }

    @Override
    public long getTotalUpcoming() { return eventDao.countUpcoming(); }

    @Override
    public long getTotalPast()     { return eventDao.countPast(); }

    @Override
    public String registerUserForEvent(long eventId, long userId) {
        Event event = eventDao.findById(eventId);
        if (event == null) {
            return "Event not found.";
        }
        if (!"UPCOMING".equals(event.getStatus())) {
            return "Registration is not open for this event.";
        }
        if (eventDao.isUserRegistered(eventId, userId)) {
            return "You are already registered for this event.";
        }
        User user = userDao.findById(userId);

        EventRegistration reg = new EventRegistration();
        reg.setEvent(event);
        reg.setUser(user);
        reg.setRegisteredAt(LocalDateTime.now());
        eventDao.saveRegistration(reg);
        return null;
    }

    @Override
    public void unregisterUserFromEvent(long eventId, long userId) {
        eventDao.deleteRegistration(eventId, userId);
    }

    @Override
    public boolean isUserRegistered(long eventId, long userId) {
        return eventDao.isUserRegistered(eventId, userId);
    }

    @Override
    public long getRegistrationCount(long eventId) {
        return eventDao.countRegistrations(eventId);
    }
}
