package com.metro.kinetic.service;

import com.metro.kinetic.model.CustomerInvoice;
import com.metro.kinetic.model.Payment;
import com.metro.kinetic.model.VendorBill;
import com.metro.kinetic.repository.CustomerInvoiceRepository;
import com.metro.kinetic.repository.PaymentRepository;
import com.metro.kinetic.repository.VendorBillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepo;

    @Autowired
    private CustomerInvoiceRepository invoiceRepo;

    @Autowired
    private VendorBillRepository vendorBillRepo;

    @Autowired
    private AccountingService accountingService;

    // ==========================================
    // CUSTOMER PAYMENT
    // ==========================================
    @Transactional
    public Payment recordCustomerPayment(Long invoiceId, LocalDate paymentDate, String paymentMethod) {
        if (paymentDate == null) paymentDate = LocalDate.now();
        if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
            paymentMethod = "Bank Transfer";
        }

        CustomerInvoice invoice = invoiceRepo.findById(invoiceId)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found: " + invoiceId));

        if ("Paid".equalsIgnoreCase(invoice.getStatus())) {
            throw new IllegalStateException("Invoice #" + invoiceId + " is already marked as Paid");
        }

        // 1. Update Invoice Status
        invoice.setStatus("Paid");
        invoiceRepo.save(invoice);

        // 2. Create Payment Record
        Payment payment = new Payment(
                "CUSTOMER",
                invoice.getId(),
                paymentDate,
                invoice.getAmountInr(),
                paymentMethod,
                "Completed"
        );
        payment = paymentRepo.save(payment);

        // 3. Double-Entry Accounting:
        // Customer Payment:
        // Debit: Bank
        // Credit: Customer Receivable
        accountingService.recordTransaction(
                "CUSTOMER_PAYMENT",
                "PAYMENT",
                payment.getId(),
                "Bank",
                "Customer Receivable",
                invoice.getAmountInr(),
                paymentDate
        );

        return payment;
    }

    // ==========================================
    // VENDOR PAYMENT
    // ==========================================
    @Transactional
    public Payment recordVendorPayment(Long billId, LocalDate paymentDate, String paymentMethod) {
        if (paymentDate == null) paymentDate = LocalDate.now();
        if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
            paymentMethod = "Bank Transfer";
        }

        VendorBill bill = vendorBillRepo.findById(billId)
                .orElseThrow(() -> new IllegalArgumentException("Vendor bill not found: " + billId));

        if ("Paid".equalsIgnoreCase(bill.getStatus())) {
            throw new IllegalStateException("Bill #" + billId + " is already marked as Paid");
        }

        // 1. Update Bill Status
        bill.setStatus("Paid");
        vendorBillRepo.save(bill);

        // 2. Create Payment Record
        Payment payment = new Payment(
                "VENDOR",
                bill.getId(),
                paymentDate,
                bill.getAmountInr(),
                paymentMethod,
                "Completed"
        );
        payment = paymentRepo.save(payment);

        // 3. Double-Entry Accounting:
        // Vendor Payment:
        // Debit: Vendor Payable
        // Credit: Bank
        accountingService.recordTransaction(
                "VENDOR_PAYMENT",
                "PAYMENT",
                payment.getId(),
                "Vendor Payable",
                "Bank",
                bill.getAmountInr(),
                paymentDate
        );

        return payment;
    }

    public List<Payment> getAllPayments() {
        return paymentRepo.findAllByOrderByIdDesc();
    }
}
