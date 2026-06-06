package com.jarena.service;

import com.jarena.model.Event;
import java.util.List;

public interface EventService {
    String createEvent(Event event);
    Event getEventById(long id);
    List<Event> getAllEvents();
    List<Event> getEventsByStatus(String status);
    String updateEvent(Event event);
    void deleteEvent(long id);
    List<Event> getUpcomingEvents();
    List<Event> getPastEvents();
    long getTotalEvents();
    long getTotalUpcoming();
    long getTotalPast();
    String registerUserForEvent(long eventId, long userId);
    void unregisterUserFromEvent(long eventId, long userId);
    boolean isUserRegistered(long eventId, long userId);
    long getRegistrationCount(long eventId);
}
