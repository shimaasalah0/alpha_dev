package com.jarena.dao;

import com.jarena.model.Booking;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public class BookingDaoImpl implements BookingDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    @Transactional
    public void save(Booking booking) {
        sessionFactory.getCurrentSession().save(booking);
    }

    @Override
    @Transactional
    public Booking findById(long id) {
        return sessionFactory.getCurrentSession().get(Booking.class, id);
    }

    @Override
    @Transactional
    public List<Booking> findByUser(long userId) {
        return sessionFactory.getCurrentSession()
                .createQuery(
                        "FROM Booking b WHERE b.user.id = :userId " +
                        "ORDER BY b.bookingDate DESC, b.startTime ASC", Booking.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    @Override
    @Transactional
    public List<Booking> findAll() {
        return sessionFactory.getCurrentSession()
                .createQuery(
                        "FROM Booking b ORDER BY b.bookingDate DESC, b.startTime ASC",
                        Booking.class)
                .getResultList();
    }

    @Override
    @Transactional
    public List<Booking> findByStatus(String status) {
        return sessionFactory.getCurrentSession()
                .createQuery(
                        "FROM Booking b WHERE b.status = :status " +
                        "ORDER BY b.bookingDate DESC, b.startTime ASC", Booking.class)
                .setParameter("status", status)
                .getResultList();
    }

    @Override
    @Transactional
    public void update(Booking booking) {
        sessionFactory.getCurrentSession().update(booking);
    }

    @Override
    @Transactional
    public void delete(long id) {
        Booking booking = sessionFactory.getCurrentSession().get(Booking.class, id);
        if (booking != null) {
            sessionFactory.getCurrentSession().delete(booking);
        }
    }

    @Override
    @Transactional
    public List<Booking> findByUserAndStatus(long userId, String status) {
        return sessionFactory.getCurrentSession()
                .createQuery(
                        "FROM Booking b WHERE b.user.id = :userId AND b.status = :status " +
                        "ORDER BY b.bookingDate DESC, b.startTime ASC", Booking.class)
                .setParameter("userId", userId)
                .setParameter("status", status)
                .getResultList();
    }

    @Override
    @Transactional
    public boolean hasConflict(long fieldId, LocalDate date,
                               LocalTime startTime, LocalTime endTime,
                               Long excludeBookingId) {

        String hql = "SELECT COUNT(b) FROM Booking b " +
                     "WHERE b.field.id = :fieldId " +
                     "AND b.bookingDate = :date " +
                     "AND b.status NOT IN ('CANCELLED') " +
                     "AND b.startTime < :endTime " +
                     "AND b.endTime > :startTime";

        if (excludeBookingId != null) {
            hql += " AND b.id != :excludeId";
        }

        Query<Long> query = sessionFactory.getCurrentSession()
                .createQuery(hql, Long.class)
                .setParameter("fieldId", fieldId)
                .setParameter("date", date)
                .setParameter("startTime", startTime)
                .setParameter("endTime", endTime);

        if (excludeBookingId != null) {
            query.setParameter("excludeId", excludeBookingId);
        }

        Long count = query.uniqueResult();
        return count != null && count > 0;
    }
}
