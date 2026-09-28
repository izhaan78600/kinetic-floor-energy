package com.metro.kinetic.model;

import jakarta.persistence.*;

@Entity
@Table(name = "kinetic_tiles")
public class KineticTile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tile_name", nullable = false)
    private String tileName;

    @Column(name = "tile_type")
    private String tileType; // Piezoelectric, Electromagnetic, Hybrid

    @Column(name = "price_inr", nullable = false)
    private Double priceInr;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private String status; // Active, Maintenance, Reserved

    public KineticTile() {
        this.quantity = 0;
        this.priceInr = 0.0;
        this.status = "Active";
    }

    public KineticTile(String tileName, String tileType, Double priceInr, Integer quantity, String status) {
        this.tileName = tileName;
        this.tileType = tileType;
        this.priceInr = priceInr;
        this.quantity = quantity;
        this.status = status != null ? status : "Active";
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTileName() { return tileName; }
    public void setTileName(String tileName) { this.tileName = tileName; }

    public String getTileType() { return tileType; }
    public void setTileType(String tileType) { this.tileType = tileType; }

    public Double getPriceInr() { return priceInr; }
    public void setPriceInr(Double priceInr) { this.priceInr = priceInr; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
