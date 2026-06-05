package com.jarena.dao;

import com.jarena.model.Booking;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface BookingDao {
    void save(Booking booking);
    Booking findById(long id);
    List<Booking> findByUser(long userId);
    List<Booking> findAll();
    List<Booking> findByStatus(String status);
    void update(Booking booking);
    void delete(long id);
    List<Booking> findByUserAndStatus(long userId, String status);
    boolean hasConflict(long fieldId, LocalDate date,
                        LocalTime startTime, LocalTime endTime,
                        Long excludeBookingId);
}
