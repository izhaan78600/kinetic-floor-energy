package com.metro.kinetic.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tile_vendors")
public class TileVendor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vendor_name", nullable = false)
    private String vendorName;

    @Column
    private String contact;

    @Column
    private String email;

    @Column
    private String address;

    public TileVendor() {}

    public TileVendor(String vendorName, String contact, String email, String address) {
        this.vendorName = vendorName;
        this.contact = contact;
        this.email = email;
        this.address = address;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getVendorName() { return vendorName; }
    public void setVendorName(String vendorName) { this.vendorName = vendorName; }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}
