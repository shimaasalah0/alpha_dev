package com.jarena.service;

import com.jarena.model.Payment;
import java.util.List;

public interface PaymentService {
    String createPayment(long bookingId, long userId, double amount);
    Payment getPaymentById(long id);
    Payment getPaymentByInvoiceId(String invoiceId);
    Payment getPaymentByBookingId(long bookingId);
    List<Payment> getPaymentsByUser(long userId);
    List<Payment> getAllPayments();
    List<Payment> getPaymentsByStatus(String status);
    String updatePaymentStatus(long paymentId, String status);
    String processPayment(long paymentId);
    String refundPayment(long paymentId);
    void deletePayment(long id);
    double getTotalRevenue();
    double getMonthlyRevenue(int month, int year);
    long getPaidCount();
    long getPendingCount();
    long getRefundedCount();
    List<Payment> getRecentPayments(int limit);
}
