package com.jarena.controller;

import com.jarena.model.Payment;
import com.jarena.model.User;
import com.jarena.service.PaymentService;
import com.jarena.util.AuthHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class AdminPaymentController {

    @Autowired
    private PaymentService paymentService;

    @GetMapping("/admin/payments")
    public String adminPayments(
            @RequestParam(required = false) String status,
            HttpSession session, Model model) {

        if (!AuthHelper.isAdmin(session)) return "redirect:/login";

        User user = AuthHelper.getLoggedInUser(session);

        List<Payment> payments;
        if (status != null && !status.isEmpty()) {
            payments = paymentService.getPaymentsByStatus(status);
        } else {
            payments = paymentService.getAllPayments();
        }

        LocalDateTime now = LocalDateTime.now();
        int currentMonth = now.getMonthValue();
        int currentYear  = now.getYear();

        model.addAttribute("payments",       payments);
        model.addAttribute("totalRevenue",   paymentService.getTotalRevenue());
        model.addAttribute("monthlyRevenue", paymentService.getMonthlyRevenue(currentMonth, currentYear));
        model.addAttribute("paidCount",      paymentService.getPaidCount());
        model.addAttribute("pendingCount",   paymentService.getPendingCount());
        model.addAttribute("refundedCount",  paymentService.getRefundedCount());
        model.addAttribute("recentPayments", paymentService.getRecentPayments(5));
        model.addAttribute("activeStatus",   status != null && !status.isEmpty() ? status : "ALL");
        model.addAttribute("currentUser",    user);

        return "payments/admin-payments";
    }

    @PostMapping("/admin/payments/status/{id}")
    public String updateStatus(
            @PathVariable long id,
            @RequestParam String status,
            HttpSession session) {

        if (!AuthHelper.isAdmin(session)) return "redirect:/login";
        paymentService.updatePaymentStatus(id, status);
        return "redirect:/admin/payments";
    }

    @PostMapping("/admin/payments/process/{id}")
    public String processPayment(@PathVariable long id, HttpSession session) {
        if (!AuthHelper.isAdmin(session)) return "redirect:/login";
        paymentService.processPayment(id);
        return "redirect:/admin/payments";
    }

    @PostMapping("/admin/payments/refund/{id}")
    public String refundPayment(@PathVariable long id, HttpSession session) {
        if (!AuthHelper.isAdmin(session)) return "redirect:/login";
        paymentService.refundPayment(id);
        return "redirect:/admin/payments";
    }

    @PostMapping("/admin/payments/delete/{id}")
    public String deletePayment(@PathVariable long id, HttpSession session) {
        if (!AuthHelper.isAdmin(session)) return "redirect:/login";
        paymentService.deletePayment(id);
        return "redirect:/admin/payments";
    }
}
