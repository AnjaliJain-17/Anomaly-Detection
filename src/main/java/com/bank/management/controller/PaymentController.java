//package com.bank.management.controller;
//
//@RestController
//@RequestMapping("/payments")
//public class PaymentController {
//
//    @Autowired
//    private PaymentService paymentService;
//
//    @GetMapping("")
//    public List<Payment> getAllPayments() {
//        return paymentService.getAllPayments();
//    }
//
//    @GetMapping("/{id}")
//    public Payment getPaymentById(@PathVariable Long id) {
//        return paymentService.getPaymentById(id);
//    }
//
//    @PostMapping("")
//    public Payment createPayment(@RequestBody Payment payment) {
//        return paymentService.createPayment(payment);
//    }
//
//    @PutMapping("/{id}")
//    public Payment updatePayment(@PathVariable Long id, @RequestBody Payment payment) {
//        return paymentService.updatePayment(id, payment);
//    }
//
//    @DeleteMapping("/{id}")
//    public void deletePayment(@PathVariable Long id) {
//        paymentService.deletePayment(id);
//    }
//}
//
