package com.metro.kinetic.controller;

import com.metro.kinetic.model.*;
import com.metro.kinetic.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class TransactionController {

    @Autowired
    private EnergySalesService energySalesService;

    @Autowired
    private VendorBillService vendorBillService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private BudgetService budgetService;

    // ==========================================
    // ENERGY SALES & CUSTOMER INVOICES
    // ==========================================
    @GetMapping("/sales-orders")
    public ResponseEntity<List<SalesOrder>> getAllSalesOrders() {
        return ResponseEntity.ok(energySalesService.getAllSalesOrders());
    }

    @PostMapping("/sales-orders")
    public ResponseEntity<?> createEnergySaleOrder(@RequestBody Map<String, Object> payload) {
        try {
            Long customerId = Long.valueOf(payload.get("customerId").toString());
            Double energyKwh = Double.valueOf(payload.get("energyKwh").toString());
            Double pricePerKwh = Double.valueOf(payload.get("pricePerKwh").toString());

            LocalDate date = LocalDate.now();
            if (payload.get("orderDate") != null && !payload.get("orderDate").toString().isEmpty()) {
                date = LocalDate.parse(payload.get("orderDate").toString());
            }

            CustomerInvoice invoice = energySalesService.createEnergySale(customerId, date, energyKwh, pricePerKwh);
            return ResponseEntity.ok(invoice);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/invoices")
    public ResponseEntity<List<CustomerInvoice>> getAllInvoices() {
        return ResponseEntity.ok(energySalesService.getAllInvoices());
    }

    @PostMapping("/invoices")
    public ResponseEntity<?> createInvoiceDirect(@RequestBody Map<String, Object> payload) {
        return createEnergySaleOrder(payload);
    }

    // ==========================================
    // VENDOR BILLS
    // ==========================================
    @GetMapping("/vendor-bills")
    public ResponseEntity<List<VendorBill>> getAllVendorBills() {
        return ResponseEntity.ok(vendorBillService.getAllBills());
    }

    @PostMapping("/vendor-bills")
    public ResponseEntity<?> createVendorBill(@RequestBody Map<String, Object> payload) {
        try {
            Long vendorId = Long.valueOf(payload.get("vendorId").toString());
            String description = payload.get("description").toString();
            Integer quantity = Integer.valueOf(payload.get("quantity").toString());
            Double amountInr = Double.valueOf(payload.get("amountInr").toString());

            LocalDate date = LocalDate.now();
            if (payload.get("billDate") != null && !payload.get("billDate").toString().isEmpty()) {
                date = LocalDate.parse(payload.get("billDate").toString());
            }

            VendorBill bill = vendorBillService.createVendorBill(vendorId, date, description, quantity, amountInr);
            return ResponseEntity.ok(bill);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ==========================================
    // PAYMENTS
    // ==========================================
    @GetMapping("/payments")
    public ResponseEntity<List<Payment>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    @PostMapping("/payments")
    public ResponseEntity<?> recordPaymentGeneric(@RequestBody Map<String, Object> payload) {
        String type = payload.get("paymentType") != null ? payload.get("paymentType").toString() : "CUSTOMER";
        if ("VENDOR".equalsIgnoreCase(type)) {
            return recordVendorPayment(payload);
        } else {
            return recordCustomerPayment(payload);
        }
    }

    @PostMapping("/payments/customer")
    public ResponseEntity<?> recordCustomerPayment(@RequestBody Map<String, Object> payload) {
        try {
            Long invoiceId = Long.valueOf(payload.get("invoiceId").toString());
            String method = payload.get("paymentMethod") != null ? payload.get("paymentMethod").toString() : "Bank Transfer";

            LocalDate date = LocalDate.now();
            if (payload.get("paymentDate") != null && !payload.get("paymentDate").toString().isEmpty()) {
                date = LocalDate.parse(payload.get("paymentDate").toString());
            }

            Payment payment = paymentService.recordCustomerPayment(invoiceId, date, method);
            return ResponseEntity.ok(payment);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/payments/vendor")
    public ResponseEntity<?> recordVendorPayment(@RequestBody Map<String, Object> payload) {
        try {
            Long billId = Long.valueOf(payload.get("billId").toString());
            String method = payload.get("paymentMethod") != null ? payload.get("paymentMethod").toString() : "Bank Transfer";

            LocalDate date = LocalDate.now();
            if (payload.get("paymentDate") != null && !payload.get("paymentDate").toString().isEmpty()) {
                date = LocalDate.parse(payload.get("paymentDate").toString());
            }

            Payment payment = paymentService.recordVendorPayment(billId, date, method);
            return ResponseEntity.ok(payment);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ==========================================
    // BUDGETS
    // ==========================================
    @GetMapping("/budgets")
    public ResponseEntity<?> getAllBudgets() {
        return ResponseEntity.ok(budgetService.getBudgetsWithVariance());
    }

    @PostMapping("/budgets")
    public ResponseEntity<?> createBudget(@RequestBody Budget budget) {
        try {
            if (budget.getSectorId() == null || budget.getBudgetYear() == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Sector and Year are required"));
            }
            Budget saved = budgetService.saveBudget(budget);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
