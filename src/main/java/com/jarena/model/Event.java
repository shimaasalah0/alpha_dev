package com.jarena.model;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "event_date")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate eventDate;

    private String location;

    @Column(name = "image_url")
    private String imageUrl;

    private String status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public long getId()                          { return id; }
    public void setId(long id)                   { this.id = id; }
    public String getTitle()                     { return title; }
    public void setTitle(String title)           { this.title = title; }
    public String getDescription()               { return description; }
    public void setDescription(String d)         { this.description = d; }
    public LocalDate getEventDate()              { return eventDate; }
    public void setEventDate(LocalDate d)        { this.eventDate = d; }
    public String getLocation()                  { return location; }
    public void setLocation(String location)     { this.location = location; }
    public String getImageUrl()                  { return imageUrl; }
    public void setImageUrl(String url)          { this.imageUrl = url; }
    public String getStatus()                    { return status; }
    public void setStatus(String status)         { this.status = status; }
    public User getCreatedBy()                   { return createdBy; }
    public void setCreatedBy(User createdBy)     { this.createdBy = createdBy; }
    public LocalDateTime getCreatedAt()          { return createdAt; }
    public void setCreatedAt(LocalDateTime t)    { this.createdAt = t; }
}
