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
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @GetMapping("/payments")
    public String customerPayments(
            @RequestParam(required = false) String status,
            HttpSession session, Model model) {

        if (!AuthHelper.isLoggedIn(session)) return "redirect:/login";

        User user = AuthHelper.getLoggedInUser(session);
        long userId = user.getId();

        List<Payment> allUserPayments = paymentService.getPaymentsByUser(userId);

        double totalPaid = allUserPayments.stream()
                .filter(p -> "PAID".equals(p.getPaymentStatus()))
                .mapToDouble(Payment::getAmount)
                .sum();

        List<Payment> payments = allUserPayments;
        if (status != null && !status.isEmpty()) {
            final String s = status;
            payments = allUserPayments.stream()
                    .filter(p -> s.equals(p.getPaymentStatus()))
                    .collect(Collectors.toList());
        }

        model.addAttribute("payments",      payments);
        model.addAttribute("totalPaid",     totalPaid);
        model.addAttribute("currentUser",   user);
        model.addAttribute("activeStatus",  status != null && !status.isEmpty() ? status : "ALL");

        return "payments/customer-payments";
    }

    @GetMapping("/payments/{id}")
    public String paymentDetail(@PathVariable long id, HttpSession session, Model model) {
        if (!AuthHelper.isLoggedIn(session)) return "redirect:/login";

        User user = AuthHelper.getLoggedInUser(session);
        Payment payment = paymentService.getPaymentById(id);

        model.addAttribute("payment",     payment);
        model.addAttribute("currentUser", user);

        return "payments/payment-detail";
    }

    @PostMapping("/payments/pay/{id}")
    public String payNow(@PathVariable long id, HttpSession session) {
        if (!AuthHelper.isLoggedIn(session)) return "redirect:/login";
        paymentService.processPayment(id);
        return "redirect:/payments";
    }
}
