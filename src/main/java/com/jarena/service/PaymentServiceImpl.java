package com.jarena.service;

import com.jarena.dao.BookingDao;
import com.jarena.dao.PaymentDao;
import com.jarena.model.Booking;
import com.jarena.model.Payment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentDao paymentDao;

    @Autowired
    private BookingDao bookingDao;

    @Override
    public String createPayment(long bookingId, long userId, double amount) {
        if (paymentDao.findByBookingId(bookingId) != null) {
            return "Payment already exists for this booking.";
        }

        Booking booking = bookingDao.findById(bookingId);
        if (booking == null) return "Booking not found.";

        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setUser(booking.getUser());
        payment.setInvoiceId(generateInvoiceId());
        payment.setAmount(amount);
        payment.setPaymentStatus("PENDING");
        payment.setPaymentDate(LocalDateTime.now());

        paymentDao.save(payment);
        return null;
    }

    @Override
    public Payment getPaymentById(long id) {
        return paymentDao.findById(id);
    }

    @Override
    public Payment getPaymentByInvoiceId(String invoiceId) {
        return paymentDao.findByInvoiceId(invoiceId);
    }

    @Override
    public Payment getPaymentByBookingId(long bookingId) {
        return paymentDao.findByBookingId(bookingId);
    }

    @Override
    public List<Payment> getPaymentsByUser(long userId) {
        return paymentDao.findByUser(userId);
    }

    @Override
    public List<Payment> getAllPayments() {
        return paymentDao.findAll();
    }

    @Override
    public List<Payment> getPaymentsByStatus(String status) {
        return paymentDao.findByStatus(status);
    }

    @Override
    public String updatePaymentStatus(long paymentId, String status) {
        if (!"PAID".equals(status) && !"PENDING".equals(status) && !"REFUNDED".equals(status)) {
            return "Invalid status. Must be PAID, PENDING, or REFUNDED.";
        }
        Payment payment = paymentDao.findById(paymentId);
        if (payment == null) return "Payment not found.";
        payment.setPaymentStatus(status);
        paymentDao.update(payment);
        return null;
    }

    @Override
    public String processPayment(long paymentId) {
        Payment payment = paymentDao.findById(paymentId);
        if (payment == null) return "Payment not found.";
        if (!"PENDING".equals(payment.getPaymentStatus())) {
            return "Only pending payments can be processed.";
        }

        payment.setPaymentStatus("PAID");
        paymentDao.update(payment);

        Booking booking = payment.getBooking();
        booking.setStatus("CONFIRMED");
        bookingDao.update(booking);

        return null;
    }

    @Override
    public String refundPayment(long paymentId) {
        Payment payment = paymentDao.findById(paymentId);
        if (payment == null) return "Payment not found.";
        if (!"PAID".equals(payment.getPaymentStatus())) {
            return "Only paid payments can be refunded.";
        }

        payment.setPaymentStatus("REFUNDED");
        paymentDao.update(payment);

        Booking booking = payment.getBooking();
        booking.setStatus("CANCELLED");
        bookingDao.update(booking);

        return null;
    }

    @Override
    public void deletePayment(long id) {
        paymentDao.delete(id);
    }

    @Override
    public double getTotalRevenue() {
        return paymentDao.getTotalRevenue();
    }

    @Override
    public double getMonthlyRevenue(int month, int year) {
        return paymentDao.getMonthlyRevenue(month, year);
    }

    @Override
    public long getPaidCount() {
        return paymentDao.countByStatus("PAID");
    }

    @Override
    public long getPendingCount() {
        return paymentDao.countByStatus("PENDING");
    }

    @Override
    public long getRefundedCount() {
        return paymentDao.countByStatus("REFUNDED");
    }

    @Override
    public List<Payment> getRecentPayments(int limit) {
        return paymentDao.findRecentPayments(limit);
    }

    private String generateInvoiceId() {
        int year = LocalDateTime.now().getYear();
        long count = paymentDao.findAll().size();
        return "INV-" + year + "-" + String.format("%03d", count + 1);
    }
}
