package com.metro.kinetic.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "vendor_bills")
public class VendorBill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vendor_id", nullable = false)
    private Long vendorId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vendor_id", insertable = false, updatable = false)
    private TileVendor vendor;

    @Column(name = "bill_date", nullable = false)
    private LocalDate billDate;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "amount_inr", nullable = false)
    private Double amountInr;

    @Column(nullable = false)
    private String status; // Unpaid, Paid

    public VendorBill() {
        this.quantity = 1;
        this.status = "Unpaid";
    }

    public VendorBill(Long vendorId, LocalDate billDate, String description, Integer quantity, Double amountInr, String status) {
        this.vendorId = vendorId;
        this.billDate = billDate;
        this.description = description;
        this.quantity = quantity != null ? quantity : 1;
        this.amountInr = amountInr;
        this.status = status != null ? status : "Unpaid";
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getVendorId() { return vendorId; }
    public void setVendorId(Long vendorId) { this.vendorId = vendorId; }

    public TileVendor getVendor() { return vendor; }
    public void setVendor(TileVendor vendor) { this.vendor = vendor; }

    public LocalDate getBillDate() { return billDate; }
    public void setBillDate(LocalDate billDate) { this.billDate = billDate; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Double getAmountInr() { return amountInr; }
    public void setAmountInr(Double amountInr) { this.amountInr = amountInr; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
