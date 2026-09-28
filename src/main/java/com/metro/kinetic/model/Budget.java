package com.metro.kinetic.model;

import jakarta.persistence.*;

@Entity
@Table(name = "budgets")
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sector_id", nullable = false)
    private Long sectorId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sector_id", insertable = false, updatable = false)
    private StationSector sector;

    @Column(name = "budget_year", nullable = false)
    private Integer budgetYear;

    @Column(name = "planned_revenue_inr", nullable = false)
    private Double plannedRevenueInr;

    @Column(name = "planned_maintenance_inr", nullable = false)
    private Double plannedMaintenanceInr;

    @Column(name = "actual_revenue_inr", nullable = false)
    private Double actualRevenueInr;

    @Column(name = "actual_maintenance_inr", nullable = false)
    private Double actualMaintenanceInr;

    public Budget() {
        this.plannedRevenueInr = 0.0;
        this.plannedMaintenanceInr = 0.0;
        this.actualRevenueInr = 0.0;
        this.actualMaintenanceInr = 0.0;
    }

    public Budget(Long sectorId, Integer budgetYear, Double plannedRevenueInr, Double plannedMaintenanceInr, Double actualRevenueInr, Double actualMaintenanceInr) {
        this.sectorId = sectorId;
        this.budgetYear = budgetYear;
        this.plannedRevenueInr = plannedRevenueInr != null ? plannedRevenueInr : 0.0;
        this.plannedMaintenanceInr = plannedMaintenanceInr != null ? plannedMaintenanceInr : 0.0;
        this.actualRevenueInr = actualRevenueInr != null ? actualRevenueInr : 0.0;
        this.actualMaintenanceInr = actualMaintenanceInr != null ? actualMaintenanceInr : 0.0;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSectorId() { return sectorId; }
    public void setSectorId(Long sectorId) { this.sectorId = sectorId; }

    public StationSector getSector() { return sector; }
    public void setSector(StationSector sector) { this.sector = sector; }

    public Integer getBudgetYear() { return budgetYear; }
    public void setBudgetYear(Integer budgetYear) { this.budgetYear = budgetYear; }

    public Double getPlannedRevenueInr() { return plannedRevenueInr; }
    public void setPlannedRevenueInr(Double plannedRevenueInr) { this.plannedRevenueInr = plannedRevenueInr; }

    public Double getPlannedMaintenanceInr() { return plannedMaintenanceInr; }
    public void setPlannedMaintenanceInr(Double plannedMaintenanceInr) { this.plannedMaintenanceInr = plannedMaintenanceInr; }

    public Double getActualRevenueInr() { return actualRevenueInr; }
    public void setActualRevenueInr(Double actualRevenueInr) { this.actualRevenueInr = actualRevenueInr; }

    public Double getActualMaintenanceInr() { return actualMaintenanceInr; }
    public void setActualMaintenanceInr(Double actualMaintenanceInr) { this.actualMaintenanceInr = actualMaintenanceInr; }
}
