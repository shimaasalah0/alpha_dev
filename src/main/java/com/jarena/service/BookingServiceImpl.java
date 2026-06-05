package com.jarena.service;

import com.jarena.dao.BookingDao;
import com.jarena.dao.FieldDao;
import com.jarena.model.Booking;
import com.jarena.model.Field;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {

    @Autowired
    private BookingDao bookingDao;

    @Autowired
    private FieldDao fieldDao;

    @Override
    public String createBooking(Booking booking) {

        // 1. End time must be after start time
        if (!booking.getEndTime().isAfter(booking.getStartTime())) {
            return "End time must be after start time.";
        }

        // 2. Booking date must not be in the past
        if (booking.getBookingDate().isBefore(LocalDate.now())) {
            return "Booking date cannot be in the past.";
        }

        // 3. Check for overlapping bookings (double-booking prevention)
        if (bookingDao.hasConflict(
                booking.getField().getId(),
                booking.getBookingDate(),
                booking.getStartTime(),
                booking.getEndTime(),
                null)) {
            return "This field is already booked for the selected time slot. " +
                   "Please choose a different time or field.";
        }

        // 4. Calculate price
        booking.setTotalPrice(calculateTotalPrice(
                booking.getField().getId(),
                booking.getStartTime(),
                booking.getEndTime()));

        // 5. Set defaults
        booking.setStatus("PENDING");
        booking.setCreatedAt(LocalDateTime.now());

        // 6. Persist
        bookingDao.save(booking);

        return null; // null = success
    }

    @Override
    public Booking getBookingById(long id) {
        return bookingDao.findById(id);
    }

    @Override
    public List<Booking> getBookingsByUser(long userId) {
        return bookingDao.findByUser(userId);
    }

    @Override
    public List<Booking> getAllBookings() {
        return bookingDao.findAll();
    }

    @Override
    public List<Booking> getBookingsByStatus(String status) {
        return bookingDao.findByStatus(status);
    }

    @Override
    public void updateBookingStatus(long bookingId, String status) {
        Booking booking = bookingDao.findById(bookingId);
        if (booking != null) {
            booking.setStatus(status);
            bookingDao.update(booking);
        }
    }

    @Override
    public void cancelBooking(long bookingId) {
        updateBookingStatus(bookingId, "CANCELLED");
    }

    @Override
    public void deleteBooking(long bookingId) {
        bookingDao.delete(bookingId);
    }

    @Override
    public double calculateTotalPrice(long fieldId, LocalTime start, LocalTime end) {
        Field field = fieldDao.findById(fieldId);
        if (field == null || start == null || end == null) return 0;
        long minutes = Duration.between(start, end).toMinutes();
        double hours = minutes / 60.0;
        return hours * field.getPricePerHour();
    }

    @Override
    public List<Booking> getBookingsByUserAndStatus(long userId, String status) {
        return bookingDao.findByUserAndStatus(userId, status);
    }

    @Override
    public boolean isFieldAvailable(long fieldId, LocalDate date,
                                    LocalTime startTime, LocalTime endTime,
                                    Long excludeBookingId) {
        return !bookingDao.hasConflict(fieldId, date, startTime, endTime, excludeBookingId);
    }
}
