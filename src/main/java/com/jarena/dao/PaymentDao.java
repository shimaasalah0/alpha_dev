package com.jarena.dao;

import com.jarena.model.Payment;
import java.util.List;

public interface PaymentDao {
    void save(Payment payment);
    Payment findById(long id);
    Payment findByInvoiceId(String invoiceId);
    Payment findByBookingId(long bookingId);
    List<Payment> findByUser(long userId);
    List<Payment> findAll();
    List<Payment> findByStatus(String status);
    void update(Payment payment);
    void delete(long id);
    double getTotalRevenue();
    double getMonthlyRevenue(int month, int year);
    long countByStatus(String status);
    List<Payment> findRecentPayments(int limit);
}
