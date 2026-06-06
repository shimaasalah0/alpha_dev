package com.jarena.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "event_registrations")
public class EventRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "registered_at")
    private LocalDateTime registeredAt;

    public long getId()                        { return id; }
    public void setId(long id)                 { this.id = id; }
    public Event getEvent()                    { return event; }
    public void setEvent(Event event)          { this.event = event; }
    public User getUser()                      { return user; }
    public void setUser(User user)             { this.user = user; }
    public LocalDateTime getRegisteredAt()     { return registeredAt; }
    public void setRegisteredAt(LocalDateTime t) { this.registeredAt = t; }
}
