package com.metro.kinetic.model;

import jakarta.persistence.*;

@Entity
@Table(name = "power_grid_customers")
public class PowerGridCustomer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_name", nullable = false)
    private String customerName;

    @Column
    private String contact;

    @Column
    private String email;

    @Column
    private String address;

    public PowerGridCustomer() {}

    public PowerGridCustomer(String customerName, String contact, String email, String address) {
        this.customerName = customerName;
        this.contact = contact;
        this.email = email;
        this.address = address;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}
