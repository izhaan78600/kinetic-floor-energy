package com.metro.kinetic.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "telemetry")
public class Telemetry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sector_id", nullable = false)
    private Long sectorId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sector_id", insertable = false, updatable = false)
    private StationSector sector;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "step_count", nullable = false)
    private Long stepCount;

    @Column(name = "energy_kwh", nullable = false)
    private Double energyKwh;

    public Telemetry() {}

    public Telemetry(Long sectorId, LocalDate date, Long stepCount, Double energyKwh) {
        this.sectorId = sectorId;
        this.date = date;
        this.stepCount = stepCount;
        this.energyKwh = energyKwh;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSectorId() { return sectorId; }
    public void setSectorId(Long sectorId) { this.sectorId = sectorId; }

    public StationSector getSector() { return sector; }
    public void setSector(StationSector sector) { this.sector = sector; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public Long getStepCount() { return stepCount; }
    public void setStepCount(Long stepCount) { this.stepCount = stepCount; }

    public Double getEnergyKwh() { return energyKwh; }
    public void setEnergyKwh(Double energyKwh) { this.energyKwh = energyKwh; }
}
