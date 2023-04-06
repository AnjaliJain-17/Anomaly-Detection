package com.bank.management.controller;

import com.bank.management.domain.Payment;
import com.bank.management.services.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);

    @Autowired
    private PaymentService paymentService;

    @GetMapping("")
    public List<Payment> getAllPayments() {
        log.info("Inside getAllPayments Controller");
        return paymentService.getAllPayments();
    }

    @GetMapping("/{id}")
    public Optional<Payment> getPaymentById(@PathVariable Long id) {
        log.info("Inside getPaymentById Controller");
        return paymentService.getPaymentById(id);
    }

    @PostMapping("")
    public Payment createPayment(@RequestBody Payment payment) {
        log.info("Inside createPayment Controller");
        return paymentService.createPayment(payment);
    }

    @PutMapping("/{id}")
    public Payment updatePayment(@PathVariable Long id, @RequestBody Payment payment) {
        log.info("Inside updatePayment Controller");
        return paymentService.updatePayment(id, payment);
    }

    @DeleteMapping("/{id}")
    public Object deletePayment(@PathVariable Long id) {
        log.info("Inside deletePayment Controller");
        return paymentService.deletePayment(id);
    }
}

