package com.jarena.dao;

import com.jarena.model.Payment;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class PaymentDaoImpl implements PaymentDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    @Transactional
    public void save(Payment payment) {
        sessionFactory.getCurrentSession().save(payment);
    }

    @Override
    @Transactional
    public Payment findById(long id) {
        return sessionFactory.getCurrentSession().get(Payment.class, id);
    }

    @Override
    @Transactional
    public Payment findByInvoiceId(String invoiceId) {
        List<Payment> results = sessionFactory.getCurrentSession()
                .createQuery("FROM Payment p WHERE p.invoiceId = :invoiceId", Payment.class)
                .setParameter("invoiceId", invoiceId)
                .getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    @Transactional
    public Payment findByBookingId(long bookingId) {
        List<Payment> results = sessionFactory.getCurrentSession()
                .createQuery("FROM Payment p WHERE p.booking.id = :bookingId", Payment.class)
                .setParameter("bookingId", bookingId)
                .getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    @Transactional
    public List<Payment> findByUser(long userId) {
        return sessionFactory.getCurrentSession()
                .createQuery(
                        "FROM Payment p WHERE p.user.id = :userId ORDER BY p.paymentDate DESC",
                        Payment.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    @Override
    @Transactional
    public List<Payment> findAll() {
        return sessionFactory.getCurrentSession()
                .createQuery("FROM Payment p ORDER BY p.paymentDate DESC", Payment.class)
                .getResultList();
    }

    @Override
    @Transactional
    public List<Payment> findByStatus(String status) {
        return sessionFactory.getCurrentSession()
                .createQuery(
                        "FROM Payment p WHERE p.paymentStatus = :status ORDER BY p.paymentDate DESC",
                        Payment.class)
                .setParameter("status", status)
                .getResultList();
    }

    @Override
    @Transactional
    public void update(Payment payment) {
        sessionFactory.getCurrentSession().update(payment);
    }

    @Override
    @Transactional
    public void delete(long id) {
        Payment payment = sessionFactory.getCurrentSession().get(Payment.class, id);
        if (payment != null) {
            sessionFactory.getCurrentSession().delete(payment);
        }
    }

    @Override
    @Transactional
    public double getTotalRevenue() {
        Object result = sessionFactory.getCurrentSession()
                .createQuery(
                        "SELECT COALESCE(SUM(p.amount), 0.0) FROM Payment p " +
                        "WHERE p.paymentStatus = 'PAID'")
                .uniqueResult();
        return result instanceof Number ? ((Number) result).doubleValue() : 0.0;
    }

    @Override
    @Transactional
    public double getMonthlyRevenue(int month, int year) {
        Object result = sessionFactory.getCurrentSession()
                .createQuery(
                        "SELECT COALESCE(SUM(p.amount), 0.0) FROM Payment p " +
                        "WHERE p.paymentStatus = 'PAID' " +
                        "AND MONTH(p.paymentDate) = :month " +
                        "AND YEAR(p.paymentDate) = :year")
                .setParameter("month", month)
                .setParameter("year", year)
                .uniqueResult();
        return result instanceof Number ? ((Number) result).doubleValue() : 0.0;
    }

    @Override
    @Transactional
    public long countByStatus(String status) {
        Long count = (Long) sessionFactory.getCurrentSession()
                .createQuery(
                        "SELECT COUNT(p) FROM Payment p WHERE p.paymentStatus = :status")
                .setParameter("status", status)
                .uniqueResult();
        return count != null ? count : 0L;
    }

    @Override
    @Transactional
    public List<Payment> findRecentPayments(int limit) {
        return sessionFactory.getCurrentSession()
                .createQuery("FROM Payment p ORDER BY p.paymentDate DESC", Payment.class)
                .setMaxResults(limit)
                .getResultList();
    }
}
