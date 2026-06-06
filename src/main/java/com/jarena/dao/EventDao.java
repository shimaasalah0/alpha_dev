package com.jarena.dao;

import com.jarena.model.Event;
import com.jarena.model.EventRegistration;
import java.util.List;

public interface EventDao {
    void save(Event event);
    Event findById(long id);
    List<Event> findAll();
    List<Event> findByStatus(String status);
    void update(Event event);
    void delete(long id);
    List<Event> findUpcoming();
    List<Event> findPast();
    long countAll();
    long countUpcoming();
    long countPast();
    void saveRegistration(EventRegistration registration);
    boolean isUserRegistered(long eventId, long userId);
    long countRegistrations(long eventId);
    List<EventRegistration> findRegistrationsByEvent(long eventId);
    void deleteRegistration(long eventId, long userId);
}
