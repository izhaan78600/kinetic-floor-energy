package com.metro.kinetic.service;

import com.metro.kinetic.model.VendorBill;
import com.metro.kinetic.repository.VendorBillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class VendorBillService {

    @Autowired
    private VendorBillRepository vendorBillRepo;

    @Autowired
    private AccountingService accountingService;

    @Transactional
    public VendorBill createVendorBill(Long vendorId, LocalDate billDate, String description, Integer quantity, Double amountInr) {
        if (billDate == null) billDate = LocalDate.now();
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Bill description is required");
        }
        if (quantity == null || quantity <= 0) quantity = 1;
        if (amountInr == null || amountInr <= 0.0) {
            throw new IllegalArgumentException("Amount in INR must be greater than zero");
        }

        // 1. Create and Save Vendor Bill
        VendorBill bill = new VendorBill(vendorId, billDate, description, quantity, amountInr, "Unpaid");
        bill = vendorBillRepo.save(bill);

        // 2. Double-Entry Accounting:
        // Tile Repair/Purchase:
        // Debit: Maintenance Expense (or Inverter Expenses if description relates to inverters)
        // Credit: Vendor Payable
        String debitAccount = description.toLowerCase().contains("inverter") ? "Inverter Expenses" : "Maintenance Expense";

        accountingService.recordTransaction(
                "VENDOR_BILL",
                "VENDOR_BILL",
                bill.getId(),
                debitAccount,
                "Vendor Payable",
                amountInr,
                billDate
        );

        return bill;
    }

    public List<VendorBill> getAllBills() {
        return vendorBillRepo.findAllByOrderByIdDesc();
    }

    public VendorBill getBillById(Long id) {
        return vendorBillRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Bill not found: " + id));
    }
}
