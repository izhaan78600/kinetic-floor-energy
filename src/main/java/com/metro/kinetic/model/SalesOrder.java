package com.metro.kinetic.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "sales_orders")
public class SalesOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", insertable = false, updatable = false)
    private PowerGridCustomer customer;

    @Column(name = "order_date", nullable = false)
    private LocalDate orderDate;

    @Column(name = "energy_kwh", nullable = false)
    private Double energyKwh;

    @Column(name = "price_per_kwh", nullable = false)
    private Double pricePerKwh;

    @Column(name = "total_amount_inr", nullable = false)
    private Double totalAmountInr;

    @Column(nullable = false)
    private String status; // Pending, Invoiced, Completed

    public SalesOrder() {
        this.status = "Pending";
    }

    public SalesOrder(Long customerId, LocalDate orderDate, Double energyKwh, Double pricePerKwh, Double totalAmountInr, String status) {
        this.customerId = customerId;
        this.orderDate = orderDate;
        this.energyKwh = energyKwh;
        this.pricePerKwh = pricePerKwh;
        this.totalAmountInr = totalAmountInr;
        this.status = status != null ? status : "Pending";
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public PowerGridCustomer getCustomer() { return customer; }
    public void setCustomer(PowerGridCustomer customer) { this.customer = customer; }

    public LocalDate getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDate orderDate) { this.orderDate = orderDate; }

    public Double getEnergyKwh() { return energyKwh; }
    public void setEnergyKwh(Double energyKwh) { this.energyKwh = energyKwh; }

    public Double getPricePerKwh() { return pricePerKwh; }
    public void setPricePerKwh(Double pricePerKwh) { this.pricePerKwh = pricePerKwh; }

    public Double getTotalAmountInr() { return totalAmountInr; }
    public void setTotalAmountInr(Double totalAmountInr) { this.totalAmountInr = totalAmountInr; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
