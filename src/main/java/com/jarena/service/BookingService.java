package com.jarena.service;

import com.jarena.model.Booking;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface BookingService {
    /** Returns null on success, or an error message String on failure. */
    String createBooking(Booking booking);

    Booking getBookingById(long id);
    List<Booking> getBookingsByUser(long userId);
    List<Booking> getAllBookings();
    List<Booking> getBookingsByStatus(String status);
    void updateBookingStatus(long bookingId, String status);
    void cancelBooking(long bookingId);
    void deleteBooking(long bookingId);
    double calculateTotalPrice(long fieldId, LocalTime start, LocalTime end);
    List<Booking> getBookingsByUserAndStatus(long userId, String status);
    boolean isFieldAvailable(long fieldId, LocalDate date,
                             LocalTime startTime, LocalTime endTime,
                             Long excludeBookingId);
}
