package com.metro.kinetic.service;

import com.metro.kinetic.model.CustomerInvoice;
import com.metro.kinetic.model.SalesOrder;
import com.metro.kinetic.repository.CustomerInvoiceRepository;
import com.metro.kinetic.repository.SalesOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class EnergySalesService {

    @Autowired
    private SalesOrderRepository salesOrderRepo;

    @Autowired
    private CustomerInvoiceRepository invoiceRepo;

    @Autowired
    private AccountingService accountingService;

    @Transactional
    public CustomerInvoice createEnergySale(Long customerId, LocalDate orderDate, Double energyKwh, Double pricePerKwh) {
        if (orderDate == null) orderDate = LocalDate.now();
        if (energyKwh == null || energyKwh <= 0.0) {
            throw new IllegalArgumentException("Energy (kWh) must be greater than zero");
        }
        if (pricePerKwh == null || pricePerKwh <= 0.0) {
            throw new IllegalArgumentException("Price per kWh must be greater than zero");
        }

        // 1. Calculate Total Amount in INR: Total Amount = Energy kWh × Price per kWh
        double totalAmountInr = Math.round(energyKwh * pricePerKwh * 100.0) / 100.0;

        // 2. Create and Save Sales Order
        SalesOrder order = new SalesOrder(customerId, orderDate, energyKwh, pricePerKwh, totalAmountInr, "Invoiced");
        order = salesOrderRepo.save(order);

        // 3. Create and Save Customer Invoice from the Sales Order
        LocalDate dueDate = orderDate.plusDays(15);
        CustomerInvoice invoice = new CustomerInvoice(order.getId(), orderDate, dueDate, totalAmountInr, "Unpaid");
        invoice = invoiceRepo.save(invoice);

        // 4. Double-Entry Accounting:
        // Energy Sale:
        // Debit: Customer Receivable
        // Credit: Harvested Power Sales Revenue
        accountingService.recordTransaction(
                "ENERGY_SALE",
                "SALES_INVOICE",
                invoice.getId(),
                "Customer Receivable",
                "Harvested Power Sales Revenue",
                totalAmountInr,
                orderDate
        );

        return invoice;
    }

    public List<SalesOrder> getAllSalesOrders() {
        return salesOrderRepo.findAllByOrderByIdDesc();
    }

    public List<CustomerInvoice> getAllInvoices() {
        return invoiceRepo.findAllByOrderByIdDesc();
    }

    public CustomerInvoice getInvoiceById(Long id) {
        return invoiceRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Invoice not found: " + id));
    }
}
