package com.metro.kinetic.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "payment_type", nullable = false)
    private String paymentType; // CUSTOMER, VENDOR

    @Column(name = "reference_id", nullable = false)
    private Long referenceId;

    @Column(name = "payment_date", nullable = false)
    private LocalDate paymentDate;

    @Column(name = "amount_inr", nullable = false)
    private Double amountInr;

    @Column(name = "payment_method", nullable = false)
    private String paymentMethod; // Bank Transfer, UPI, NEFT, Cash

    @Column(nullable = false)
    private String status; // Completed, Pending

    public Payment() {
        this.paymentMethod = "Bank Transfer";
        this.status = "Completed";
    }

    public Payment(String paymentType, Long referenceId, LocalDate paymentDate, Double amountInr, String paymentMethod, String status) {
        this.paymentType = paymentType;
        this.referenceId = referenceId;
        this.paymentDate = paymentDate;
        this.amountInr = amountInr;
        this.paymentMethod = paymentMethod != null ? paymentMethod : "Bank Transfer";
        this.status = status != null ? status : "Completed";
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPaymentType() { return paymentType; }
    public void setPaymentType(String paymentType) { this.paymentType = paymentType; }

    public Long getReferenceId() { return referenceId; }
    public void setReferenceId(Long referenceId) { this.referenceId = referenceId; }

    public LocalDate getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDate paymentDate) { this.paymentDate = paymentDate; }

    public Double getAmountInr() { return amountInr; }
    public void setAmountInr(Double amountInr) { this.amountInr = amountInr; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
