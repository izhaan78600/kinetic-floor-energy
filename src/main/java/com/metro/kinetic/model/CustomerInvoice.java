package com.metro.kinetic.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "customer_invoices")
public class CustomerInvoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sales_order_id", nullable = false)
    private Long salesOrderId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sales_order_id", insertable = false, updatable = false)
    private SalesOrder salesOrder;

    @Column(name = "invoice_date", nullable = false)
    private LocalDate invoiceDate;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "amount_inr", nullable = false)
    private Double amountInr;

    @Column(nullable = false)
    private String status; // Unpaid, Paid

    public CustomerInvoice() {
        this.status = "Unpaid";
    }

    public CustomerInvoice(Long salesOrderId, LocalDate invoiceDate, LocalDate dueDate, Double amountInr, String status) {
        this.salesOrderId = salesOrderId;
        this.invoiceDate = invoiceDate;
        this.dueDate = dueDate;
        this.amountInr = amountInr;
        this.status = status != null ? status : "Unpaid";
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSalesOrderId() { return salesOrderId; }
    public void setSalesOrderId(Long salesOrderId) { this.salesOrderId = salesOrderId; }

    public SalesOrder getSalesOrder() { return salesOrder; }
    public void setSalesOrder(SalesOrder salesOrder) { this.salesOrder = salesOrder; }

    public LocalDate getInvoiceDate() { return invoiceDate; }
    public void setInvoiceDate(LocalDate invoiceDate) { this.invoiceDate = invoiceDate; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public Double getAmountInr() { return amountInr; }
    public void setAmountInr(Double amountInr) { this.amountInr = amountInr; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
