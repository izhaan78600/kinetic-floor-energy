package com.metro.kinetic.model;

import jakarta.persistence.*;

@Entity
@Table(name = "station_sectors")
public class StationSector {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "station_name", nullable = false)
    private String stationName;

    @Column(name = "sector_name", nullable = false)
    private String sectorName;

    @Column
    private String location;

    @Column(name = "tile_count", nullable = false)
    private Integer tileCount;

    public StationSector() {
        this.tileCount = 0;
    }

    public StationSector(String stationName, String sectorName, String location, Integer tileCount) {
        this.stationName = stationName;
        this.sectorName = sectorName;
        this.location = location;
        this.tileCount = tileCount != null ? tileCount : 0;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getStationName() { return stationName; }
    public void setStationName(String stationName) { this.stationName = stationName; }

    public String getSectorName() { return sectorName; }
    public void setSectorName(String sectorName) { this.sectorName = sectorName; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Integer getTileCount() { return tileCount; }
    public void setTileCount(Integer tileCount) { this.tileCount = tileCount; }
}
